package org.huebner.frederic.complaintapp.adapter;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.huebner.frederic.complaintapp.R;
import org.huebner.frederic.complaintapp.content.Complaint;
import org.huebner.frederic.complaintapp.content.ProcessingStatus;

public class ComplaintViewHolder extends RecyclerView.ViewHolder {

    public TextView title, complaint;
    public ImageView statusIcon;

    public ComplaintViewHolder(View itemView) {
        super(itemView);
        this.title = (TextView) itemView.findViewById(R.id.title);
        this.complaint = (TextView) itemView.findViewById(R.id.complaint);
        this.statusIcon = (ImageView) itemView.findViewById(R.id.icon);
    }

    public void setData(Cursor data) {
        String titleText = data.getString(data.getColumnIndexOrThrow(Complaint.NAME));
        titleText += ", " + data.getString(data.getColumnIndexOrThrow(Complaint.LOCATION));
        title.setText(titleText);
        complaint.setText(data.getString(data.getColumnIndexOrThrow(Complaint.COMPLAINT_TEXT)));

        switch (ProcessingStatus.valueOf(data.getString(data.getColumnIndexOrThrow(Complaint.PROCESSING_STATUS)))) {
            case CREATED:
                statusIcon.setImageDrawable(itemView.getContext().getDrawable(R.drawable.ic_cloud_upload_black_24dp));
                break;
            case SEND:
                statusIcon.setImageDrawable(itemView.getContext().getDrawable(R.drawable.ic_check_black_24dp));
                break;
            case IN_PROCESS:
                statusIcon.setImageDrawable(itemView.getContext().getDrawable(R.drawable.ic_watch_later_black_24dp));
                break;
            case COMPLETED:
                statusIcon.setImageDrawable(itemView.getContext().getDrawable(R.drawable.ic_done_all_black_24dp));
                break;
        }
    }
}
