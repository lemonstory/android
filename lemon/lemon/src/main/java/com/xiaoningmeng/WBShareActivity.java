package com.xiaoningmeng;

import com.umeng.socialize.Config;
import com.umeng.socialize.media.WBShareCallBackActivity;

public class WBShareActivity extends WBShareCallBackActivity {


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Config.dialog = null;
    }
}
