package org.rc.inventory.activity;


import android.content.Intent;
import android.os.Bundle;

import org.rc.inventory.model.InventoryModel;
import org.rc.inventory.model.LocationModel;
import org.rc.inventory.util.IntentUtil;
import org.rc.inventory.util.InventoryUtil;

import java.util.LinkedList;
import java.util.List;

public class InventoryLocationSubActivity extends AbstractListActivity {

    private InventoryModel mInventory = new InventoryModel();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().hasExtra(InventoryUtil.EXTRA_INVENTORY_ITEM)) {
            mInventory = (InventoryModel) getIntent()
                    .getSerializableExtra(InventoryUtil.EXTRA_INVENTORY_ITEM);
        }
    }


    @Override
    public void onItemClick(LocationModel item) {
        mInventory.setSubLocation(item.getName());

        Intent overviewActivity = new Intent(this, InventorySelectionOverviewActivity.class);
        overviewActivity.putExtra(InventoryUtil.EXTRA_INVENTORY_ITEM, mInventory);

        startActivity(overviewActivity);
    }


    @Override
    protected List<LocationModel> loadData() {
        // get the list of all sub locations
        List<LocationModel> subLocations = IntentUtil.getInstance().getExtra(
                        InventoryUtil.EXTRA_SUB_LOCATION_LIST);

        if(null == subLocations) {
            subLocations = new LinkedList<>();
        }

        return subLocations;
    }
}
