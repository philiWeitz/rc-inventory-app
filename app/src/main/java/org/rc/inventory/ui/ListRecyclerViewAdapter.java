package org.rc.inventory.ui;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.rc.inventory.R;
import org.rc.inventory.activity.AbstractListActivity;
import org.rc.inventory.model.LocationModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ListRecyclerViewAdapter extends
        RecyclerView.Adapter<ListRecyclerViewHolder> {

    private AbstractListActivity mActivity;
    // holds all user data items
    private List<LocationModel> mDataList = new ArrayList<>();


    public ListRecyclerViewAdapter(AbstractListActivity activity) {
        init(activity, Collections.<LocationModel>emptyList());
    }


    public ListRecyclerViewAdapter(AbstractListActivity activity, List<LocationModel> dataList) {
        init(activity, dataList);
    }


    private void init(AbstractListActivity activity, List<LocationModel> dataList) {
        mActivity = activity;
        setDataList(dataList);
    }


    public void setDataList(List<LocationModel> dataList) {
        if(null != dataList) {
            mDataList = dataList;
        } else {
            mDataList = Collections.emptyList();
        }

        // update the recycler view
        notifyDataSetChanged();
    }


    @Override
    public ListRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_entry, parent, false);

        return new ListRecyclerViewHolder(mActivity, view);
    }


    @Override
    public void onBindViewHolder(ListRecyclerViewHolder holder, int position) {
        if(mDataList.size() > position) {
            holder.setData(mDataList.get(position));
        }
    }


    @Override
    public int getItemCount() {
        return mDataList.size();
    }

}