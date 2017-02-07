package org.huebner.frederic.complaintapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
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

import org.huebner.frederic.complaintapp.content.Complaint;
import org.huebner.frederic.complaintapp.content.ComplaintAdapter;
import org.huebner.frederic.complaintapp.content.ComplaintContentProvider;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String AUTHORITY = ComplaintContentProvider.AUTHORITY;
    public static final String ACCOUNT_TYPE = "complaint.account";
    public static final String ACCOUNT = "dummy";

    private RecyclerView recyclerView;
    private Account account;
    private ComplaintAdapter complaintAdapter;
    private Cursor cursor;
    private List<Complaint> complaintList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.complaint_toolbar);
        setSupportActionBar(toolbar);

        // Dummy account for synchronisation
        account = createSyncAccount(this);

        setup();
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

    private void setup() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        complaintAdapter = new ComplaintAdapter(complaintList, getApplicationContext());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(complaintAdapter);

        cursor = getContentResolver().query(
                ComplaintContentProvider.CONTENT_URI,
                null,
                null,
                null,
                Complaint.ID
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Complaint complaint = Complaint.fromCursor(cursor);
                    complaintList.add(complaint);
                } while (cursor.moveToNext());
            }
            cursor.close();
            complaintAdapter.notifyDataSetChanged();
        }
    }

    private void doManualSync() {
        Bundle extras = new Bundle();
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account, ComplaintContentProvider.AUTHORITY, extras);
        complaintList.clear();
        complaintAdapter.notifyDataSetChanged();
        cursor = getContentResolver().query(
                ComplaintContentProvider.CONTENT_URI,
                null,
                null,
                null,
                Complaint.ID
        );
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    Complaint complaint = Complaint.fromCursor(cursor);
                    complaintList.add(complaint);
                } while (cursor.moveToNext());
            }
            cursor.close();
            complaintAdapter.notifyDataSetChanged();
        }


        // TODO: Implement UI update, maybe LoaderManager
    }


    public static Account createSyncAccount(Context context) {
        Account account = new Account(ACCOUNT, ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return account;
    }

}
