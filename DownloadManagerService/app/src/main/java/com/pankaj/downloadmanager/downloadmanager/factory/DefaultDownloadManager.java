package com.pankaj.downloadmanager.downloadmanager.factory;

import android.content.Context;

import com.pankaj.downloadmanager.downloadmanager.beans.DownloadableObject;
import com.pankaj.downloadmanager.downloadmanager.utils.DMLog;

/**
 * Created by Pankaj Kumar on 7/23/2017.
 * pankaj.arrah@gmail.com
 */
public class DefaultDownloadManager implements IDownloadManager {
    private static final String TAG = DefaultDownloadManager.class.getSimpleName();
    private Context mContext;

    public DefaultDownloadManager(Context context) {
        mContext = context;
    }

    @Override
    public long enqueue(DownloadableObject toDownload) {
        DMLog.d(TAG, "enqueue download " + toDownload.getObjectUrl());
        return 0;
    }
}
