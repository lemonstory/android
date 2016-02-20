package com.xiaoningmeng;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.base.BasePohotoActivity;
import com.xiaoningmeng.constant.Constant;
import com.xiaoningmeng.http.LHttpHandler;
import com.xiaoningmeng.http.LHttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class NewThreadActivity extends BasePohotoActivity implements View.OnClickListener,BasePohotoActivity.IUploadCall {


    private Context mContext;
    private TextView rightTv;
    private EditText subjectEt;
    private EditText messageEt;
    private LinearLayout addedImageContainerLl;

    private TextWatcher textChangeListener;
    private ImageView ivAddImageControl;

    public final static int MAX_ADDED_IMAGE_COUNT = 3;
    public final static String ADDED_IMAGE_TAG = "added_image";
    private ArrayList<File> addedImageFiles;
    private String hash;
    private String formHash;
    private int fid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_new_thread);
        initView();
    }

    private void initView() {

        rightTv = (TextView) findViewById(R.id.tv_head_right);
        subjectEt = (EditText) findViewById(R.id.et_subject);
        messageEt = (EditText) findViewById(R.id.et_message);
        addedImageContainerLl = (LinearLayout) findViewById(R.id.ll_added_image_container);
        ivAddImageControl = (ImageView) findViewById(R.id.iv_add_image_control);

        setTitleName("发布帖子");
        setRightHeadText("发送");
        subjectEt.setHint("回复楼主:");
        rightTv.setAlpha((float) 0.5);

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
        RequestQueue mRequestQueue = MyApplication.getInstance().getmRequestQueue();

        if (subjectEt.getText().length() == 0) {

            Toast.makeText(mContext,"请输入标题",Toast.LENGTH_SHORT).show();
        }

        if(messageEt.getText().length() == 0) {

            Toast.makeText(mContext,"没有输入任何内容",Toast.LENGTH_SHORT).show();
        }

        //构建一个请求队列,先逐个上传图片,在上传文字
        if(addedImageFiles.size() > 0) {

            for (File file: addedImageFiles) {

                File fileData = file;

                //上传图片
                LHttpRequest.getInstance().forumUpload(this,new LHttpHandler<String>(this) {
                    @Override
                    public void onGetDataSuccess(String data) {

                        try {

                            JSONObject jsonObject = new JSONObject(data);
                            JSONObject variablesObject = new JSONObject(jsonObject.getString("Variables"));
                            if(variablesObject.has("ret")) {

                                JSONObject retObject = new JSONObject(variablesObject.getString("ret"));
                                String aId = retObject.getString("aId");
                                String isImage = retObject.getString("image");

                                if(isImage != null && isImage.equals("1") && aId != null && !aId.equals("")) {

                                    aids.add(aId);

                                    if(aids.size() == addedImageFiles.size()) {

                                        commitThreadMessage(aids);
                                    }
                                }
                            }

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
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

                    try {

                        JSONObject jsonObject = new JSONObject(data);
                        JSONObject variablesObject = new JSONObject(jsonObject.getString("Variables"));
                        if (jsonObject.has("Message")) {

                            JSONObject messageObject = new JSONObject(jsonObject.getString("Message"));
                            String messageVal = messageObject.getString("messageval");
                            String messageStr = messageObject.getString("messagestr");

                            if(messageVal != null && messageVal.equals(Constant.FORUM_POST_NEW_THREAD_SUCCEED)) {

                                Toast.makeText(mContext,messageStr,Toast.LENGTH_SHORT).show();
                                finish();

                            } else {

                                Toast.makeText(mContext,messageStr,Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }
            },fid,subject,message,aids,formHash);
        } else {
            //TODO:这个很垃圾,不能有错误提示,待优化
            Toast.makeText(mContext,"请重新登录",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        String tag = (String) v.getTag();

        switch (id) {

            case R.id.tv_head_right:
                commitThread();
                break;

            case R.id.iv_add_image_control:
                addImage();
                break;

            case R.id.add_image_0:
            case R.id.add_image_1:
            case R.id.add_image_2:
                Toast.makeText(this,"clicked",Toast.LENGTH_SHORT).show();
                break;

        }
    }

    private void addImage() {

        selectPic(this,false);
    }

    private int addedImageCount() {

        int count = addedImageContainerLl.getChildCount();
        int isv = ivAddImageControl.getVisibility();
        boolean iss = ivAddImageControl.isShown();

        if(ivAddImageControl.getVisibility() == View.VISIBLE) {
            count = count -1;
        }
        return count;
    }

    private void checkAddImageControlVisibility() {

        if(addedImageCount() >= MAX_ADDED_IMAGE_COUNT) {

            ivAddImageControl.setVisibility(View.GONE);
        }
    }

    @Override
    public void gpuback(File file) {

        //uploadAvatar(file.getAbsolutePath());

        addedImageFiles.add(file);
        ImageView imageView = new ImageView(this);
        imageView.setImageURI(Uri.fromFile(file));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setClickable(true);
        imageView.setOnClickListener(this);
        imageView.setFocusable(true);
        imageView.setLongClickable(false);

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)ivAddImageControl.getLayoutParams();

        int addedImageCount = addedImageCount();
        int id = 0;
        switch (addedImageCount) {

            case 0:
                id = R.id.add_image_0;
                lp.setMargins(0, 0, 10, 0);
                break;
            case 1:
                id = R.id.add_image_1;
                lp.setMargins(0, 0, 10, 0);
                break;
            case 2:
                id = R.id.add_image_2;
                break;
        }

        imageView.setLayoutParams(lp);
        imageView.setId(id);
        imageView.setTag(id,ADDED_IMAGE_TAG);

        addedImageContainerLl.addView(imageView,addedImageCount);
        checkAddImageControlVisibility();
    }
}
