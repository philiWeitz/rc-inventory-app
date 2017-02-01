package org.rc.inventory.activity;


import android.content.Intent;
import android.os.Bundle;

import org.rc.inventory.excel.ExcelParser;
import org.rc.inventory.model.InventoryModel;
import org.rc.inventory.model.LocationModel;
import org.rc.inventory.util.IntentUtil;
import org.rc.inventory.util.InventoryUtil;

import java.util.List;

public class InventoryLocationMainActivity extends AbstractListActivity {

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
    public void onItemClick(LocationModel location) {
        mInventory.setMainLocation(location.getName());

        // store the child location list
        IntentUtil.getInstance().setExtra(
                InventoryUtil.EXTRA_SUB_LOCATION_LIST, location.getSubLocations());

        // start the new intent
        Intent selectBagLocationIntent = new Intent(this, InventoryLocationSubActivity.class);
        selectBagLocationIntent.putExtra(InventoryUtil.EXTRA_INVENTORY_ITEM, mInventory);

        startActivity(selectBagLocationIntent);
    }


    @Override
    protected List<LocationModel> loadData() {
        return ExcelParser.getInstance().getLocations();
    }
}
