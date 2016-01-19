package com.xiaoningmeng;

import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.http.LHttpHandler;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.utils.TelUtil;
import com.xiaoningmeng.view.dialog.TipDialog;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

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
				LHttpRequest.getInstance().feedbackReq(this, qq,tel,feedBackStr, new LHttpHandler<String>(this) {
					
					@Override
					public void onGetDataSuccess(String data) {
						Toast.makeText(FeedBackActivity.this,"感谢您反馈的宝贵意见",Toast.LENGTH_SHORT).show();
						finish(); 
						
					}
				});
			} else {
				new TipDialog.Builder(this).setAutoDismiss(true).setTransparent(false).setTipText("您还没有输入任何文字...").create().show();
			}
		}
	}
}
