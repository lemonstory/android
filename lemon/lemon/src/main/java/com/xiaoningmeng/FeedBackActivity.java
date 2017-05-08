package com.xiaoningmeng;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.http.JsonResponse;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.utils.TelUtil;
import com.xiaoningmeng.view.dialog.TipDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.xiaoningmeng.http.LHttpRequest.mRetrofit;

public class FeedBackActivity extends BaseActivity implements OnClickListener {

	private EditText mFeedbackEt;
	private EditText mQQEt;
	private EditText mTelEt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		setTitleName("意见反馈");
		findViewById(R.id.tv_head_right).setOnClickListener(this);
		mFeedbackEt = (EditText)findViewById(R.id.et_feedback);
		mQQEt = (EditText)findViewById(R.id.et_feedback_qq);
		mTelEt = (EditText)findViewById(R.id.et_feedback_tel);
		mQQEt.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		mTelEt.setInputType(EditorInfo.TYPE_CLASS_PHONE);
	}

	public void onClick(View v) {
		if (v.getId() == R.id.tv_feedback_submit) {
			String qq = mQQEt.getText().toString();
			if(!qq.equals("") && !TelUtil.checkQQ(qq)){
				new TipDialog.Builder(this).setAutoDismiss(true).setTransparent(false).setTipText("QQ格式错误").create().show();
				return;
			}
			String tel = mTelEt.getText().toString();
			if(!tel.equals("") && !TelUtil.isTel(tel)){
				new TipDialog.Builder(this).setAutoDismiss(true).setTransparent(false).setTipText("电话格式错误").create().show();
				return;
			}
			String feedBackStr = mFeedbackEt.getText().toString();
			if (feedBackStr.length() > 0) {

				LHttpRequest.FeedbackRequest searchRequest = mRetrofit.create(LHttpRequest.FeedbackRequest.class);
				Call<JsonResponse<String>> call = searchRequest.getResult(qq, tel, feedBackStr);
				call.enqueue(new Callback<JsonResponse<String>>() {

					@Override
					public void onResponse(Call<JsonResponse<String>> call, Response<JsonResponse<String>> response) {

						if (response.isSuccessful() && response.body().isSuccessful()) {
							Toast.makeText(FeedBackActivity.this, "感谢您反馈的宝贵意见", Toast.LENGTH_SHORT).show();
							finish();
						} else {
                            Logger.e(response.toString());
                        }
					}

					@Override
					public void onFailure(Call<JsonResponse<String>> call, Throwable t) {
                        Logger.e(t.toString());
                    }
				});

			} else {
				new TipDialog.Builder(this).setAutoDismiss(true).setTransparent(false).setTipText("您还没有输入任何文字...").create().show();
			}
		}
	}
}
