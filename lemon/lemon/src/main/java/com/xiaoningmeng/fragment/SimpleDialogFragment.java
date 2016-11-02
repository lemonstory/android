package com.xiaoningmeng.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoningmeng.R;
import com.xiaoningmeng.base.BaseDialogFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by huang on 2016/9/26.
 */

public class SimpleDialogFragment extends BaseDialogFragment implements   DialogInterface.OnShowListener{
    RecyclerView recyclerView;
    SimpleAdapter mAdapter;
    Callback mCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<String>  l = new ArrayList<>();
        for(int i = 0 ;i < 10;i++){
            l.add("item"+i);
        }
        mAdapter = new SimpleAdapter(l);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.mp_play_list_dialog_add_to)
                .setView(R.layout.dialog_simple)
                .setNegativeButton(R.string.mp_cancel, null)
                .create();
        dialog.setOnShowListener(this);
        return dialog;
    }



    @Override
    public void onShow(DialogInterface dialog) {
        resizeDialogSize();
        if (recyclerView == null) {
            recyclerView = (RecyclerView) getDialog().findViewById(R.id.recycler_view);
            recyclerView.setAdapter(mAdapter);
        }
    }


    public SimpleDialogFragment setCallback(Callback callback) {
        mCallback = callback;
        return this;
    }



    public interface Callback {
        void onPlayListSelected();
    }

    public static class SimpleAdapter extends BaseQuickAdapter<String>{

        public SimpleAdapter( List<String> data) {
            super(R.layout.item_address, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            helper.setText(R.id.tv_address_title,item);
        }
    }
}
