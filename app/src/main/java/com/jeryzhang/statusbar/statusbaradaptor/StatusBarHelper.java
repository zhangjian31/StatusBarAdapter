package com.jeryzhang.statusbar.statusbaradaptor;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

public class StatusBarHelper {
    private Activity activity;
    private int statusBarId;

    public StatusBarHelper(Activity activity) {
        this.activity = activity;
    }

    public StatusBarHelper translucentStatusBar() {
        //国内有些rom如果单纯在style文件中设置windowTranslucentStatus可能导致设置无效,
        //所以在 Activity中再次设置一遍
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = activity.getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        return this;
    }

    private int getStatusBarHeight() {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }


    public StatusBarHelper setDarkStatusBar() {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (FlymeSetStatusBarLightMode(activity.getWindow(), true)) {
                result = true;
            } else if (MIUISetStatusBarLightMode(activity.getWindow(), true)) {
                result = true;
            }
        }
        if (!result && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeDarkStatusBar();
        }
        return this;
    }

    public StatusBarHelper setLightStatusBar() {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (FlymeSetStatusBarLightMode(activity.getWindow(), false)) {
                result = true;
            } else if (MIUISetStatusBarLightMode(activity.getWindow(), false)) {
                result = true;
            }
        }
        if (!result && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeLightStatusBar();
        }
        return this;
    }

    private boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                    if (dark) {
                        changeDarkStatusBar();
                    } else {
                        changeLightStatusBar();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    private void changeDarkStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            decorView.setSystemUiVisibility(option);
        }
    }

    private void changeLightStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
        }
    }


    /**
     * 沉浸式状态栏
     */
    public StatusBarHelper immersionStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //5.0以上
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(option);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            if (statusBarId != 0) {
                View view = activity.findViewById(statusBarId);
                view.getLayoutParams().height = getStatusBarHeight();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// 4.4以上
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        return this;
    }

    public void setStatusBarColor(@ColorInt int color) {
        if (statusBarId != 0) {
            View view = activity.findViewById(statusBarId);
            view.setBackgroundColor(color);
        }
    }

    public StatusBarHelper statusBarId(int id) {
        this.statusBarId = id;
        return this;
    }
}
