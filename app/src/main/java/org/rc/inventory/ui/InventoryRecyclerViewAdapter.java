package org.rc.inventory.ui;


import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import org.rc.inventory.R;
import org.rc.inventory.model.InventoryItemModel;
import org.rc.inventory.model.InventoryModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class InventoryRecyclerViewAdapter extends
        RecyclerView.Adapter<InventoryRecyclerViewHolder> implements Filterable {

    private Activity mActivity;
    // holds all user data items
    private List<InventoryItemModel> mDataList = new ArrayList<>();
    // holds all the filtered results
    private List<InventoryItemModel> mDataListFiltered = new ArrayList<>();
    // the inventory item
    InventoryModel mInventory;


    public InventoryRecyclerViewAdapter(Activity activity, InventoryModel inventory) {
        init(activity, inventory, Collections.<InventoryItemModel>emptyList());
    }


    public InventoryRecyclerViewAdapter(Activity activity, InventoryModel inventory, List<InventoryItemModel> dataList) {
        init(activity, inventory, dataList);
    }


    private void init(Activity activity, InventoryModel inventory, List<InventoryItemModel> dataList) {
        mActivity = activity;
        mInventory = inventory;
        setDataList(dataList);
    }


    public void setDataList(List<InventoryItemModel> dataList) {
        if(null != dataList) {
            mDataList = dataList;
        } else {
            mDataList = Collections.emptyList();
        }
        mDataListFiltered = dataList;

        // update the recycler view
        notifyDataSetChanged();
    }


    @Override
    public InventoryRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        // gets the data binding object
        final ViewDataBinding binding = DataBindingUtil.inflate(
                layoutInflater, R.layout.layout_overview_list_entry, parent, false);

        return new InventoryRecyclerViewHolder(mActivity, mInventory, binding);
    }


    @Override
    public void onBindViewHolder(InventoryRecyclerViewHolder holder, int position) {
        if(mDataListFiltered.size() > position) {
            holder.setInventoryItem(mDataListFiltered.get(position));
        }
    }


    @Override
    public int getItemCount() {
        return mDataListFiltered.size();
    }


    @Override
    public Filter getFilter() {
        return mDataResultsFilter;
    }


    private final Filter mDataResultsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();

            if (charSequence == null || charSequence.length() == 0) {
                results.values = mDataList;
                results.count = mDataList.size();
            } else {
                List<InventoryItemModel> filteredList = new ArrayList<>();

                for (InventoryItemModel data : mDataList) {
                    if (data.getName().toUpperCase().contains(charSequence.toString().toUpperCase())) {
                        filteredList.add(data);
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mDataListFiltered = (List<InventoryItemModel>) results.values;
            notifyDataSetChanged();
        }
    };
}