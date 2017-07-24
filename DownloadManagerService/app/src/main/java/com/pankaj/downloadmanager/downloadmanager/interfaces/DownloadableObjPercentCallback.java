package com.pankaj.downloadmanager.downloadmanager.interfaces;

import com.pankaj.downloadmanager.downloadmanager.beans.DownloadableObject;

/**
 * An interface for callbacks which is being used to track live status for subscribe
 * emitted {@link DownloadableObject}
 * <p/>
 * Created by Pankaj Kumar on 7/16/2017.
 * pankaj.arrah@gmail.com
 */
public interface DownloadableObjPercentCallback {
    /**
     * Update downloading status on UI.
     *
     * @param downloadableItem - {@link DownloadableObject} with current status.
     */
    void updateDownloadableObject(DownloadableObject downloadableItem);
}
