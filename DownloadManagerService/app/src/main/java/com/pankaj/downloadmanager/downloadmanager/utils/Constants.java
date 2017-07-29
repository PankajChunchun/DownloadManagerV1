package com.pankaj.downloadmanager.downloadmanager.utils;

import com.pankaj.downloadmanager.downloadmanager.DownloadManagerService;

/**
 * Created by Pankaj Kumar on 7/15/2017.
 * pankaj.arrah@gmail.com
 */
public final class Constants {
    private Constants() {
        throw new UnsupportedOperationException("Can not instanciated!!!");
    }

    public static final class DownloadConfig {
        public static final int DEFAULT_ID = -1;
        public static final int PERCENT_COMPLETED = 100;
        public static final int MAX_DOWNLOADS = 2;
        public final static int DEALAY_STATUS_QUERY = 200;
        public final static int MIN_DIFF_PRCNT_TO_EMIT_STATUS = 3;
    }

    public static final class Action {
        public static String ACTION_NEXT_DOWNLOAD = "com.pankaj.downloadmanager.NEXT_DOWNLOAD";
        public static String ACTION_PAUSE_DOWNLOAD = "com.pankaj.downloadmanager.PAUSE_DOWNLOAD";
        public static String ACTION_RESUME_DOWNLOAD = "com.pankaj.downloadmanager.ACTION_RESUME_DOWNLOAD";
        public static String ACTION_RETRY_DOWNLOAD = "com.pankaj.downloadmanager.RETRY_DOWNLOAD";
        public static String ACTION_STOP_DOWNLOAD_MANAGER = "com.pankaj.downloadmanager.STOP_DOWNLOAD_SERVICE";
        public static String ACTION_DOWNLOADED = "com.pankaj.downloadmanager.ACTION_DOWNLOADED";
        public static String ACTION_UPDATE_UI_ON_DOWNLOADED = "com.pankaj.downloadmanager.ACTION_UPDATE_UI_ON_DOWNLOADED";
        public static String ACTION_TRACK_PROGRESS = "com.pankaj.downloadmanager.ACTION_TRACK_PROGRESS";
        public static String ACTION_LAUNCH_APP = "com.pankaj.downloadmanager.ACTION_LAUNCH_APP";
        public static String STARTFOREGROUND_ACTION = "com.pankaj.downloadmanager.ACTION_STARTFOREGROUND";
        public static String STOPFOREGROUND_ACTION = "com.pankaj.downloadmanager.ACTION_STOPFOREGROUND";
    }

    public static final class Extra {
        public static String KEY_NEXT_DOWNLOAD = "REQUEST.NEXT_DOWNLOAD";
        public static String KEY_TRACK_DOWNLOAD_PROGRESS = "REQUEST.TRACK_DOWNLOAD_PROGRESS";
        public static String KEY_DOWNLOADED_DMID = "REQUEST.DOWNLOADED_DMID";
    }

    public static final class Notification {
        public static int FOREGROUND_SERVICE_NID = 101;
    }
}
