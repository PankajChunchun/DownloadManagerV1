package com.pankaj.downloadmanager.downloadmanager.utils;

import android.util.Log;

/**
 * Created by Pankaj Kumar on 7/16/2017.
 * pankaj.arrah@gmail.com
 */
public class DMLog {
    private static final boolean LOG_ENABLE = true;

    public static void d(String tag, String message) {
        if (LOG_ENABLE) {
            Log.d(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (LOG_ENABLE) {
            Log.e(tag, message);
        }
    }
}
