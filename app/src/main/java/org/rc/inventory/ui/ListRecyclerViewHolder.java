package org.rc.inventory.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.rc.inventory.R;
import org.rc.inventory.activity.AbstractListActivity;
import org.rc.inventory.model.LocationModel;


public class ListRecyclerViewHolder extends RecyclerView.ViewHolder {

    // item data
    LocationModel mItemData;
    // layout
    private View mView;
    // parent activity
    private AbstractListActivity mActivity;


    public ListRecyclerViewHolder(AbstractListActivity activity, View view) {
        super(view);

        // keep the view
        mView = view;
        mActivity = activity;

        // set the click listener
        view.setOnClickListener(mOnItemClick);
    }


    public void setData(LocationModel data) {
        mItemData = data;

        TextView textView = (TextView)
                mView.findViewById(R.id.layout_list_entry_value);

        textView.setText(data.getDisplayName());
    }


    private final View.OnClickListener mOnItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivity.onItemClick(mItemData);
        }
    };
}
