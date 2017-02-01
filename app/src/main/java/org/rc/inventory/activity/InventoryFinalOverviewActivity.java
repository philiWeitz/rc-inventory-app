package org.rc.inventory.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import org.rc.inventory.R;
import org.rc.inventory.model.InventoryModel;
import org.rc.inventory.ui.InventoryRecyclerViewAdapter;
import org.rc.inventory.util.InventoryUtil;

public class InventoryFinalOverviewActivity extends AppCompatActivity {

    private InventoryModel mInventory = new InventoryModel();

    private InventoryRecyclerViewAdapter mListAdapter;

    private SearchView mSearchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_final_overview);

        // get the inventory object
        setInventoryModel();
        // init all UI elements
        initUI();
    }


    // gets the UI fields and initializes the recycler view
    private void initUI() {
        // get the UI elements
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_final_overview_recycler_view);

        // setup the search
        mSearchView = (SearchView) findViewById(R.id.searchView);
        // set the search filter
        mSearchView.setOnQueryTextListener(mOnSearchListener);
        // expand the search view to make it everywhere clickable
        mSearchView.setIconified(false);

        // initializes the recycler view similar to list view
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mListAdapter = new InventoryRecyclerViewAdapter(this, mInventory);
        // set the adapter
        recyclerView.setAdapter(mListAdapter);
    }


    private void setInventoryModel() {
        if(getIntent().hasExtra(InventoryUtil.EXTRA_INVENTORY_ITEM)) {
            mInventory = (InventoryModel) getIntent().getSerializableExtra(InventoryUtil.EXTRA_INVENTORY_ITEM);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        // avoid showing the keyboard
        mSearchView.clearFocus();

        // only load the data once
        if(null == mListAdapter
                || mListAdapter.getItemCount() <= 0) {
            loadData();
        }
    }


    private void loadData() {
        mListAdapter.setDataList(mInventory.getInventoryItems());

        if(getIntent().hasExtra(InventoryUtil.EXTRA_INVENTORY_ITEM_IDX)) {
            int itemIdx = getIntent().getIntExtra(InventoryUtil.EXTRA_INVENTORY_ITEM_IDX, 0);

            final RecyclerView recyclerView =
                    (RecyclerView) findViewById(R.id.activity_final_overview_recycler_view);
            recyclerView.scrollToPosition(itemIdx);
        }
    }


    public void onBackButtonClick(View view) {
        onBackPressed();
    }


    public void onNextButtonClick(View view) {
        final Intent setUserData = new Intent(this, InventorySetUserDataActivity.class);
        setUserData.putExtra(InventoryUtil.EXTRA_INVENTORY_ITEM, mInventory);
        startActivity(setUserData);
    }


    private final SearchView.OnQueryTextListener mOnSearchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            mListAdapter.getFilter().filter(newText);
            return false;
        }
    };
}
