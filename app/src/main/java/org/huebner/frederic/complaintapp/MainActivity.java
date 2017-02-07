package org.huebner.frederic.complaintapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.huebner.frederic.complaintapp.adapter.ComplaintCursorAdapter;
import org.huebner.frederic.complaintapp.content.Complaint;
import org.huebner.frederic.complaintapp.content.ComplaintContentProvider;
import org.huebner.frederic.complaintapp.content.SyncState;
import org.huebner.frederic.complaintapp.sync.SyncUtils;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView recyclerView;
    private ComplaintCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.complaint_toolbar);
        setSupportActionBar(toolbar);

        // Dummy account for synchronisation
        SyncUtils.CreateSyncAccount(this);

        // RecyclerView Setup
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        getLoaderManager().initLoader(0, null, this);
        cursorAdapter = new ComplaintCursorAdapter(this, null);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(cursorAdapter);

    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync:
                doManualSync();
                return true;
            case R.id.action_create:
                openCreateComplaintActivity();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openCreateComplaintActivity() {
        Intent intent = new Intent(this, CreateComplaintActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void doManualSync() {
        SyncUtils.TriggerRefresh(true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Fields from the database (projection)
        // Must include the _id column for the adapter to work
        String[] projection = {Complaint.ID, Complaint.NAME, Complaint.LOCATION, Complaint.COMPLAINT_TEXT, Complaint.PROCESSING_STATUS, Complaint.SYNC_STATE};

        CursorLoader cursorLoader = new CursorLoader(this,
                ComplaintContentProvider.CONTENT_URI, projection,
                Complaint.SYNC_STATE + " != ?",
                new String[]{SyncState.DELETE.name()}, Complaint.ID
                + " COLLATE NOCASE ASC");
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
