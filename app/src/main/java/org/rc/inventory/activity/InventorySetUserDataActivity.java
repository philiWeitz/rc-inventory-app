package org.rc.inventory.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.rc.inventory.R;
import org.rc.inventory.excel.ExcelWriter;
import org.rc.inventory.model.InventoryModel;
import org.rc.inventory.util.InventoryUtil;

import java.io.File;

public class InventorySetUserDataActivity extends AbstractToolbarActivity {

    private InventoryModel mInventory = new InventoryModel();
    private EditText mUserNameEditText;
    private boolean mEmailSend = false;


    @Override
    protected int getToolbarCaption() {
        return R.string.title_inventory_set_user_data;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_set_user_data);

        setInventoryItem();
        initUI();
    }


    private void setInventoryItem() {
        if(getIntent().hasExtra(InventoryUtil.EXTRA_INVENTORY_ITEM)) {
            mInventory = (InventoryModel) getIntent().getSerializableExtra(InventoryUtil.EXTRA_INVENTORY_ITEM);
        }
    }


    private void initUI() {
        mUserNameEditText = (EditText) findViewById(R.id.set_user_data_name);
    }


    public void onBackButtonClick(View view) {
        onBackPressed();
    }


    private void returnToMainActivity() {
        final Intent mainActivity = new Intent(this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainActivity);

        finish();
    }


    public void onSendButtonClick(View view) {
        if(mUserNameEditText.length() > 0) {
            // write the workbook to the external storage
            File workbookFile = ExcelWriter.getInstance().writeInventoryFileToExternalStorage(this,
                            mUserNameEditText.getText().toString(), mInventory);

            // send workbook by email
            if(null != workbookFile) {
                openEmailChooser(workbookFile);

            } else {
                // unable to write the workbook to storage
                new AlertDialog.Builder(this)
                        .setTitle(R.string.alert_dialog_inventory_write_error_title)
                        .setMessage(R.string.alert_dialog_inventory_write_error_msg)
                        .setPositiveButton(R.string.button_ok, null).show();
            }
        } else {
            Toast.makeText(this, R.string.toast_fill_in_user_name, Toast.LENGTH_LONG).show();
        }
    }


    private void openEmailChooser(File workbookFile) {
        Uri path = Uri.fromFile(workbookFile);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {getString(R.string.email_send_to_email_address)};

        // set the body text including the sender name
        emailIntent.putExtra(Intent.EXTRA_TEXT, getString(
                R.string.email_send_to_email_body, mUserNameEditText.getText()));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_send_to_subject));

        mEmailSend = true;
        startActivity(Intent.createChooser(emailIntent , getString(R.string.email_send_to_msg)));
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(mEmailSend) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.inventory_send_title)
                    .setCancelable(false)
                    .setMessage(R.string.inventory_send_msg)
                    .setPositiveButton(R.string.button_return_to_home, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            returnToMainActivity();
                        }
                    })
                    .setNegativeButton(R.string.button_cancel, null)
                    .show();
        }

        mEmailSend = false;
    }
}
