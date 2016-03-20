package com.xiaoningmeng.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.xiaoningmeng.R;
import com.xiaoningmeng.utils.AppUtils;
import com.xiaoningmeng.utils.UiUtils;

import java.io.File;
import java.util.ArrayList;

import static com.xiaoningmeng.R.drawable.keyboard_edit_bg;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link KeyboardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link KeyboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KeyboardFragment extends Fragment implements EmojiconGridFragment.OnEmojiconClickedListener,EmojiconGridFragment.OnEmojiconDeleteClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener,AddedImageFragment.OnAddedImgListener {

    EmojiconEditText mEditEmojicon;
    private ImageView mSwitchIv;
    private ImageView mPhotoIv;
    private TextView mSendContent;
    private OnFragmentInteractionListener mListener;
    private boolean isEmojiconHidden;
    private FrameLayout mFlemojicons;
    private FrameLayout mAddedImage;
    public AddedImageFragment addedImageFragment;

    public KeyboardFragment() {
        // Required empty public constructor
    }

    public static KeyboardFragment newInstance() {

        KeyboardFragment fragment = new KeyboardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_keyboard, container, false);
        mEditEmojicon = (EmojiconEditText)view.findViewById(R.id.editEmojicon);
        mEditEmojicon.setBackgroundResource(keyboard_edit_bg);

        mSwitchIv = (ImageView) view.findViewById(R.id.iv_switch);
        mPhotoIv = (ImageView) view.findViewById(R.id.iv_photo);
        mSendContent = (TextView) view.findViewById(R.id.btn_send_content);
        mFlemojicons = (FrameLayout) view.findViewById(R.id.emojicons);
        mAddedImage = (FrameLayout) view.findViewById(R.id.fl_added_image);

        mEditEmojicon.setOnFocusChangeListener(etOnFocusChangeListener);
        mEditEmojicon.setOnClickListener(btnClicklistener);
        mSwitchIv.setOnClickListener(btnClicklistener);
        mPhotoIv.setOnClickListener(btnClicklistener);
        mSendContent.setOnClickListener(btnClicklistener);

        hideEmojicons();
        hideAddImg();
        setEmojiconFragment(false);
        setAddedImageFragment();
        AddedImageFragment.newInstance().setOnAddedImgListener(this);
        return view;
    }

    private void setEmojiconFragment(boolean useSystemDefault) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    private void setAddedImageFragment() {

        addedImageFragment = AddedImageFragment.newInstance();
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_added_image, addedImageFragment, "addedImageFragment")
                .commit();
        addedImageFragment.setOnAddedImgListener(this);
    }

    public View.OnClickListener btnClicklistener = new View.OnClickListener() {

        public void onClick(View v) {

            int id = v.getId();
            switch (id) {
                case R.id.iv_switch:
                    onSwitchImgClicked((ImageView) v);
                    break;
                case R.id.iv_photo:
                    onPhotoImgClicked((ImageView) v);
                    break;
                case R.id.btn_send_content:
                    onSendContentClicked((TextView) v);
                    break;
                case R.id.editEmojicon:
                    hideEmojicons();
                    hideAddImg();
                    mSwitchIv.setImageResource(R.drawable.sent_emotion_normal);
                    break;
            }
        }
    };

    public OnFocusChangeListener etOnFocusChangeListener = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            switch (v.getId()) {

                case R.id.editEmojicon:
                    if (hasFocus) {
                        hideEmojicons();
                        hideAddImg();
                        mSwitchIv.setImageResource(R.drawable.sent_emotion_normal);
                        break;
                    }
            }

        }
    };


    @Override
    public void onEmojiconClicked(Emojicon emojicon) {

        EmojiconsFragment.input(mEditEmojicon, emojicon);
    }

    @Override
    public void onDeleteClicked(View v) {

        EmojiconsFragment.backspace(mEditEmojicon);
    }


    public void onSwitchImgClicked(ImageView view) {

        int keyboardHeight = 0;
        int lockHeight = 0;
        //显示表情
        if (isEmojiconHidden) {
            hideAddImg();
            hideKeyboard();
            keyboardHeight = UiUtils.getKeyboardHeight(getActivity());
            lockHeight = keyboardHeight - 160;
            lockContainerHeight(lockHeight);
            mFlemojicons.setVisibility(View.VISIBLE);
            isEmojiconHidden = false;
        }else {
            hideAddImg();
            //显示键盘
            hideEmojicons();
            RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams) mFlemojicons.getLayoutParams();
            localLayoutParams.height = mFlemojicons.getTop();
            showKeyboard();
        }

        if(mKeyBoardBarViewListener != null){
            mKeyBoardBarViewListener.onSwitchImgClick(view);
        }
    }

    public void onPhotoImgClicked(ImageView view) {

        int keyboardHeight = 0;
        int lockHeight = 0;
        hideEmojicons();
        hideKeyboard();
        showAddImg();
        keyboardHeight = UiUtils.getKeyboardHeight(getActivity());
        lockHeight = keyboardHeight - 160;
        lockContainerHeight(lockHeight);
    }

    public void onSendContentClicked(TextView view) {

        if(mKeyBoardBarViewListener != null){
            mKeyBoardBarViewListener.OnSendBtnClick(mEditEmojicon.getText().toString());
        }
    }


    //弹起键盘
    public void showKeyboard() {

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        AppUtils.openKeyboard(getActivity());
        mEditEmojicon.setFocusable(true);
        mEditEmojicon.requestFocus();
        mSwitchIv.setImageResource(R.drawable.sent_emotion_normal);
    }

    //收起键盘
    public void hideKeyboard() {

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        AppUtils.hiddenKeyboard(getActivity());
        mSwitchIv.setImageResource(R.drawable.sent_keyboard_normal);
    }


    public void hideEmojicons() {

        mFlemojicons.setVisibility(View.GONE);
        isEmojiconHidden = true;
    }

    public void showAddImg() {

        if (mAddedImage != null) {
            mAddedImage.setVisibility(View.VISIBLE);
        }
    }

    public void hideAddImg() {

        if (mAddedImage != null) {
            mAddedImage.setVisibility(View.GONE);
        }
    }

    public void resetKeyboard() {

        hideKeyboard();
        hideEmojicons();
        hideAddImg();
        addedImageFragment.removeAddedImages();
        mEditEmojicon.getText().clear();
        mEditEmojicon.setHint(null);
        mEditEmojicon.clearComposingText();
        mSwitchIv.setImageResource(R.drawable.sent_emotion_normal);
    }

    public void setmEditEmojiconHint(String hint) {

        if(null != mEditEmojicon) {
            mEditEmojicon.setHint(hint);
        }
    }

    //UiUtils中有检测键盘状态的工具方法

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(mEditEmojicon);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Override
    public void OnAddedImgInContainer(ArrayList<File> imagesfiles) {

        int count = imagesfiles.size();
        if(mKeyBoardBarViewListener != null){
            mKeyBoardBarViewListener.OnAddedImgInContainer(imagesfiles);
        }
    }

    @Override
    public void onAddImageControlClick(View view) {

        if(mKeyBoardBarViewListener != null){
            mKeyBoardBarViewListener.onAddImageControlClick(view);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void lockContainerHeight(int paramInt) {

        RelativeLayout.LayoutParams mFlemojiconsLayoutParams = (RelativeLayout.LayoutParams) mFlemojicons.getLayoutParams();
        RelativeLayout.LayoutParams mAddedImageLayoutParams = (RelativeLayout.LayoutParams) mAddedImage.getLayoutParams();
        mFlemojiconsLayoutParams.height = paramInt;
        mAddedImageLayoutParams.height = paramInt;
    }

    KeyBoardBarViewListener mKeyBoardBarViewListener;
    public void setOnKeyBoardBarViewListener(KeyBoardBarViewListener l) { this.mKeyBoardBarViewListener = l; }

    public interface KeyBoardBarViewListener {

        public void onSwitchImgClick(ImageView view);
        public void OnSendBtnClick(String msg);
        public void OnAddedImgInContainer(ArrayList<File> imagesfiles);
        public void onAddImageControlClick(View view);
    }
}
