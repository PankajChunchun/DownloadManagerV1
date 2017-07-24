package com.pankaj.downloadmanager.downloadmanager.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;

import com.pankaj.downloadmanager.downloadmanager.beans.DownloadPercent;
import com.pankaj.downloadmanager.downloadmanager.beans.DownloadStatus;
import com.pankaj.downloadmanager.downloadmanager.beans.DownloadableObject;
import com.pankaj.downloadmanager.downloadmanager.interfaces.DownloadStatusCallback;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.ObservableEmitter;

/**
 * Utilities for Download manager service.
 * <p/>
 * Created by Pankaj Kumar on 7/15/2017.
 * pankaj.arrah@gmail.com
 */
public class DMUtils {
    private static final String TAG = DMUtils.class.getSimpleName();

    /**
     * It emits percent of downloading object and status of the downloading object. Status can be used to
     * know if downloading object is being downloaded or failed or paused.
     * <p/>
     * This method will be called itself after
     * {@link com.pankaj.downloadmanager.downloadmanager.utils.Constants.DownloadConfig#DEALAY_STATUS_QUERY }.
     * <p>
     * In case of failure, this method will call {@link DownloadStatusCallback#onDownloadError(DownloadableObject, int)}
     * to notify UI to update this status. Caller should update this failed status in cache.
     *
     * @param downloadManager    - {@link DownloadManager}
     * @param downloadableObject - {@link DownloadableObject}
     * @param observableEmitter  - {@link ObservableEmitter}
     * @param statusCallback     - {@link DownloadStatusCallback}
     */
    public static void queryDownloadPercents(final DownloadManager downloadManager,
                                             final DownloadableObject downloadableObject,
                                             final ObservableEmitter observableEmitter,
                                             final DownloadStatusCallback statusCallback) {

        //If the emitter has been disposed, then return.
        if (downloadManager == null || downloadableObject == null || observableEmitter == null
                || observableEmitter.isDisposed()) {
            return;
        }

        long lastEmittedDownloadPercent = downloadableObject.getLastEmittedDownloadPercent();
        DownloadPercent downloadPercent = queryDownloadPercent(downloadManager, downloadableObject.getDmId());

        if (downloadPercent == null) {
            return;
        }

        //Get the current DownloadPercent and download status
        int currentDownloadPercent = downloadPercent.getPercent();
        int downloadStatus = downloadPercent.getDownloadStatus();
        downloadableObject.setDownloadPercent(currentDownloadPercent);
        if ((currentDownloadPercent - lastEmittedDownloadPercent >= Constants.DownloadConfig.MIN_DIFF_PRCNT_TO_EMIT_STATUS) ||
                currentDownloadPercent == Constants.DownloadConfig.PERCENT_COMPLETED) {
            observableEmitter.onNext(downloadableObject);
            downloadableObject.setLastEmittedDownloadPercent(currentDownloadPercent);
        }
        // DMLog.d(TAG, downloadableObject.getObjectUrl() + " downloaded " + currentDownloadPercent + "% and Status is " + downloadStatus);
        switch (downloadStatus) {
            case DownloadManager.STATUS_FAILED:
                break;

            case DownloadManager.STATUS_SUCCESSFUL:
                break;

            case DownloadManager.STATUS_PENDING:
            case DownloadManager.STATUS_RUNNING:
            case DownloadManager.STATUS_PAUSED: // TODO Handle PAUSE  We can avoid quering about downloading percent on PAUSE, if done by user.
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        queryDownloadPercents(downloadManager, downloadableObject, observableEmitter, statusCallback);
                    }
                }, Constants.DownloadConfig.DEALAY_STATUS_QUERY);
                break;

            case DownloadManager.ERROR_UNKNOWN:
            case DownloadManager.ERROR_FILE_ERROR:
            case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
            case DownloadManager.ERROR_HTTP_DATA_ERROR:
            case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
            case DownloadManager.ERROR_INSUFFICIENT_SPACE:
            case DownloadManager.ERROR_DEVICE_NOT_FOUND:
            case DownloadManager.ERROR_CANNOT_RESUME:
            case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                if (statusCallback != null) {
                    statusCallback.onDownloadError(downloadableObject, downloadStatus);
                }
                break;
        }
    }

    /**
     * Add given downloadUrl to {@link DownloadManager} for download.
     * Download will be started and id for the object which is being downloaded will be returned.
     *
     * @param context     - {@link Context}, can not be null.
     * @param downloadUrl - Url of object which will be downloaded. Can not be null
     * @return id of the object which is being downloaded,
     * or {@link com.pankaj.downloadmanager.downloadmanager.utils.Constants.DownloadConfig#DEFAULT_ID}
     * if required arguments are not valid.
     */
    public static long enqueueToDownloadManager(Context context, String downloadUrl) {
        if (context == null || downloadUrl == null || TextUtils.isEmpty(downloadUrl.trim())) {
            return Constants.DownloadConfig.DEFAULT_ID;
        }
        // Create request for android download manager
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = Uri.parse(downloadUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        // TODO Remove notification. Start foreground service for downloading and showing notification.
        request.setTitle("DownloadManager");
        request.setDescription("A download manager created for assignment");

        // Make files visible to media scanners to show in respective application
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        //Set the local destination for the downloaded file to a path
        //within the application's external files directory
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, getFileName(uri));

        DMLog.d(TAG, "Enqueuing download to download manager " + downloadUrl);
        return downloadManager.enqueue(request);
    }

    /**
     * Get downloaded status from DownloadManager. This method is being used get actual status of downloading object
     * after getting {@link DownloadManager#ACTION_DOWNLOAD_COMPLETE}.
     *
     * @param context - {@link Context}
     * @param lookupId - id for which {@link DownloadManager} broadcasted {@link DownloadManager#ACTION_DOWNLOAD_COMPLETE}.
     * @return - {@link DownloadStatus}
     */
    public static DownloadStatus queryDownloadStatus(Context context, long lookupId) {
        if (context == null || lookupId == Constants.DownloadConfig.DEFAULT_ID) {
            return null;
        }
        // Get status of downloaded object
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //Create a query with downloadId as the filter.
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(lookupId);
        DownloadStatus status = null;

        Cursor cursor = null;
        try {
            cursor = downloadManager.query(query);
            if (cursor == null || !cursor.moveToFirst()) {
                return null;
            }

            //Get the download status
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int downloadStatus = cursor.getInt(columnIndex);

            switch (downloadStatus) {
                case DownloadManager.STATUS_FAILED:
                    status = DownloadStatus.FAILED;
                    break;

                case DownloadManager.STATUS_SUCCESSFUL:
                    status = DownloadStatus.COMPLETED;
                    break;

                case DownloadManager.STATUS_PENDING:
                    status = DownloadStatus.QUEUED;
                    break;
                case DownloadManager.STATUS_RUNNING:
                    status = DownloadStatus.IN_PROGRESS;
                    break;
                case DownloadManager.STATUS_PAUSED:
                    status = DownloadStatus.PAUSED;
                    break;

                case DownloadManager.ERROR_UNKNOWN:
                case DownloadManager.ERROR_FILE_ERROR:
                case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                case DownloadManager.ERROR_HTTP_DATA_ERROR:
                case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                case DownloadManager.ERROR_CANNOT_RESUME:
                case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                    status = DownloadStatus.FAILED;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return status;
    }

    /**
     * Get file name from given Uri
     * @param uri - {@link Uri} which file name required
     * @return - File name
     */
    private static String getFileName(Uri uri) {
        File file = new File(uri.getPath());
        return file.getName();
    }

    /**
     * Query about current downloading status (percent and status) for given lookupId.
     *
     * @param downloadManager - {@link DownloadManager}
     * @param lookupId        - id which was created by {@link DownloadManager} when object added to download list.
     * @return - {@link DownloadPercent}
     */
    private static DownloadPercent queryDownloadPercent(DownloadManager downloadManager, long lookupId) {

        //Create a query with downloadId as the filter.
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(lookupId);

        //Create an instance of downloadable result
        DownloadPercent downloadableResult = new DownloadPercent();
        downloadableResult.setDownloadingId(lookupId);

        Cursor cursor = null;
        try {
            cursor = downloadManager.query(query);
            if (cursor == null || !cursor.moveToFirst()) {
                return downloadableResult;
            }
            //Get the download percent
            float bytesDownloaded =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            float bytesTotal =
                    cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            int downloadPercent = (int) ((bytesDownloaded / bytesTotal) * 100);
            if (downloadPercent <= Constants.DownloadConfig.PERCENT_COMPLETED) {
                downloadableResult.setPercent(downloadPercent);
            }
            //Get the download status
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int downloadStatus = cursor.getInt(columnIndex);
            downloadableResult.setDownloadStatus(downloadStatus);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return downloadableResult;
    }

    private static ArrayList<DownloadPercent> queryDownloadPercent(DownloadManager downloadManager, ArrayList<Long> downloadingIds) {

        ArrayList<DownloadPercent> result = new ArrayList<DownloadPercent>();
        for (long id : downloadingIds) {
            result.add(queryDownloadPercent(downloadManager, id));
        }
        return result;
    }
}