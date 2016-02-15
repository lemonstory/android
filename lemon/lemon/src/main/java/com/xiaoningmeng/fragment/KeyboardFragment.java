package com.xiaoningmeng.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

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
public class KeyboardFragment extends Fragment implements EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EmojiconEditText mEditEmojicon;
    private Button mSwitchBtn;
    private Button mSendContent;
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

        mSwitchBtn = (Button) view.findViewById(R.id.btn_switch);
        mSendContent = (Button) view.findViewById(R.id.btn_send_content);
        mFlemojicons = (FrameLayout) view.findViewById(R.id.emojicons);

        mEditEmojicon.setOnClickListener(btnClicklistener);
        mSwitchBtn.setOnClickListener(btnClicklistener);
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
                case R.id.btn_switch:
                    onSwitchImgClicked((Button) v);
                    break;
                case R.id.btn_send_content:
                    onSendContentClicked((Button) v);
                    break;
                case R.id.editEmojicon:
                    hideEmojicons();
                    mSwitchBtn.setText("表情");
                    break;
            }
        }
    };


    @Override
    public void onEmojiconClicked(Emojicon emojicon) {

        Log.d("onEmojiconClicked", " emojicon classname = " + emojicon.getClass().getName());

        if (emojicon.getClass().getName().equals("ImageButton")) {

            EmojiconsFragment.backspace(mEditEmojicon);

        }else {

            EmojiconsFragment.input(mEditEmojicon, emojicon);
        }
    }


    public void onSwitchImgClicked(Button view) {

        int keyboardHeight = 0;
        int lockHeight = 0;
        //显示表情
        if (isEmojiconHidden) {

            keyboardHeight = UiUtils.getKeyboardHeight(getActivity());
            AppUtils.hiddenKeyboard(getActivity());
            mFlemojicons.setVisibility(View.VISIBLE);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            lockHeight = keyboardHeight - 160;
            lockContainerHeight(lockHeight);
            isEmojiconHidden = false;
            view.setText("键盘");
        }else {

            //显示键盘
            RelativeLayout.LayoutParams localLayoutParams = (RelativeLayout.LayoutParams) mFlemojicons.getLayoutParams();
            localLayoutParams.height = mFlemojicons.getTop();
            hideEmojicons();
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            //弹起键盘
            AppUtils.openKeyboard(getActivity());
            view.setText("表情");
        }

        if(mKeyBoardBarViewListener != null){
            mKeyBoardBarViewListener.onSwitchImgClicked(view);
        }
    }

    public void onSendContentClicked(Button view) {

        Log.d("aaa","KeyboardFragment onSendContentClicked is run");
        if(mKeyBoardBarViewListener != null){
            mKeyBoardBarViewListener.OnSendBtnClick(mEditEmojicon.getText().toString());
        }
    }

    public void hideEmojicons() {

        mFlemojicons.setVisibility(View.GONE);
        isEmojiconHidden = true;
    }

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

        public void onSwitchImgClicked(Button view);
        public void OnSendBtnClick(String msg);
    }
}
