package com.jeryzhang.statusbar.statusbaradaptor;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    public StatusBarHelper statusBarHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusBarHelper = new StatusBarHelper(this)
                .translucentStatusBar()
                .immersionStatusBar()
                .statusBarId(getStatusBarId());
        if (isLightStatusBar()) {
            setLightStatusBar();
        } else {
            setDarkStatusBar();
        }
    }

    public int getStatusBarId() {
        return 0;
    }

    public boolean isLightStatusBar() {
        return true;
    }



    private void setLightStatusBar() {
        statusBarHelper.setLightStatusBar();
    }

    private void setDarkStatusBar() {
        statusBarHelper.setDarkStatusBar();
    }


}