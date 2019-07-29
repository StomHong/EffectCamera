package com.gpufast.utils;

import android.util.Log;

import androidx.annotation.NonNull;

public class ELog {

    private static final String ELOG_TAG = "effectLib_:";

    private ELog() {
    }

    public static void i(@NonNull Object obj, String msg) {
        i(obj.getClass(), msg);
    }

    public static void i(@NonNull Class<?> cls, String msg) {
        i(cls.getSimpleName(), msg);
    }

    public static void i(String tag, String msg) {
        Log.i(tag, ELOG_TAG + msg);
    }


    public static void d(@NonNull Object obj, String msg) {
        d(obj.getClass(), msg);
    }

    public static void d(@NonNull Class<?> cls, String msg) {
        d(cls.getSimpleName(), msg);
    }

    public static void d(String tag, String msg) {
        Log.d(tag, ELOG_TAG + msg);
    }


    public static void w(@NonNull Object obj, String msg) {
        w(obj.getClass(), msg);
    }

    public static void w(@NonNull Class<?> cls, String msg) {
        w(cls.getSimpleName(), msg);
    }

    public static void w(String tag, String msg) {
        Log.w(tag, ELOG_TAG + msg);
    }


    public static void e(@NonNull Object obj, String msg) {
        e(obj.getClass(), msg);
    }

    public static void e(@NonNull Object obj, String msg, Throwable e) {
        e(obj.getClass(), msg, e);
    }

    public static void e(@NonNull Class<?> cls, String msg) {
        e(cls.getSimpleName(), msg);
    }

    public static void e(@NonNull Class<?> cls, String msg, Throwable e) {
        e(cls.getSimpleName(), msg, e);
    }

    public static void e(String tag, String msg) {
        Log.e(tag, ELOG_TAG + msg);
    }

    public static void e(String tag, String msg, Throwable e) {
        Log.e(tag, ELOG_TAG + msg, e);
    }
}
