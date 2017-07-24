package com.pankaj.downloadmanager.downloadmanager.db;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.pankaj.downloadmanager.R;
import com.pankaj.downloadmanager.downloadmanager.beans.DownloadableObject;
import com.pankaj.downloadmanager.downloadmanager.utils.DMLog;

/**
 * An {@link OrmLiteSqliteOpenHelper} class which handles database operations.
 * <p/>
 * Created by Pankaj Kumar on 7/20/2017.
 * pankaj.arrah@gmail.com
 */
// Caching download task in storage, will help to handle failed/ paused of download urls,
// and it will also help to show download list on UI, if user comes back from background.
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "download_list_cache.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<DownloadableObject, Integer> mDownloadableObjDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, DownloadableObject.class);
        } catch (SQLException e) {
            DMLog.e(TAG, "Unable to create datbases" + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVer, int newVer) {
        try {
            TableUtils.dropTable(connectionSource, DownloadableObject.class, true);
            onCreate(sqliteDatabase, connectionSource);
        } catch (SQLException e) {
            DMLog.e(TAG, "Unable to upgrade database from version " + oldVer + " to new "
                    + newVer + " " + e.getMessage());
        }
    }

    /**
     * Get DAO for downloadables.
     *
     * @return
     * @throws SQLException
     */
    public Dao<DownloadableObject, Integer> getDownloadListDao() throws SQLException {
        if (mDownloadableObjDao == null) {
            mDownloadableObjDao = getDao(DownloadableObject.class);
        }
        return mDownloadableObjDao;
    }
}