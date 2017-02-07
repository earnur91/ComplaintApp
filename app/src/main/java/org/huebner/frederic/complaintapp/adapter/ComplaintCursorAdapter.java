package org.huebner.frederic.complaintapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.huebner.frederic.complaintapp.R;

public class ComplaintCursorAdapter extends CursorRecyclerViewAdapter {

    public ComplaintCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        ComplaintViewHolder complaintViewHolder = (ComplaintViewHolder) viewHolder;
        cursor.moveToPosition(cursor.getPosition());
        complaintViewHolder.setData(cursor);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.complaint_list_item, parent, false);
        return new ComplaintViewHolder(view);
    }
}
