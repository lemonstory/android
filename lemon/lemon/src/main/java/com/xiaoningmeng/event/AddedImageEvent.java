package com.xiaoningmeng.event;

import java.io.File;

/**
 * Created by gaoyong on 16/2/23.
 */
public class AddedImageEvent {

    public File image;
    public int position;

    public AddedImageEvent(File image, int position) {

        this.image = image;
        this.position = position;
    }

}
