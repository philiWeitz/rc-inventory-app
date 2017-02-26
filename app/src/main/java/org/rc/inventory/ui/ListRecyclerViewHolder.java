package org.rc.inventory.ui;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.rc.inventory.R;
import org.rc.inventory.activity.AbstractListActivity;
import org.rc.inventory.model.LocationModel;
import org.rc.inventory.util.InventoryUtil;

import java.util.Calendar;
import java.util.Date;


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
        TextView lastCheckedTextView = (TextView)
                mView.findViewById(R.id.layout_list_entry_value_last_check);

        textView.setText(data.getDisplayName());

        if(null == mItemData.getLastCheck()) {
            lastCheckedTextView.setVisibility(View.GONE);

        } else {
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.MONTH, -InventoryUtil.MONTH_TILL_LAST_CHECK);

            String caption = mActivity.getString(R.string.location_last_check_caption);
            lastCheckedTextView.setText(caption + " " + InventoryUtil.dateToString(data.getLastCheck()));
            lastCheckedTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.colorTextSecondary));

            // color it red!
            if(data.getLastCheck().before(c.getTime())) {
                lastCheckedTextView.setTextColor(ContextCompat.getColor(mActivity, R.color.colorRed));
            }
        }

    }


    private final View.OnClickListener mOnItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivity.onItemClick(mItemData);
        }
    };
}
