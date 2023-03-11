package com.julihe.orderPad.util;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.Map;

public class MainThreadHandler {
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static final Map<String, Runnable> map = new HashMap<>();

    public static Handler getHandler() {
        return handler;
    }

    public static void postDelayed(String tag, Runnable runnable, long delayMillis) {
        Runnable oldRunnable = map.get(tag);
        if (oldRunnable != null) {
            handler.removeCallbacks(oldRunnable);
        }
        map.put(tag, runnable);
        handler.postDelayed(runnable, delayMillis);
    }

    public static void postDelayed(Runnable runnable, long delayMillis) {
        handler.postDelayed(runnable, delayMillis);
    }

    public static void removeCallbacks(String tag) {
        Runnable runnable = map.get(tag);
        if (runnable != null) {
            handler.removeCallbacks(runnable);
            map.remove(tag);
        }
    }

    public static void removeAllCallbacks() {
        handler.removeCallbacksAndMessages(null);
        map.clear();
    }
}