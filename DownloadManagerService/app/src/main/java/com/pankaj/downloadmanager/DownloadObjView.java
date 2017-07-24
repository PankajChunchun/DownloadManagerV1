package com.pankaj.downloadmanager;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pankaj.downloadmanager.downloadmanager.beans.DownloadStatus;

/**
 * Custom view which represents download url view on UI.
 *
 * Created by Pankaj Kumar on 7/16/2017.
 * pankaj.arrah@gmail.com
 */
@Deprecated
public class DownloadObjView extends RelativeLayout {
    private TextView mLable;
    private ImageView mStatusImage;
    private ProgressBar mProgressBar;
    private DownloadStatus mState;

    public DownloadObjView(Context context) {
        super(context);
        init();
    }

    public DownloadObjView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DownloadObjView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.downloadable_obj_layout, this);

        int padding = (int) getResources().getDimension(R.dimen.downlodable_view_row_padding);
        setPadding(padding, 0, padding, 0);
        this.mLable = (TextView) findViewById(R.id.dm_obj_view_title);
        this.mProgressBar = (ProgressBar) findViewById(R.id.dm_obj_view_progress);
        this.mStatusImage = (ImageView) findViewById(R.id.dm_obj_view_status);
    }

    public TextView getLable() {
        return mLable;
    }

    public void setLable(TextView mLable) {
        this.mLable = mLable;
    }

    public ImageView getStatusImage() {
        return mStatusImage;
    }

    public void setStatusImage(ImageView mStatusImage) {
        this.mStatusImage = mStatusImage;
    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    public void setProgressBar(ProgressBar mProgressBar) {
        this.mProgressBar = mProgressBar;
    }

    public DownloadStatus getState() {
        return mState;
    }

    public void setState(DownloadStatus state) {
        this.mState = state;
    }

    public void updateStatus(DownloadStatus state) {
        this.mState = state;
        if (mStatusImage != null) {
            mStatusImage.setImageResource(this.mState.getIconId());
        }
    }
}
