package com.xiaoningmeng.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xiaoningmeng.AddedImageViewPager;
import com.xiaoningmeng.R;
import com.xiaoningmeng.base.BasePhotoFragment;
import com.xiaoningmeng.event.AddedImageEvent;
import de.greenrobot.event.EventBus;

import java.io.File;
import java.util.ArrayList;


public class AddedImageFragment extends BasePhotoFragment implements BasePhotoFragment.IUploadCall {

    public final static int MAX_ADDED_IMAGE_COUNT = 3;
    public final static String ADDED_IMAGE_TAG = "added_image";
    private ImageView ivAddImageControl;
    private LinearLayout addedImageContainerLl;
    private ArrayList<File> addedImageFiles;
    private View mView;
    private OnAddedImgListener mListener;


    public AddedImageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddedImageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddedImageFragment newInstance() {

        AddedImageFragment fragment = new AddedImageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addedImageFiles = new ArrayList<>();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int id = v.getId();
            int position = 0;
            Intent i = new Intent(getActivity(),AddedImageViewPager.class);
            i.putExtra("addedImageFiles",addedImageFiles);
            switch (id) {

                case R.id.iv_add_image_control:
                    addImage();
                    mListener.onAddImageControlClick(v);
                    break;

                case R.id.add_image_0:
                    position = 0;
                    i.putExtra("position", position);
                    startActivityForNew(i);
                    break;

                case R.id.add_image_1:
                    position = 1;
                    i.putExtra("position", position);
                    startActivityForNew(i);
                    break;

                case R.id.add_image_2:
                    position = 2;
                    i.putExtra("position", position);
                    startActivityForNew(i);
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_added_image, container, false);
        addedImageContainerLl = (LinearLayout) mView.findViewById(R.id.ll_added_image_container);
        ivAddImageControl = (ImageView) mView.findViewById(R.id.iv_add_image_control);
        EventBus.getDefault().register(this);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle saved) {

        super.onActivityCreated(saved);
        ivAddImageControl.setOnClickListener(onClickListener);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void setOnAddedImgListener(OnAddedImgListener listener) {

        mListener = listener;
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
    public interface OnAddedImgListener {
        // TODO: Update argument type and name
        void OnAddedImgInContainer(ArrayList<File> imagesfiles);
        void onAddImageControlClick(View view);
    }

    private void addImage() {

        selectPic((IUploadCall) this,false);
    }

    private int addedImageCount() {

        int count = addedImageContainerLl.getChildCount();
        count = count -1;
        return count;
    }

    private void checkAddImageControlVisibility() {

        if(addedImageCount() >= MAX_ADDED_IMAGE_COUNT) {

            ivAddImageControl.setVisibility(View.GONE);
        } else {

            ivAddImageControl.setVisibility(View.VISIBLE);
        }
    }

    public void removeAddedImages() {

        int count = addedImageCount();
        if (count > 1) {

            for (int position = 1;position < count;position++) {
                addedImageFiles.remove(position);
                addedImageContainerLl.removeViewAt(position);
            }
        }
    }


    @Override
    public void gpuback(File file) {

        //uploadAvatar(file.getAbsolutePath());
        addedImageFiles.add(file);
        ImageView imageView = new ImageView(getActivity());
        //imageView.setImageURI会出现OutOfMemoryError,参考下面的方案处理
        //参考文档
        //  http://stackoverflow.com/questions/2191407/changing-imageview-content-causes-outofmemoryerror
        if(((BitmapDrawable)imageView.getDrawable())!=null) {
            ((BitmapDrawable)imageView.getDrawable()).getBitmap().recycle();
        }
        imageView.setImageURI(Uri.fromFile(file));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setClickable(true);
        imageView.setOnClickListener(onClickListener);
        imageView.setFocusable(true);
        imageView.setLongClickable(false);

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)ivAddImageControl.getLayoutParams();
        int addedImageCount = addedImageCount();
        int id = 0;
        switch (addedImageCount) {

            case 0:
                id = R.id.add_image_0;
                lp.setMargins(0, 0, 22, 0);
                break;
            case 1:
                id = R.id.add_image_1;
                lp.setMargins(0, 0, 22, 0);
                break;
            case 2:
                id = R.id.add_image_2;
                break;
        }

        imageView.setLayoutParams(lp);
        imageView.setId(id);
        imageView.setTag(id, ADDED_IMAGE_TAG);

        addedImageContainerLl.addView(imageView, addedImageCount);
        checkAddImageControlVisibility();
        mListener.OnAddedImgInContainer(addedImageFiles);
        imageView.invalidate();
    }

    public void onEventMainThread(AddedImageEvent addedImageEvent) {

        int a = addedImageContainerLl.getChildCount();
        int position = addedImageEvent.position;
        addedImageFiles.remove(position);
        addedImageContainerLl.removeViewAt(position);
        int b = addedImageContainerLl.getChildCount();
        checkAddImageControlVisibility();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
