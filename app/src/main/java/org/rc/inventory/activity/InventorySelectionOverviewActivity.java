package org.rc.inventory.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;

import org.rc.inventory.R;
import org.rc.inventory.excel.ExcelParser;
import org.rc.inventory.model.InventoryItemModel;
import org.rc.inventory.model.InventoryModel;
import org.rc.inventory.util.InventoryUtil;

import java.util.List;

public class InventorySelectionOverviewActivity extends AbstractToolbarActivity {

    private InventoryModel mInventory = new InventoryModel();


    @Override
    protected int getToolbarCaption() {
        return R.string.title_inventory_location_overview;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the item
        setInventoryItem();

        // gets the data binding object
        final ViewDataBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_inventory_selection_overview);

        binding.setVariable(BR.inventoryModel, mInventory);
    }


    private void setInventoryItem() {
        if(getIntent().hasExtra(InventoryUtil.EXTRA_INVENTORY_ITEM)) {
            mInventory = (InventoryModel) getIntent().getSerializableExtra(InventoryUtil.EXTRA_INVENTORY_ITEM);
        }
    }


    public void onStartInventoryClick(View v) {

        // get the inventory items
        List<InventoryItemModel> items =
                ExcelParser.getInstance().loadInventoryItems(mInventory.getSubLocation());

        mInventory.setInventoryItems(items);

        if(!items.isEmpty()) {
            final Intent startInventory = new Intent(this, InventoryProcessItemsActivity.class);
            startInventory.putExtra(InventoryUtil.EXTRA_INVENTORY_ITEM, mInventory);

            startInventory.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startInventory);

        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.alert_dialog_inventory_no_items_title)
                    .setMessage(R.string.alert_dialog_inventory_no_items_msg)
                    .setPositiveButton(R.string.button_ok, null)
                    .show();
        }
    }


    public void onBackButtonClick(View v) {
        onBackPressed();
    }
}
