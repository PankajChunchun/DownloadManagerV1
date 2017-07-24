package com.pankaj.downloadmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import com.pankaj.downloadmanager.downloadmanager.DownloadManagerService;
import com.pankaj.downloadmanager.downloadmanager.beans.DownloadableObject;
import com.pankaj.downloadmanager.downloadmanager.db.DatabaseManager;
import com.pankaj.downloadmanager.downloadmanager.utils.Constants;
import com.pankaj.downloadmanager.downloadmanager.utils.DMLog;
import com.pankaj.downloadmanager.adapter.DownloadableListAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private DownloadableListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<DownloadableObject> mFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        DatabaseManager.init(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Get updated list from application cache
        mFiles = getDownloadableList();

        // Update UI with latest status
        mAdapter = new DownloadableListAdapter(MainActivity.this, mFiles);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void buttonClicked(View v) {
        Button button = (Button) v;
        Intent service = new Intent(MainActivity.this, DownloadManagerService.class);
        if (!DownloadManagerService.IS_SERVICE_RUNNING) {
            service.setAction(Constants.Action.STARTFOREGROUND_ACTION);
            DownloadManagerService.IS_SERVICE_RUNNING = true;
            button.setText("Stop Service");
        } else {
            service.setAction(Constants.Action.STOPFOREGROUND_ACTION);
            DownloadManagerService.IS_SERVICE_RUNNING = false;
            button.setText("Start Service");

        }
        startService(service);
    }

    private ArrayList<DownloadableObject> getDownloadableList() {
        ArrayList<DownloadableObject> list = (ArrayList) DatabaseManager.getInstance().getDownloadCache();
        if (list == null || list.isEmpty()) {
            DMLog.d(TAG, "Cache empty... Inserting dummy data");
            ArrayList<DownloadableObject> mFiles = DownloadObjStore.getDummyList();
            for (DownloadableObject obj : mFiles) {
                DatabaseManager.getInstance().addToDownloadCache(obj);
            }
            return (ArrayList) DatabaseManager.getInstance().getDownloadCache();
        } else {
            DMLog.d(TAG, "Cache NOT empty... returning cached data");
            return list;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.removeUICallbacks();
    }
}