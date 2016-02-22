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

import static com.xiaoningmeng.R.drawable.keyboard_edit_bg;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link KeyboardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link KeyboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KeyboardFragment extends Fragment implements EmojiconGridFragment.OnEmojiconClickedListener,EmojiconGridFragment.OnEmojiconDeleteClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EmojiconEditText mEditEmojicon;
    private ImageView mSwitchIv;
    private ImageView mPhotoIv;
    private TextView mSendContent;
    private OnFragmentInteractionListener mListener;
    private boolean isEmojiconHidden;
    private FrameLayout mFlemojicons;



    public KeyboardFragment() {
        // Required empty public constructor
    }

    public static KeyboardFragment newInstance() {
        KeyboardFragment fragment = new KeyboardFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment KeyboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static KeyboardFragment newInstance(String param1, String param2) {
        KeyboardFragment fragment = new KeyboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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

        mEditEmojicon.setOnFocusChangeListener(etOnFocusChangeListener);
        mEditEmojicon.setOnClickListener(btnClicklistener);
        mSwitchIv.setOnClickListener(btnClicklistener);
        mSendContent.setOnClickListener(btnClicklistener);

        hideEmojicons();
        setEmojiconFragment(false);
        return view;
    }

    private void setEmojiconFragment(boolean useSystemDefault) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
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

            hideKeyboard();
            keyboardHeight = UiUtils.getKeyboardHeight(getActivity());
            mFlemojicons.setVisibility(View.VISIBLE);
            lockHeight = keyboardHeight - 160;
            lockContainerHeight(lockHeight);
            isEmojiconHidden = false;

        }else {

            //显示键盘
            hideEmojicons();
            RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams) mFlemojicons.getLayoutParams();
            localLayoutParams.height = mFlemojicons.getTop();
            showKeyboard();
        }

        if(mKeyBoardBarViewListener != null){
            mKeyBoardBarViewListener.onSwitchImgClicked(view);
        }
    }

    public void onPhotoImgClicked(ImageView view) {

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

    public void resetKeyboard() {

        hideKeyboard();
        hideEmojicons();
        mEditEmojicon.getText().clear();
        mEditEmojicon.setHint(null);
        mEditEmojicon.clearComposingText();
        mSwitchIv.setImageResource(R.drawable.sent_emotion_normal);
    }

    public void setmEditEmojiconHint(String hint) {

        mEditEmojicon.setHint(hint);
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
        RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams) mFlemojicons.getLayoutParams();
        localLayoutParams.height = paramInt;
    }

    KeyBoardBarViewListener mKeyBoardBarViewListener;
    public void setOnKeyBoardBarViewListener(KeyBoardBarViewListener l) { this.mKeyBoardBarViewListener = l; }

    public interface KeyBoardBarViewListener {

        public void onSwitchImgClicked(ImageView view);
        public void OnSendBtnClick(String msg);
    }
}
