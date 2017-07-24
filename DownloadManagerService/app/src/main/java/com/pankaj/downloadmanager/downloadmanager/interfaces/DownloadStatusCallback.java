package com.pankaj.downloadmanager.downloadmanager.interfaces;

import com.pankaj.downloadmanager.downloadmanager.beans.DownloadableObject;

/**
 * An interface to handles callbacks for ongoing download.
 * It helps to handle error while user is on download page,
 * or handle pause, resume actions if these events triggered
 * by user.
 * <p/>
 * Created by Pankaj Kumar on 7/21/2017.
 * pankaj.arrah@gmail.com
 */
public interface DownloadStatusCallback {
    void onDownloadError(DownloadableObject failedObj, int errorCode);

    void onPausedByUser(DownloadableObject failedObj);
}
