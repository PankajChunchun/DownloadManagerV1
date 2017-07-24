package com.pankaj.downloadmanager.adapter;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pankaj.downloadmanager.R;
import com.pankaj.downloadmanager.downloadmanager.DownloadManagerService;
import com.pankaj.downloadmanager.downloadmanager.beans.DownloadStatus;
import com.pankaj.downloadmanager.downloadmanager.beans.DownloadableObject;
import com.pankaj.downloadmanager.downloadmanager.db.DatabaseManager;
import com.pankaj.downloadmanager.downloadmanager.interfaces.DownloadStatusCallback;
import com.pankaj.downloadmanager.downloadmanager.interfaces.DownloadableObjPercentCallback;
import com.pankaj.downloadmanager.downloadmanager.utils.Constants;
import com.pankaj.downloadmanager.downloadmanager.utils.DMLog;
import com.pankaj.downloadmanager.downloadmanager.utils.DMPercentObserverHelper;
import com.pankaj.downloadmanager.downloadmanager.utils.DMUtils;
import com.pankaj.downloadmanager.downloadmanager.utils.PrefUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * {@link android.support.v7.widget.RecyclerView.Adapter}, which shows download list.
 * Where user can start/ stop/ resume downloading of objects.
 *
 * This registers a {@link BroadcastReceiver} to listen actions {@link Constants.Action#ACTION_TRACK_PROGRESS}
 * which says adapter to start observing downloading progress (in percent), and
 * {@link Constants.Action#ACTION_UPDATE_UI_ON_DOWNLOADED} to update status "completed" of one of ongoing download.
 *
 * Created by Pankaj Kumar on 7/11/2017.
 * pankaj.arrah@gmail.com
 */
public class DownloadableListAdapter extends RecyclerView.Adapter<DownloadableListAdapter.ViewHolder>
        implements DownloadableObjPercentCallback, DownloadStatusCallback {

    private static final String TAG = DownloadableListAdapter.class.getSimpleName();
    private ArrayList<DownloadableObject> mUrls;
    private final DMPercentObserverHelper mDmPercentObserverHelper;
    private final WeakReference<Context> mContextWeakReference;
    private PrefUtils mPrefUtils;

    /**
     * Initialise {@link android.support.v7.widget.RecyclerView.Adapter}.
     * @param context - {@link Context}
     * @param urls - list of {@link DownloadableObject}
     */
    public DownloadableListAdapter(Context context, ArrayList<DownloadableObject> urls) {
        this.mContextWeakReference = new WeakReference(context);
        this.mUrls = urls;
        this.mDmPercentObserverHelper = new DMPercentObserverHelper(this);
        this.mPrefUtils = new PrefUtils(context);
        DatabaseManager.init(context);
        IntentFilter filter = new IntentFilter(Constants.Action.ACTION_TRACK_PROGRESS);
        filter.addAction(Constants.Action.ACTION_UPDATE_UI_ON_DOWNLOADED);
        context.registerReceiver(mUIUpdateReceiver, filter);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mDownloadableFile;
        public ProgressBar mDownloadProgress;
        public ImageView mDownloadAction;

        public ViewHolder(View view) {
            super(view);
            mDownloadableFile = (TextView) view.findViewById(R.id.dm_obj_view_title);
            mDownloadProgress = (ProgressBar) view.findViewById(R.id.dm_obj_view_progress);
            mDownloadAction = (ImageView) view.findViewById(R.id.dm_obj_view_status);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.downloadable_obj_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final DownloadableObject row = mUrls.get(position);
        holder.mDownloadableFile.setText(row.getObjectUrl());
        getFileName(holder.mDownloadableFile, row.getObjectUrl());
        holder.mDownloadAction.setImageResource(row.getDownloadStatus().getIconId());
        holder.mDownloadProgress.setProgress(row.getDownloadPercent());
        holder.mDownloadAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DMLog.d(TAG, "Clicked on " + holder.mDownloadableFile.getText());
                switch (row.getDownloadStatus()) {
                    case NEW:
                    case FAILED:
                        // Update status on UI
                        row.setDownloadStatus(DownloadStatus.QUEUED);
                        mPrefUtils.persistItemState(row);
                        holder.mDownloadAction.setImageResource(row.getDownloadStatus().getIconId());
                        updateDownloadableObject(row);
                        DownloadManagerService.enqueDownload(mContextWeakReference.get(), row);
                        break;

                    case PAUSED:
                        // TODO: Write logic to resume downloading
                        break;

                    case COMPLETED:
                    case QUEUED:
                        // TODO: Update UI if required.
                        // Ignore click in such cases
                        break;

                    default:
                        break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUrls.size();
    }

    private void getFileName(TextView tv, String url) {
        Uri returnUri = Uri.parse(url);
        File file = new File(returnUri.getPath());
        file.getName();
        tv.setText(file.getName());
    }

    @Override
    public void updateDownloadableObject(DownloadableObject downloadableObject) {
        if (downloadableObject == null || mContextWeakReference.get() == null) {
            return;
        }

        DownloadStatus status = DMUtils.queryDownloadStatus(mContextWeakReference.get(), downloadableObject.getDmId());
        if (status == DownloadStatus.COMPLETED) {
            downloadableObject.setDownloadStatus(DownloadStatus.COMPLETED);
            downloadableObject.setDownloadPercent(100);
        }

        // Update local store
        mPrefUtils.persistItemState(downloadableObject);
        DatabaseManager.getInstance().updateDownloadCache(downloadableObject);

        updateUI(downloadableObject);
    }

    private BroadcastReceiver mUIUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            DMLog.d(TAG, "Received " + intent.getAction());
            if (Constants.Action.ACTION_TRACK_PROGRESS.equalsIgnoreCase(intent.getAction())) {
                DownloadableObject trackDownload = (DownloadableObject) intent.getExtras().getSerializable(Constants.Extra.KEY_TRACK_DOWNLOAD_PROGRESS);
                updateUI(trackDownload);
                DownloadManager dm = (DownloadManager) mContextWeakReference.get().getSystemService(Context.DOWNLOAD_SERVICE);
                DMUtils.queryDownloadPercents(dm, trackDownload, mDmPercentObserverHelper.getPercentageObservableEmitter(), DownloadableListAdapter.this);
            } else if (Constants.Action.ACTION_UPDATE_UI_ON_DOWNLOADED.equalsIgnoreCase(intent.getAction())) {
                long downloadedId = intent.getExtras().getLong(Constants.Extra.KEY_DOWNLOADED_DMID, -1);
                DownloadableObject obj = getDownloadedDownloadableObjectWithDmId(downloadedId);
                if (obj != null) {
                    // obj.setDownloadPercent(100);
                    // obj.setDownloadStatus(DownloadStatus.COMPLETED);
                    updateDownloadableObject(obj);
                }
            }
        }
    };

    private DownloadableObject getDownloadedDownloadableObjectWithDmId(long dmId) {
        DownloadableObject result = null;
        for (DownloadableObject object : mUrls) {
            if (dmId == object.getDmId()) {
                result = object;
                break;
            }
        }
        return result;
    }

    private void updateUI(DownloadableObject updatedObj) {
        int position = -1;
        int len = mUrls.size();
        for (int i=0; i<len; i++) {
            if (updatedObj.getId() == mUrls.get(i).getId()) {
                position = i;
                break;
            }
        }

        // Reset object at position
        if (position != -1) {
            mUrls.set(position, updatedObj);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onDownloadError(DownloadableObject failedObj, int errorCode) {

        if (failedObj == null) {
            return;
        }

        notifyUserAboutFailure(errorCode, failedObj.getObjectUrl());

        failedObj.setDownloadStatus(DownloadStatus.FAILED);
        // FIXME Percent should not be zero always. This can help to track downloaded parts of an Object, which can help in retry or resume logic.
        failedObj.setDownloadPercent(0);
        mPrefUtils.persistItemState(failedObj);
        DatabaseManager.getInstance().updateDownloadCache(failedObj);
        updateUI(failedObj);
        // Notify service to process next download.
        DownloadManagerService.startWithAction(mContextWeakReference.get(), Constants.Action.ACTION_DOWNLOADED);
    }

    @Override
    public void onPausedByUser(DownloadableObject failedObj) {

    }

    private void notifyUserAboutFailure(int errorCode, String url) {
        // Map error message from error code
        String message = null;
        Context context = mContextWeakReference.get();
        try {
            message = context.getResources().getString(context.getResources().getIdentifier(
                    context.getResources().getString(R.string.dm_error_prefix) + errorCode, "string", context.getPackageName()));
        } catch (Resources.NotFoundException e) {

        }
        if (message != null) {
            Toast.makeText(mContextWeakReference.get(), url + " failed. " + message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Remove callbacks and observers which intended to update UI.
     * This method should call when Application is going in background.
     */
    public void removeUICallbacks() {
        mDmPercentObserverHelper.performCleanUp();
        try {
            mContextWeakReference.get().unregisterReceiver(mUIUpdateReceiver);
        } catch (IllegalArgumentException e) {
            // Handle receiver not registered exception
        }
    }
}