package org.rc.inventory.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.rc.inventory.R;


public abstract class AbstractToolbarActivity extends AppCompatActivity {


    protected abstract int getToolbarCaption();


    @Override
    protected void onStart() {
        super.onStart();
        initToolbar();
    }


    private void initToolbar() {
        // set new toolbar
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if(null != toolbar) {
            if(getToolbarCaption() != 0) {
                toolbar.setTitle(getToolbarCaption());
            }

            // set the color programmatically (XML tag only supported API >= 23)
            toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorTextButton));
            //toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            setSupportActionBar(toolbar);
        }
    }
}
