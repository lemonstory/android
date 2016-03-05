package com.xiaoningmeng;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoningmeng.base.BaseFragmentActivity;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.fragment.AddedImageFragment;
import com.xiaoningmeng.fragment.KeyboardFragment;
import com.xiaoningmeng.http.LHttpHandler;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.utils.AppUtils;
import com.xiaoningmeng.utils.FileUtils;
import com.xiaoningmeng.utils.ImageUtils;
import com.xiaoningmeng.utils.UiUtils;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class NewThreadActivity extends BaseFragmentActivity implements View.OnClickListener,KeyboardFragment.OnFragmentInteractionListener,AddedImageFragment.OnAddedImgListener {

    private Context mContext;
    private TextView rightTv;
    private EditText subjectEt;
    private EditText messageEt;
    private TextWatcher textChangeListener;
    private ArrayList<File> addedImageFiles;
    private String hash;
    private String formHash;
    private int fid;
    private AddedImageFragment addedImageFragment;
    private String SAVED_IMAGE_DIR_PATH = Environment
            .getExternalStorageDirectory() + "/DCIM/Camera";// 图片的存储目录

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_new_thread);
        initView();
    }

    private void setAddedImageFragment() {

        addedImageFragment = AddedImageFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_added_image, addedImageFragment,"addedImageFragment")
                .commit();
        addedImageFragment.setOnAddedImgListener(this);
    }

    private void initView() {

        rightTv = (TextView) findViewById(R.id.tv_head_right);
        subjectEt = (EditText) findViewById(R.id.et_subject);
        messageEt = (EditText) findViewById(R.id.et_message);
        setTitleName("发布帖子");
        setRightHeadText("发送");
        subjectEt.setHint("请输入帖子标题");
        rightTv.setAlpha((float) 0.5);
        setAddedImageFragment();
        textChangeListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (subjectEt.getText().length() > 0 && messageEt.getText().length() > 0) {

                    rightTv.setAlpha(1);

                } else {

                    rightTv.setAlpha((float) 0.5);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        subjectEt.addTextChangedListener(textChangeListener);
        messageEt.addTextChangedListener(textChangeListener);
        fid = this.getIntent().getIntExtra("fid", 0);
        hash = this.getIntent().getStringExtra("hash");
        formHash = this.getIntent().getStringExtra("formhash");
        addedImageFiles = new ArrayList<>();
    }


    private void commitThread() {

        final ArrayList<String>aids = new ArrayList<>();;//dz上传图片后返回的图片标示符
        //构建一个请求队列,先逐个上传图片,在上传文字
        if(addedImageFiles.size() > 0) {

            for (File file: addedImageFiles) {

                String compressFilePath =  ImageUtils.compress(file.getAbsolutePath(), Bitmap.CompressFormat.JPEG, 80);
                final File fileData = new File(compressFilePath);
                //上传图片
                LHttpRequest.getInstance().forumUpload(this,new LHttpHandler<String>(this) {
                    @Override
                    public void onGetDataSuccess(String data) {
                        FileUtils.deleteFile(fileData);
                        try {

                            JSONObject jsonObject = new JSONObject(data);
                            JSONObject variablesObject = new JSONObject(jsonObject.getString("Variables"));
                            if(variablesObject.has("ret")) {

                                JSONObject retObject = new JSONObject(variablesObject.getString("ret"));
                                String aId = retObject.getString("aId");
                                String isImage = retObject.getString("image");
                                String code = variablesObject.getString("code");

                                if(isImage != null && isImage.equals("1") && aId != null && !aId.equals("") && !aId.equals("0") && code.equals("0")) {

                                    aids.add(aId);
                                    if(aids.size() == addedImageFiles.size()) {

                                        commitThreadMessage(aids);
                                    }
                                }else {

                                    Toast.makeText(mContext,"图片发布失败 ：(",Toast.LENGTH_SHORT).show();
                                    commitThreadMessage(null);
                                }
                            }

                        } catch (JSONException e) {

                            stopLoading();
                            Toast.makeText(mContext,"系统错误",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        stopLoading();
                        Toast.makeText(mContext,"请检查网络设置",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {

                        super.onFinish();
                        stopLoading();
                    }

                },fileData,hash);
            }
        }else {

            commitThreadMessage(null);
        }
    }

    //上传文字
    private void commitThreadMessage(ArrayList<String>aids) {

        if (fid != 0 && formHash != null && !formHash.equals("")) {
            String subject = subjectEt.getText().toString();
            String message = messageEt.getText().toString();
            LHttpRequest.getInstance().newThread(this, new LHttpHandler<String>(this) {
                @Override
                public void onGetDataSuccess(String data) {

                    stopLoading();
                    try {

                        JSONObject jsonObject = new JSONObject(data);
                        if (jsonObject.has("Message")) {

                            JSONObject messageObject = new JSONObject(jsonObject.getString("Message"));
                            String messageVal = messageObject.getString("messageval");
                            String messageStr = messageObject.getString("messagestr");

                            if(messageVal != null && messageVal.equals(Constant.FORUM_POST_NEW_THREAD_SUCCEED)) {
                                NewThreadActivity.this.setResult(RESULT_OK);
                                finish();
                                //Toast.makeText(mContext,messageStr,Toast.LENGTH_SHORT).show();
                            } else {

                                Toast.makeText(mContext,messageStr,Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e) {

                        Toast.makeText(mContext,"系统错误",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    stopLoading();
                    Toast.makeText(mContext,"请检查网络设置",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinish() {

                    super.onFinish();
                    stopLoading();
                }



            },fid,subject,message,aids,formHash);
        } else {
            //TODO:这个很垃圾,不能有错误提示,待优化
            stopLoading();
            Toast.makeText(mContext,"请重新登录",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.tv_head_right:
                setLoadingTip("正在发布");
                if (subjectEt.getText().length() == 0) {

                    Toast.makeText(mContext,"请输入标题",Toast.LENGTH_SHORT).show();
                }
                if(messageEt.getText().length() == 0) {

                    Toast.makeText(mContext,"没有输入任何内容",Toast.LENGTH_SHORT).show();
                }
                startLoading();
                commitThread();
                break;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void OnAddedImgInContainer(ArrayList<File> imagesfiles) {

        this.addedImageFiles = imagesfiles;
    }

    @Override
    public void onAddImageControlClick(View view) {

        boolean isKeyboardVisible = UiUtils.isKeyboardShown(subjectEt.getRootView());
        if(isKeyboardVisible) {
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            AppUtils.hiddenKeyboard(this);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        this.addedImageFragment.onActivityResult(requestCode,resultCode,data);
    }


}
