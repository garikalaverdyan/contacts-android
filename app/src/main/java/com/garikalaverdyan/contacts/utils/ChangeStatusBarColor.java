package com.garikalaverdyan.contacts.utils;

import android.content.Context;
import android.os.Build;
import android.view.Window;

import com.garikalaverdyan.contacts.R;

import androidx.core.content.ContextCompat;

public class ChangeStatusBarColor {
    private Window window;
    private Context context;

    public ChangeStatusBarColor(Window window, Context context) {
        this.window = window;
        this.context = context;
        changeStatusBarColor();
    }

    private void changeStatusBarColor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(context, R.color.status_bar_color));
        }
    }
}
