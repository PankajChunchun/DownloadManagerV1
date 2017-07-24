package com.pankaj.downloadmanager.downloadmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.pankaj.downloadmanager.downloadmanager.DownloadManagerService;
import com.pankaj.downloadmanager.downloadmanager.beans.DownloadStatus;
import com.pankaj.downloadmanager.downloadmanager.beans.DownloadableObject;

/**
 * Preferences utilities which stores status and current download percent for each requested download object.
 * <p/>
 * Created by Pankaj Kumar on 7/16/2017.
 * pankaj.arrah@gmail.com
 */
public class PrefUtils {
    private static final String TAG = PrefUtils.class.getSimpleName();
    private static final String PREF_NAME = DownloadManagerService.class.getSimpleName().toLowerCase();
    private static final String KEY_PREFIX_DOWNLOAD = "dm";
    private static final String KEY_PREFIX_PERCENT = "pcnt";
    private Context mContext;
    private SharedPreferences mPrefs;

    public PrefUtils(Context context) {
        this.mContext = context;
        mPrefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public DownloadableObject getItem(DownloadableObject downloadableItem) {
        if (mContext == null || downloadableItem == null) {
            return downloadableItem;
        }
        String downloadingStatus = getDownloadStatus(downloadableItem.getId());
        int downloadPercent = getDownloadPercent(downloadableItem.getId());
        downloadableItem.setDownloadStatus(DownloadStatus.valueOf(downloadingStatus));
        downloadableItem.setDownloadPercent(downloadPercent);
        return downloadableItem;
    }

    public void persistItemState(DownloadableObject downloadableItem) {
        setDownloadPercent(downloadableItem.getId(),
                downloadableItem.getDownloadPercent());
        setDownloadStatus(downloadableItem.getId(),
                downloadableItem.getDownloadStatus());
    }

    public String getDownloadStatus(int itemId) {
        return mPrefs.getString(KEY_PREFIX_DOWNLOAD + itemId,
                DownloadStatus.NEW.name());
    }

    public void setDownloadStatus(int itemId, DownloadStatus downloadingStatus) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(KEY_PREFIX_DOWNLOAD + itemId, downloadingStatus.name());
        editor.commit();
    }

    public int getDownloadPercent(int itemId) {
        return mPrefs.getInt(KEY_PREFIX_PERCENT + itemId, 0);
    }

    public void setDownloadPercent(int itemId, int percent) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putInt(KEY_PREFIX_PERCENT + itemId, percent);
        editor.commit();
    }
}