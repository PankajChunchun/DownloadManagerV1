package com.pankaj.downloadmanager.downloadmanager;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.app.Notification;
import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.pankaj.downloadmanager.MainActivity;
import com.pankaj.downloadmanager.R;
import com.pankaj.downloadmanager.downloadmanager.beans.DownloadStatus;
import com.pankaj.downloadmanager.downloadmanager.beans.DownloadableObject;
import com.pankaj.downloadmanager.downloadmanager.db.DatabaseManager;
import com.pankaj.downloadmanager.downloadmanager.interfaces.DownloadableObjCallback;
import com.pankaj.downloadmanager.downloadmanager.utils.Constants;
import com.pankaj.downloadmanager.downloadmanager.utils.DMLog;
import com.pankaj.downloadmanager.downloadmanager.utils.DMSubscriberHelper;
import com.pankaj.downloadmanager.downloadmanager.utils.DMUtils;
import com.pankaj.downloadmanager.downloadmanager.utils.PrefUtils;

/**
 * {@link Service} which starts downloading for requested object and update database after download completes.
 * It works as below
 * <p/>
 * <pre>
 *     1. UI informs it via {@link #enqueDownload(Context, DownloadableObject)}
 *
 *     2. It Handles action and call {@link #onDownloadRequested(DownloadableObject)}, which asks
 *        {@link io.reactivex.FlowableEmitter} to emit this request if applicable.
 *
 *        Here applicable defines the maximum requests {@link com.pankaj.downloadmanager.downloadmanager.utils.Constants.DownloadConfig#MAX_DOWNLOADS}
 *        can be downloaded parallely.
 *
 *        If ongoing requests are more than {@link com.pankaj.downloadmanager.downloadmanager.utils.Constants.DownloadConfig#MAX_DOWNLOADS},
 *        {@link #onDownloadStarted(DownloadableObject)} will be called after any other download would be completed.
 *
 *     3. {@link #onDownloadStarted(DownloadableObject)} will be called. Where it will update status to database and inform UI to start downloading
 *        progress via {@link com.pankaj.downloadmanager.downloadmanager.utils.DMPercentObserverHelper}.
 *
 *     4. {@link #downloadReceiver} will inform this Service when downloading will be completed for any object. From {@link #downloadReceiver}
 *        it will confirm status of object by the id received from {@link DownloadManager}. And it updates database and broadcast an Intent
 *        with downloaded object's id, which UI component can receive this Intent and update UI accordingly.
 *
 * </pre>
 */
public class DownloadManagerService extends Service implements DownloadableObjCallback {
    private static final String TAG = DownloadManagerService.class.getSimpleName();
    public static boolean IS_SERVICE_RUNNING = false;
    private DMSubscriberHelper mDmSubscriberHelper;
    private int mDownloadCounter = 0;
    private PrefUtils mPrefUtils;

    public DownloadManagerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDmSubscriberHelper = new DMSubscriberHelper(this);
        mPrefUtils = new PrefUtils(this);
        DatabaseManager.init(this);
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            DMLog.d(TAG, "onStartCommand() Action : " + intent.getAction());
            if (Constants.Action.STARTFOREGROUND_ACTION.equals(intent.getAction())) {
                showNotification();
            } else if (Constants.Action.ACTION_NEXT_DOWNLOAD.equals(intent.getAction())) {
                DownloadableObject nextRequest = (DownloadableObject) intent.getExtras().getSerializable(Constants.Extra.KEY_NEXT_DOWNLOAD);
                onDownloadRequested(nextRequest);
            } else if (Constants.Action.ACTION_DOWNLOADED.equals(intent.getAction())) {
                onDownloadComplete();
            } else if (Constants.Action.STOPFOREGROUND_ACTION.equals(intent.getAction())) {
                stopForeground(true);
                stopSelf();
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDmSubscriberHelper.performCleanUp();
        DMLog.d(TAG, "In onDestroy");
    }

    @Override
    public void onDownloadRequested(DownloadableObject downloadableItem) {
        DMLog.d(TAG, "onDownloadRequested. Url : " + downloadableItem.getObjectUrl());
        mDmSubscriberHelper.emitNextItem(downloadableItem);
    }

