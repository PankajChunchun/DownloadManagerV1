package com.pankaj.downloadmanager.downloadmanager.factory;

import android.content.Context;

import com.pankaj.downloadmanager.downloadmanager.beans.DownloadableObject;
import com.pankaj.downloadmanager.downloadmanager.utils.DMLog;

/**
 * Created by Pankaj Kumar on 7/23/2017.
 * pankaj.arrah@gmail.com
 */
public class CustomDownloadManager implements IDownloadManager {
    private static final String TAG = CustomDownloadManager.class.getSimpleName();
    private Context mContext;

    public CustomDownloadManager(Context context) {
        mContext = context;
    }

    @Override
    public long enqueue(DownloadableObject toDownload) {
        DMLog.d(TAG, "enqueue download " + toDownload.getObjectUrl());
        return 0;
    }
}
