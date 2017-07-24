package com.pankaj.downloadmanager.downloadmanager.interfaces;

import com.pankaj.downloadmanager.downloadmanager.beans.DownloadableObject;

/**
 * An interface to handles callbacks to start download, set it to inprogress or
 * start next download on completion of current downloading object.
 * <p/>
 * Created by Pankaj Kumar on 7/15/2017.
 * pankaj.arrah@gmail.com
 */
public interface DownloadableObjCallback {
    void onDownloadRequested(DownloadableObject downloadableItem);

    void onDownloadStarted(DownloadableObject downloadableItem);

    void onDownloadComplete();
}
