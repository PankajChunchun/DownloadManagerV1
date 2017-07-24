package com.pankaj.downloadmanager.downloadmanager.beans;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Model class which represents a web url, which holds status about current progress of download
 * or downloaded status {@link DownloadStatus}.
 * <p/>
 * This model is also being used to store url information in database.
 * <p/>
 * Created by Pankaj Kumar on 7/15/2017.
 * pankaj.arrah@gmail.com
 */
// This same model can be used to store url information using Room.
@DatabaseTable
public class DownloadableObject implements Serializable {
    @DatabaseField(generatedId = true)
    private int mId;
    private long mDmId;
    private DownloadStatus mDownloadStatus;
    private String mDownloadUrl;
    private int mDownloadedPercent;
    private int mLastEmittedDownloadedPercent = -1;

    public DownloadableObject() {

    }

    public DownloadableObject(int id, long dmId, DownloadStatus downloadStatus, String downloadUrl, int downloadPercent, int lastEmittedDownloadPercent) {
        this.mId = id;
        this.mDmId = dmId;
        this.mDownloadStatus = downloadStatus;
        this.mDownloadUrl = downloadUrl;
        this.mDownloadedPercent = downloadPercent;
        this.mLastEmittedDownloadedPercent = lastEmittedDownloadPercent;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public long getDmId() {
        return mDmId;
    }

    public void setDmId(long dmId) {
        this.mDmId = dmId;
    }

    public DownloadStatus getDownloadStatus() {
        return mDownloadStatus;
    }

    public void setDownloadStatus(DownloadStatus downloadStatus) {
        this.mDownloadStatus = downloadStatus;
    }

    public String getObjectUrl() {
        return mDownloadUrl;
    }

    public void setObjectUrl(String objectUrl) {
        this.mDownloadUrl = objectUrl;
    }

    public int getDownloadPercent() {
        return mDownloadedPercent;
    }

    public void setDownloadPercent(int downloadPercent) {
        this.mDownloadedPercent = downloadPercent;
    }

    public int getLastEmittedDownloadPercent() {
        return mLastEmittedDownloadedPercent;
    }

    public void setLastEmittedDownloadPercent(int lastEmittedDownloadPercent) {
        this.mLastEmittedDownloadedPercent = lastEmittedDownloadPercent;
    }

    @Override
    public String toString() {
        return "DownloadableObject{" +
                "mId=" + mId +
                ", mDmId=" + mDmId +
                ", mDownloadStatus=" + mDownloadStatus +
                ", mDownloadUrl='" + mDownloadUrl + '\'' +
                ", mDownloadedPercent=" + mDownloadedPercent +
                ", mLastEmittedDownloadedPercent=" + mLastEmittedDownloadedPercent +
                '}';
    }

    @Override
    public boolean equals(Object object) {

        if (object == null || !(object instanceof DownloadableObject))
            return false;

        DownloadableObject other = (DownloadableObject) object;
        return this.mId == other.mId;
    }

    @Override
    public int hashCode() {
        return String.valueOf(this.mId).hashCode();
    }
}