package org.rc.inventory.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.rc.inventory.R;
import org.rc.inventory.model.LocationModel;
import org.rc.inventory.ui.ListRecyclerViewAdapter;

import java.util.List;

public abstract class AbstractListActivity extends AppCompatActivity {

    private ListRecyclerViewAdapter mListAdapter;


    // abstract methods
    public abstract void onItemClick(LocationModel item);
    protected abstract List<LocationModel> loadData();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        initUI();
    }


    // gets the UI fields and initializes the recycler view
    private void initUI() {
        // get the UI elements
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.activity_select_location_recycler_view);

        // initializes the recycler view similar to list view
        final RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mListAdapter = new ListRecyclerViewAdapter(this);
        // set the adapter
        recyclerView.setAdapter(mListAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();

        // only load the data once
        if(null == mListAdapter
                || mListAdapter.getItemCount() <= 0) {
            mListAdapter.setDataList(loadData());
        }
    }
}
