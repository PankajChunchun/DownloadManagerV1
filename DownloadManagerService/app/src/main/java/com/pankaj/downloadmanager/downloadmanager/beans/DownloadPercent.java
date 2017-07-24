package com.pankaj.downloadmanager.downloadmanager.beans;

/**
 * Model class to handle status of downloading process
 * while user is present on UI.
 *
 * This is being used in observer and to update current status of download progress
 * on UI.
 *
 * Created by Pankaj Kumar on 7/15/2017.
 * pankaj.arrah@gmail.com
 */
public class DownloadPercent {
    private long mDownloadingId;
    private int mPercent;
    private int mStatus;

    public int getPercent() {
        return mPercent;
    }

    public void setPercent(int percent) {
        this.mPercent = percent;
    }

    public int getDownloadStatus() {
        return mStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.mStatus = downloadStatus;
    }

    public long getDownloadingId() {
        return mDownloadingId;
    }

    public void setDownloadingId(long downloadingId) {
        this.mDownloadingId = downloadingId;
    }

    @Override
    public String toString() {
        return "DownloadPercent{" +
                "mPercent=" + mPercent +
                ", mStatus=" + mStatus +
                ", mDownloadingId=" + mDownloadingId +
                '}';
    }
}
