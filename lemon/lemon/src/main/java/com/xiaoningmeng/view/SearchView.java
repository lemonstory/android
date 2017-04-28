package com.xiaoningmeng.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoningmeng.R;

public class SearchView extends LinearLayout implements OnClickListener {

    private EditText mSearchEt;
    private ImageView mLeftSearchIcon;
    private TextView mCenterHintTv;
    private TextView mCancelTv;
    private ImageView mDelImg;
    private OnSearchViewListener mOnSearchViewListener;
    private boolean isInterceptFoucs;

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void loadView(boolean isOPen) {

        mSearchEt = (EditText) this.findViewById(R.id.et_search);
        mLeftSearchIcon = (ImageView) this.findViewById(R.id.img_search_hint);
        mCenterHintTv = (TextView) this.findViewById(R.id.tv_search_center_hint);
        mCancelTv = (TextView) this.findViewById(R.id.tv_search_cacel);
        mDelImg = (ImageView) this.findViewById(R.id.img_search_del);
        mLeftSearchIcon.setVisibility(View.INVISIBLE);
        mCenterHintTv.setVisibility(View.VISIBLE);
        mCancelTv.setVisibility(View.GONE);
        mDelImg.setVisibility(View.INVISIBLE);
        mDelImg.setOnClickListener(this);
        mCancelTv.setOnClickListener(this);
        this.setOnClickListener(this);
        setSearchTab();
        if (isOPen) {
            this.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mSearchEt.requestFocus();
                    showInput();
                }
            }, 50);
        }
    }

    private void setSearchTab() {

        mSearchEt.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                moveHintAnimation(hasFocus);
            }
        });
        mSearchEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (mSearchEt.getText().toString().length() == 0)
                    mDelImg.setVisibility(View.INVISIBLE);
                else if (mSearchEt.getText().toString().length() > 0 && mSearchEt.hasFocus()) {
                    mDelImg.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mSearchEt.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    String searchCotent = mSearchEt.getText().toString();
                    if (searchCotent.length() == 0) {
                        Toast.makeText(getContext(), "您还没有输入任何文字...",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        if (mOnSearchViewListener != null)
                            mOnSearchViewListener.search(searchCotent);
                    }
                    closeInput();
                    return true;
                }
                return false;
            }
        });
    }

    private void moveHintAnimation(boolean hasFocus) {
        TranslateAnimation animation = null;
        if (hasFocus) {
            animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_PARENT, -0.25f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            animation.setInterpolator(new AccelerateInterpolator());
            animation.setDuration(320);
            animation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mCenterHintTv.clearAnimation();
                    mLeftSearchIcon.setVisibility(View.VISIBLE);
                    mCenterHintTv.setVisibility(View.INVISIBLE);
                    mCancelTv.setVisibility(View.VISIBLE);
                    mSearchEt.setHint(getResources().getString(R.string.search_hint));
                    if (mOnSearchViewListener != null && !isInterceptFoucs)
                        mOnSearchViewListener.showEmpty(false);
                    isInterceptFoucs = false;

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

            });

        } else {
            mLeftSearchIcon.setVisibility(View.INVISIBLE);
            mCancelTv.setVisibility(View.GONE);
            mDelImg.setVisibility(View.INVISIBLE);
            mSearchEt.setHint("");
            mSearchEt.setText("");
            animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -0.25f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            animation.setInterpolator(new DecelerateInterpolator());
            animation.setDuration(300);
            animation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mCenterHintTv.clearAnimation();
                    mCenterHintTv.setVisibility(View.VISIBLE);
                    closeInput();
                    if (mOnSearchViewListener != null)
                        mOnSearchViewListener.showDefault();
                    isInterceptFoucs = false;

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

            });
        }
        mCenterHintTv.startAnimation(animation);
    }


    public interface OnSearchViewListener {

        void search(String searchCotent);

        void showDefault();

        void showEmpty(boolean isShowSearch);
    }


    public void setOnSearchViewListener(OnSearchViewListener onSearchViewListener) {
        this.mOnSearchViewListener = onSearchViewListener;

    }


    @Override
    public void onClick(View v) {
        if (v == this) {
            mSearchEt.requestFocus();
            showInput();
        } else if (v.getId() == R.id.tv_search_cacel) {
            mSearchEt.clearFocus();
            v.requestFocus();
        } else {
            mSearchEt.setText("");
            if (mOnSearchViewListener != null)
                mOnSearchViewListener.showEmpty(false);
        }
    }


    public void closeInput() {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mSearchEt.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void showInput() {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(mSearchEt, InputMethodManager.SHOW_FORCED);
    }


    public boolean checkIsFocus() {
        if (mSearchEt != null && mSearchEt.hasFocus()) {
            mSearchEt.clearFocus();
            mCancelTv.requestFocus();
            return true;
        }
        return false;

    }

    public void search(String content) {
        isInterceptFoucs = true;
        mSearchEt.requestFocus();
        mSearchEt.setText(content);
        if (mOnSearchViewListener != null) {
            mOnSearchViewListener.showEmpty(true);
            mOnSearchViewListener.search(content);
        }

    }
}
