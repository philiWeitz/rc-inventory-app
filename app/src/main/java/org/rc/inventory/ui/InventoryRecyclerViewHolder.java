package org.rc.inventory.ui;

import android.app.Activity;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.rc.inventory.BR;
import org.rc.inventory.R;
import org.rc.inventory.activity.InventoryProcessItemsActivity;
import org.rc.inventory.model.InventoryItemModel;
import org.rc.inventory.model.InventoryModel;
import org.rc.inventory.util.InventoryUtil;

import java.util.Collections;


public class InventoryRecyclerViewHolder extends RecyclerView.ViewHolder {

    Activity mActivity;
    // item data
    InventoryModel mInventory;
    InventoryItemModel mInventoryItem;
    // binding
    private ViewDataBinding mBinding;


    public InventoryRecyclerViewHolder(Activity activity, InventoryModel inventory, ViewDataBinding binding) {
        super(binding.getRoot());

        mBinding = binding;
        mActivity = activity;
        mInventory = inventory;

        binding.getRoot().setOnClickListener(mHideKeyboardObClick);
        binding.getRoot().setSoundEffectsEnabled(false);

        binding.getRoot().findViewById(R.id.overview_list_entry_edit_button)
                .setOnClickListener(mOnItemClick);
    }


    public void setInventoryItem(InventoryItemModel inventoryItem) {
        mInventoryItem = inventoryItem;
        mBinding.setVariable(BR.inventoryItem, inventoryItem);
        mBinding.executePendingBindings();
    }


    private final View.OnClickListener mOnItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int index = mInventory.getInventoryItems().indexOf(mInventoryItem);

            if(index >= 0) {
                final Intent editInventory = new Intent(mActivity, InventoryProcessItemsActivity.class);
                editInventory.putExtra(InventoryUtil.EXTRA_INVENTORY_ITEM, mInventory);
                editInventory.putExtra(InventoryUtil.EXTRA_INVENTORY_ITEM_IDX, index);

                mActivity.startActivity(editInventory);
            }
        }
    };


    private final View.OnClickListener mHideKeyboardObClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    };
}
