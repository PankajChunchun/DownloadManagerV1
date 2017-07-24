package com.pankaj.downloadmanager.downloadmanager.factory;

import android.content.Context;
import android.widget.Switch;

import com.pankaj.downloadmanager.downloadmanager.beans.DownloadStatus;

/**
 * Created by Pankaj Kumar on 7/23/2017.
 * pankaj.arrah@gmail.com
 */
public final class DownloadManagerFactory {

    public static IDownloadManager getDownloadManager(Context context, DownloadStatus downloadStatus) {
        switch (downloadStatus) {
            case NEW:
            case QUEUED: // This would not be a valid state while creating object.
                return new DefaultDownloadManager(context);
            case RETRY:
                return new DefaultDownloadManager(context);
            default:
                throw new IllegalArgumentException(downloadStatus + " is Illegal.");
        }
    }
}
