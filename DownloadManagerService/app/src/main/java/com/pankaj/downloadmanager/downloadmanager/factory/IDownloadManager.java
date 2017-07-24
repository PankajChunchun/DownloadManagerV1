package com.pankaj.downloadmanager.downloadmanager.factory;

import com.pankaj.downloadmanager.downloadmanager.beans.DownloadableObject;

/**
 * Created by Pankaj Kumar on 7/23/2017.
 * pankaj.arrah@gmail.com
 */
interface IDownloadManager {
    long enqueue(DownloadableObject toDownload);
}
