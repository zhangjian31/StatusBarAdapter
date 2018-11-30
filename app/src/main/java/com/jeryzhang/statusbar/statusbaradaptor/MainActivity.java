package com.jeryzhang.statusbar.statusbaradaptor;

import android.os.Bundle;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean isLightStatusBar() {
        return false;
    }

}
