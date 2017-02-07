package org.huebner.frederic.complaintapp;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.huebner.frederic.complaintapp.content.Complaint;
import org.huebner.frederic.complaintapp.content.ComplaintContentProvider;
import org.huebner.frederic.complaintapp.content.ProcessingStatus;
import org.huebner.frederic.complaintapp.content.SyncState;
import org.huebner.frederic.complaintapp.sync.SyncUtils;

public class CreateComplaintActivity extends AppCompatActivity {

    private EditText nameEdit;
    private EditText locationEdit;
    private EditText complaintTextEdit;
    private Button createButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_complaint);
        Toolbar toolbar = (Toolbar) findViewById(R.id.create_toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        nameEdit = (EditText) findViewById(R.id.input_name);
        locationEdit = (EditText) findViewById(R.id.input_location);
        complaintTextEdit = (EditText) findViewById(R.id.input_complaintText);
        createButton = (Button) findViewById(R.id.btn_create);

    }

    public void createComplaint(View view) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Complaint.NAME, nameEdit.getText().toString());
        contentValues.put(Complaint.LOCATION, locationEdit.getText().toString());
        contentValues.put(Complaint.COMPLAINT_TEXT, complaintTextEdit.getText().toString());
        contentValues.put(Complaint.PROCESSING_STATUS, ProcessingStatus.CREATED.name());
        getContentResolver().insert(ComplaintContentProvider.CONTENT_URI, contentValues);

        requestUpload();
        finish();
    }

    private void requestUpload() {
        SyncUtils.TriggerRefresh(true);
    }
}
