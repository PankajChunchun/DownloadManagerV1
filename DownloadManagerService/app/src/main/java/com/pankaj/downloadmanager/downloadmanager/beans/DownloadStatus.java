package com.pankaj.downloadmanager.downloadmanager.beans;

import com.pankaj.downloadmanager.R;

/**
 * An enum, which represents status of download object.
 *
 * Created by Pankaj Kumar on 7/15/2017.
 * pankaj.arrah@gmail.com
 */
public enum DownloadStatus {
    /**
     * On emitting of object request.
     */
    NEW {
        @Override
        public int getIconId() {
            return R.drawable.status_new_icon;
        }
    },
    /**
     * User requested to download.
     */
    QUEUED {
        @Override
        public int getIconId() {
            return R.drawable.status_waiting_icon;
        }
    },
    /**
     * Request sent to {@link android.app.DownloadManager}
     */
    IN_PROGRESS {
        @Override
        public int getIconId() {
            return R.drawable.status_inprogress_icon;
        }
    },
    /**
     * Downloading paused.
     */
    PAUSED {
        @Override
        public int getIconId() {
            return R.drawable.status_paused_icon;
        }
    },
    /**
     * Retry download. This status can be used for RESUMing also.
     */
    RETRY {
        @Override
        public int getIconId() {
            return R.drawable.status_paused_icon;
        }
    },
    /**
     * Downloading failed.
     */
    FAILED {
        @Override
        public int getIconId() {
            return R.drawable.status_retry_icon;
        }
    },
    /**
     * Downloading completed.
     */
    COMPLETED {
        @Override
        public int getIconId() {
            return R.drawable.status_completed_icon;
        }
    };

    /**
     * Each status will have 1 drawable associated with it. Which can be
     * used to represent status graphically.
     * @return
     */
    abstract public int getIconId();
}
