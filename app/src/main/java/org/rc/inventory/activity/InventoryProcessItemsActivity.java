package org.rc.inventory.activity;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.databinding.library.baseAdapters.BR;

import org.rc.inventory.R;
import org.rc.inventory.http.FtpResponseCode;
import org.rc.inventory.http.IImageCallback;
import org.rc.inventory.http.ImageEndpoint;
import org.rc.inventory.model.InventoryItemModel;
import org.rc.inventory.model.InventoryModel;
import org.rc.inventory.util.InventoryUtil;

public class InventoryProcessItemsActivity extends AbstractToolbarActivity {

    private boolean mEditOnly = false;
    private int mInventoryItemIdx = 0;
    private InventoryModel mInventory = new InventoryModel();

    private ViewDataBinding mBinding;

    private EditText mAmountMissing;
    private EditText mToBeChanged;

    private ImageView mLocationImageViewBig;
    private ImageView mLocationImageViewSmall;
    private Bitmap mLocationBitmap = null;

    private String mLastLocationImageName = "";


    @Override
    protected int getToolbarCaption() {
        return R.string.title_inventory_process_item;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the inventory model item
        setInventoryModel();
        // gets the data binding object
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_inventory_process_items);
        // get the UI elements
        initUI();
        // sets the first item
        setInventoryItem();
    }


    private void initUI() {
        mAmountMissing = (EditText) findViewById(R.id.inventory_process_items_missing);
        mToBeChanged = (EditText) findViewById(R.id.inventory_process_items_to_be_changed);

        mLocationImageViewBig = (ImageView) findViewById(R.id.inventory_process_items_location_image_big);
        mLocationImageViewSmall = (ImageView) findViewById(R.id.inventory_process_items_location_image_small);

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

            // only show the small image
            onBigImageClick(null);
            // get the new item
            InventoryItemModel item = mInventory.getInventoryItems().get(mInventoryItemIdx);
            // set the data
            mBinding.setVariable(BR.inventoryItem, item);
            // set the location image
            String imageName = item.getLocationImageName();

            // only load the bitmap if not already loaded
            if(TextUtils.isEmpty(mLastLocationImageName) || !mLastLocationImageName.equals(imageName)) {
                mLocationImageViewBig.setImageDrawable(null);
                mLocationImageViewSmall.setImageDrawable(null);

                mLastLocationImageName = imageName;

                ImageEndpoint.getInstance().getImage(
                    item.getLocationImageName(), mLocationImageCallback);
            }
        }

        mToBeChanged.requestFocus();
    }


    private boolean commitItem() {
        boolean successful = true;

        if(mAmountMissing.length() > 0 && mToBeChanged.length() > 0) {
            // save the values
            mInventory.getInventoryItems().get(mInventoryItemIdx)
                    .setAmountMissing(mAmountMissing.getText().toString());
            mInventory.getInventoryItems().get(mInventoryItemIdx)
                    .setAmountToBeChanged(mToBeChanged.getText().toString());

        } else {
            successful = false;
        }
        return successful;
    }


    public void onNextButtonClick(View view) {
        hideKeyboardOnClick(view);

        if(commitItem()) {

            if(mEditOnly) {
                showOverviewPage();
                finish();
            } else {

                ++mInventoryItemIdx;

                if (mInventoryItemIdx < mInventory.getInventoryItems().size()) {
                    // show next animation
                    AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(
                            this, R.animator.slide_next_animator);
                    set.setTarget(findViewById(R.id.test_anim));
                    set.start();

                    // set next item values
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
        hideKeyboardOnClick(view);
        onBackPressed();
    }


    @Override
    public void onBackPressed() {
        if(mEditOnly) {
            super.onBackPressed();

        } else {

            --mInventoryItemIdx;

            if (mInventoryItemIdx >= 0) {
                // show back animation
                AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(
                        this, R.animator.slide_back_animator);
                set.setTarget(findViewById(R.id.test_anim));
                set.start();
                // set previous item values
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


    private IImageCallback mLocationImageCallback = new IImageCallback() {
        @Override
        public void onFail(FtpResponseCode response) {
            setImage(null);
        }

        @Override
        public void onResponse(Bitmap image) {
            setImage(image);
        }

        private void setImage(final Bitmap bitmap) {
            InventoryProcessItemsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLocationImageViewBig.setImageBitmap(bitmap);
                    mLocationImageViewSmall.setImageBitmap(bitmap);

                    if(null != mLocationBitmap) {
                        mLocationBitmap.recycle();
                    }

                    mLocationBitmap = bitmap;
                }
            });
        }
    };


    public void onSmallImageClick(View v) {
        mLocationImageViewBig.setVisibility(View.VISIBLE);
        mLocationImageViewSmall.setVisibility(View.GONE);
    }


    public void onBigImageClick(View v) {
        mLocationImageViewBig.setVisibility(View.GONE);
        mLocationImageViewSmall.setVisibility(View.VISIBLE);
    }
}
