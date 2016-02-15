/*
 * Copyright 2014 Hieu Rocker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rockerhieu.emojicon;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.rockerhieu.emojicon.emoji.Emojicon;

import java.util.List;

/**
 * @author Hieu Rocker (rockerhieu@gmail.com)
 */
class EmojiAdapter extends ArrayAdapter<Emojicon>{
    private boolean mUseSystemDefault = false;

    public EmojiAdapter(Context context, List<Emojicon> data) {
        super(context, R.layout.emojicon_item, data);
        mUseSystemDefault = false;
    }

    public EmojiAdapter(Context context, List<Emojicon> data, boolean useSystemDefault) {
        super(context, R.layout.emojicon_item, data);
        mUseSystemDefault = useSystemDefault;
    }

    public EmojiAdapter(Context context, Emojicon[] data) {
        super(context, R.layout.emojicon_item, data);
        mUseSystemDefault = false;
    }

    public EmojiAdapter(Context context, Emojicon[] data, boolean useSystemDefault) {
        super(context, R.layout.emojicon_item, data);
        mUseSystemDefault = useSystemDefault;
    }

    public int getCount() {

        return  super.getCount() + 1;
    }


    //TODO:getView初始状态下会跑很多次posion=0,原因待查
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //View v = convertView;
        ViewHolder holder = null;
        //右下角的删除按钮，除非必要可以不需要
        if(position  == getCount() - 1) {
            View v = View.inflate(getContext(), R.layout.emojicon_keyboard_delete, null);
            return v;
        }else {
            if (convertView == null) {

                holder = new ViewHolder();
                convertView = View.inflate(getContext(), R.layout.emojicon_item, null);
                holder.icon = (EmojiconTextView) convertView.findViewById(R.id.emojicon_icon);
                holder.icon.setUseSystemDefault(mUseSystemDefault);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            //TODO:会出现空指针,原因待查 java.lang.NullPointerException
            if (holder != null) {

                Emojicon emoji = getItem(position);
                holder.icon.setText(emoji.getEmoji());
            }
        }
        return convertView;
    }

    static class ViewHolder {
        EmojiconTextView icon;
    }
}