    @Override
    public void onDownloadStarted(DownloadableObject downloadableItem) {
        mDownloadCounter++;
        long downloadId = DMUtils.enqueueToDownloadManager(getApplicationContext(), downloadableItem.getObjectUrl());
        if (downloadId == Constants.DownloadConfig.DEFAULT_ID) {
            DMLog.d(TAG, "onDownloadStarted. Error from DownloadManager.");
            return;
        }
        DMLog.d(TAG, "onDownloadStarted" + downloadableItem.getObjectUrl());

        downloadableItem.setDmId(downloadId);
        downloadableItem.setDownloadStatus(DownloadStatus.IN_PROGRESS);
        // Store status in cache
        mPrefUtils.persistItemState(downloadableItem);
        DatabaseManager.getInstance().updateDownloadCache(downloadableItem);

        // Send broadcasts to UI to get current status and start tracking progress.
        Intent intent = new Intent(Constants.Action.ACTION_TRACK_PROGRESS);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.Extra.KEY_TRACK_DOWNLOAD_PROGRESS, downloadableItem);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    @Override
    public void onDownloadComplete() {
        DMLog.d(TAG, "onDownloadComplete(). Requesting next download...");

        mDownloadCounter--;
        // Check for positive number. Where org.reactivestreams.Subscription#request() requires only positive numbers.
        int n = Constants.DownloadConfig.MAX_DOWNLOADS - mDownloadCounter;
        if (n > 0) {
            mDmSubscriberHelper.requestNextDownloads(Constants.DownloadConfig.MAX_DOWNLOADS - mDownloadCounter);
        }
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent == null) {
                // Ignore this onreceive call
                return;
            }

            //check if the broadcast message is for our enqueued download
            long downloadedObjId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            DMLog.d(TAG, "From Receiver >> Download completed for " + downloadedObjId);

            // Take Next download request
            onDownloadComplete();
            updateStatusAndNotifyUI(downloadedObjId);
        }
    };

    public static void enqueDownload(Context context, DownloadableObject downloadableObject) {
        Intent intent = new Intent(context, DownloadManagerService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.Extra.KEY_NEXT_DOWNLOAD, downloadableObject);
        intent.putExtras(bundle);
        intent.setAction(Constants.Action.ACTION_NEXT_DOWNLOAD);
        context.startService(intent);
    }

    public static void startWithAction(Context context, String action) {
        Intent intent = new Intent(context, DownloadManagerService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    private void updateStatusAndNotifyUI(final long downloadedId) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Update current downloaded object's status in database
                DownloadableObject currObj = DatabaseManager.getInstance().getDownloadCacheWithDownloadId(downloadedId);

                // Get actual status
                DownloadStatus status = DMUtils.queryDownloadStatus(DownloadManagerService.this.getApplicationContext(),
                        currObj.getDmId());
                // Inform UI to update status of downloaded item
                // Send broadcasts to UI to get current status
                if (status != null) {
                    currObj.setDownloadStatus(status);
                    if (status == DownloadStatus.COMPLETED) {
                        currObj.setDownloadPercent(100);
                    }
                    /*if (status == DownloadStatus.FAILED) {
                        object.setDownloadPercent(0);
                    }*/
                } else {
                    // TODO Handle error case
                }

                DMLog.d(TAG, "From Receiver updateStatusAndNotifyUI >> Download completed for [ID :" + currObj.getDmId()
                        + "] [URL : " + currObj.getObjectUrl() + "]");

                // Store status in cache
                mPrefUtils.persistItemState(currObj);
                DatabaseManager.getInstance().updateDownloadCache(currObj);

                Intent uiIntent = new Intent(Constants.Action.ACTION_UPDATE_UI_ON_DOWNLOADED);
                Bundle bundle = new Bundle();
                bundle.putLong(Constants.Extra.KEY_DOWNLOADED_DMID, downloadedId);
                uiIntent.putExtras(bundle);
                sendBroadcast(uiIntent);
            }
        });
        thread.start();
    }

    private void showNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.Action.ACTION_LAUNCH_APP);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent pauseIntent = new Intent(this, DownloadManagerService.class);
        pauseIntent.setAction(Constants.Action.ACTION_PAUSE_DOWNLOAD);
        PendingIntent pendingPauseIntent = PendingIntent.getService(this, 0, pauseIntent, 0);

        Intent resumeIntent = new Intent(this, DownloadManagerService.class);
        resumeIntent.setAction(Constants.Action.ACTION_RESUME_DOWNLOAD);
        PendingIntent pendingResumeIntent = PendingIntent.getService(this, 0, resumeIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("DownloadManager Assignment")
                .setTicker("Download Manager")
                .setContentText("Object Name")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_media_pause, "Pause", pendingPauseIntent)
                .addAction(android.R.drawable.ic_media_play, "Resume", pendingResumeIntent)
                .build();
        startForeground(Constants.Notification.FOREGROUND_SERVICE_NID, notification);
    }
}