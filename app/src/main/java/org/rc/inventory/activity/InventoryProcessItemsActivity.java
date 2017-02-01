package org.rc.inventory.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.databinding.library.baseAdapters.BR;

import org.rc.inventory.R;
import org.rc.inventory.model.InventoryModel;
import org.rc.inventory.util.InventoryUtil;

public class InventoryProcessItemsActivity extends AppCompatActivity {

    private boolean mEditOnly = false;
    private int mInventoryItemIdx = 0;
    private InventoryModel mInventory = new InventoryModel();

    private ViewDataBinding mBinding;

    private EditText mAmountAvailable;
    private EditText mToBeChanged;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the inventory model item
        setInventoryModel();
        // gets the data binding object
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_inventory_process_items);
        // sets the first item
        setInventoryItem();
        // get the UI elements
        initUI();
    }


    private void initUI() {
        mAmountAvailable = (EditText) findViewById(R.id.inventory_process_items_available);
        mToBeChanged = (EditText) findViewById(R.id.inventory_process_items_to_be_changed);

        if(mEditOnly) {
            ((Button) findViewById(R.id.process_items_next_button)).setText(R.string.button_save);
        }
    }


    private void setInventoryModel() {
        if(getIntent().hasExtra(InventoryUtil.EXTRA_INVENTORY_ITEM)) {
            mInventory = (InventoryModel) getIntent().getSerializableExtra(InventoryUtil.EXTRA_INVENTORY_ITEM);
        }
        if(getIntent().hasExtra(InventoryUtil.EXTRA_INVENTORY_ITEM_IDX)) {
            mEditOnly = true;
            mInventoryItemIdx = getIntent().getIntExtra(InventoryUtil.EXTRA_INVENTORY_ITEM_IDX, 0);
        }
    }


    private void setInventoryItem() {
        if(mInventoryItemIdx >= 0 && mInventoryItemIdx < mInventory.getInventoryItems().size()) {
            mBinding.setVariable(BR.inventoryItem, mInventory.getInventoryItems().get(mInventoryItemIdx));
        }
    }


    private boolean commitItem() {
        boolean successful = true;

        if(mAmountAvailable.length() > 0 && mToBeChanged.length() > 0) {
            // save the values
            mInventory.getInventoryItems().get(mInventoryItemIdx)
                    .setAmount(mAmountAvailable.getText().toString());
            mInventory.getInventoryItems().get(mInventoryItemIdx)
                    .setAmountToBeChanged(mToBeChanged.getText().toString());

        } else {
            successful = false;
        }
        return successful;
    }


    public void onNextButtonClick(View view) {
        if(commitItem()) {

            if(mEditOnly) {
                showOverviewPage();
                finish();
            } else {

                ++mInventoryItemIdx;

                if (mInventoryItemIdx < mInventory.getInventoryItems().size()) {
                    setInventoryItem();
                } else {
                    showOverviewPage();
                }
            }

            Toast.makeText(this, R.string.toast_item_saved, Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this, R.string.toast_fill_in_all_fields, Toast.LENGTH_LONG).show();
        }
    }


    private void showOverviewPage() {
        final Intent showOverview = new Intent(this, InventoryFinalOverviewActivity.class);

        if(mEditOnly) {
            showOverview.putExtra(InventoryUtil.EXTRA_INVENTORY_ITEM_IDX, mInventoryItemIdx);
        }

        showOverview.putExtra(InventoryUtil.EXTRA_INVENTORY_ITEM, mInventory);
        startActivity(showOverview);
    }


    public void onBackButtonClick(View view) {
        onBackPressed();
    }


    @Override
    public void onBackPressed() {
        if(mEditOnly) {
            super.onBackPressed();

        } else {

            --mInventoryItemIdx;

            if (mInventoryItemIdx >= 0) {
                setInventoryItem();
            } else {
                // return to previous activity
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // ensure that the index is in range
        mInventoryItemIdx = Math.max(mInventoryItemIdx, 0);
        mInventoryItemIdx = Math.min(mInventoryItemIdx, mInventory.getInventoryItems().size()-1);
    }

    public void hideKeyboardOnClick(View v) {
        InputMethodManager inputMethodManager =
                (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
