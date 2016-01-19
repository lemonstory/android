package com.xiaoningmeng;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.xiaoningmeng.application.MyApplication;
import com.xiaoningmeng.auth.UserAuth;
import com.xiaoningmeng.base.BaseActivity;
import com.xiaoningmeng.bean.Comment;
import com.xiaoningmeng.bean.UserInfo;
import com.xiaoningmeng.http.LHttpHandler;
import com.xiaoningmeng.http.LHttpRequest;
import com.xiaoningmeng.view.RatingBar;
import com.xiaoningmeng.view.RatingBar.OnRatingChangeListener;
import com.xiaoningmeng.view.dialog.TipDialog;

public class CommentActivity extends BaseActivity implements OnClickListener {

	private RatingBar mRatingBar;
	private EditText mRatingEt;
	private String albumId;
	private int starLevel = 0;
	private AtomicBoolean isReq = new AtomicBoolean();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rate);
		mRatingBar = (RatingBar) findViewById(R.id.rb_ablum_detail_rate);
		mRatingEt = (EditText) findViewById(R.id.et_rating);
		mRatingEt.setImeOptions(EditorInfo.IME_ACTION_SEND);  
		mRatingEt.setInputType(EditorInfo.TYPE_CLASS_TEXT);  
		albumId = getIntent().getStringExtra("albumId");
		setTitleName("发表评论");
		mRatingBar.setOnRatingChangeListener(new OnRatingChangeListener() {

			@Override
			public void onRatingChange(int RatingCount) {
				starLevel = RatingCount;

			}
		});
		mRatingEt.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					commentAlbum();
					closeInput();
					return true;
				}
				return false;
			}
		});
	}

	
	public  void closeInput(){
		InputMethodManager inputMethodManager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(mRatingEt.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_rating_submit:
			commentAlbum();
			break;

		default:
			break;
		}
	}

	private void commentAlbum() {
		if(UserAuth.auditUser(this, "登录后,才能评论故事喔.")){
			String commentContent = mRatingEt.getText().toString();
			int length = commentContent.trim().length();
			if (length == 0) {
				new TipDialog.Builder(this).setAutoDismiss(true)
					.setTransparent(true).setTipText("您还没有输入文字呢").create().show();
			} else if (length > 140) {
				new TipDialog.Builder(this).setAutoDismiss(true)
					.setTransparent(true).setTipText("字数不能超过140").create()
					.show();
			} else if (starLevel == 0) {
				new TipDialog.Builder(this).setAutoDismiss(true)
					.setTransparent(true).setTipText("您还没有评分").create().show();
			} else {
				uploadAlbumComment(starLevel, commentContent);
			}
		}

	}

	private void uploadAlbumComment(final int starLevel,final String commentContent) {
		if(isReq.get()){
			return;
		}
		isReq.set(true);
		LHttpRequest.getInstance().addCommentReq(this, albumId, commentContent,
				starLevel, new LHttpHandler<String>(this, this) {

			
					@Override
					public void onGetDataSuccess(String data) {
						UserInfo userinfo = MyApplication.getInstance().userInfo;
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			             String time=  sdf.format( new  Date());
						Comment comment = new Comment(userinfo.getUid(), userinfo.getNickname(), starLevel, commentContent, userinfo.getAvatartime(),time);
						Intent i = new Intent();
						i.putExtra("comment", comment);
						setResult(5, i);
						finish();
					}
					
					@Override
					public void onFinish() {
						isReq.set(false);
						super.onFinish();
					}
				});

	}
}
