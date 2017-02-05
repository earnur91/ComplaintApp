package org.huebner.frederic.complaintapp.content;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.huebner.frederic.complaintapp.R;

import java.util.List;


public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ComplaintViewHolder> {

    private List<Complaint> complaintList;
    private Context context;

    public class ComplaintViewHolder extends RecyclerView.ViewHolder {

        public TextView title, complaint;

        public ImageView statusIcon;

        public ComplaintViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            complaint = (TextView) itemView.findViewById(R.id.complaint);
            statusIcon = (ImageView) itemView.findViewById(R.id.icon);
        }
    }

    public ComplaintAdapter(List<Complaint> complaintList, Context context) {
        this.complaintList = complaintList;
        this.context = context;
    }

    @Override
    public ComplaintViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.complaint_list_item, parent, false);
        return new ComplaintViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ComplaintViewHolder holder, int position) {
        Complaint complaint = complaintList.get(position);
        holder.title.setText(complaint.getName() + ", " + complaint.getLocation());
        holder.complaint.setText(complaint.getComplaintText());
        switch (complaint.getProcessingStatus()) {
            case CREATED:
                holder.statusIcon.setImageDrawable(context.getDrawable(R.drawable.ic_cloud_upload_black_24dp));
                break;
            case SEND:
                holder.statusIcon.setImageDrawable(context.getDrawable(R.drawable.ic_check_black_24dp));
                break;
            case IN_PROCESS:
                holder.statusIcon.setImageDrawable(context.getDrawable(R.drawable.ic_watch_later_black_24dp));
                break;
            case COMPLETED:
                holder.statusIcon.setImageDrawable(context.getDrawable(R.drawable.ic_done_all_black_24dp));
                break;
        }
    }


    @Override
    public int getItemCount() {
        return complaintList.size();
    }
}
