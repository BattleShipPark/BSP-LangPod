package com.battleshippark.bsp_langpod.util;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

/**
 */

public class Logger implements Loggable {
    private final String tag;

    public Logger(String tag) {
        this.tag = tag;
    }

    @Override
    public void v(String msg) {
        Log.v(tag, msg);
    }

    @Override
    public void v(String msg, Object... args) {
        Log.v(tag, String.format(msg, args));
    }

    @Override
    public void d(String msg) {
        Log.d(tag, msg);
    }

    @Override
    public void d(String msg, Object... args) {
        Log.d(tag, String.format(msg, args));
    }

    @Override
    public void i(String msg) {
        Log.i(tag, msg);
    }

    @Override
    public void i(String msg, Object... args) {
        Log.i(tag, String.format(msg, args));
    }

    @Override
    public void w(String msg) {
        Log.w(tag, msg);
    }

    @Override
    public void w(String msg, Object... args) {
        Log.w(tag, String.format(msg, args));
    }

    @Override
    public void w(Throwable t) {
        Log.w(tag, t);
        Crashlytics.logException(t);
    }

    @Override
    public void e(String msg) {
        Log.e(tag, msg);
    }

    @Override
    public void e(String msg, Object... args) {
        Log.e(tag, String.format(msg, args));
    }
}
