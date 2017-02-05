package org.rc.inventory.http;


import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.xmlbeans.ResourceLoader;
import org.rc.inventory.App;
import org.rc.inventory.R;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class WorkbookEndpoint {
    private static final String TAG = "WorkbookEndpoint";
    // singleton instance
    private static final WorkbookEndpoint instance;


    static {
        // instantiate the singleton
        instance = new WorkbookEndpoint();
    }


    public static WorkbookEndpoint getInstance() {
        return instance;
    }


    private WorkbookEndpoint() {

    }


    // TODO: run this on a separate thread
    public void getWorkbook(IWorkbookCallback workbookCallback) {

        final String fileName = App.getInstance().getString(R.string.ftp_file_name);
        final String serverUrl = App.getInstance().getString(R.string.ftp_url);
        final String userName = App.getInstance().getString(R.string.ftp_username);
        final String password = App.getInstance().getString(R.string.ftp_password);

        FTPClient ftp = null;

        try {
            // connect to FTP Server
            ftp = new FTPClient();
            ftp.connect(serverUrl);
            ftp.login(userName, password);
            ftp.setFileType(FTP.ASCII_FILE_TYPE);
            ftp.enterLocalPassiveMode();

            try {
                // download the file
                InputStream is = ftp.retrieveFileStream(fileName);
                XSSFWorkbook excelWorkbook = new XSSFWorkbook(is);
                is.close();

                if(null != excelWorkbook) {
                    workbookCallback.onResponse(excelWorkbook);
                } else {
                    workbookCallback.onFail();
                }

            } catch (Exception e) {
                Log.e(TAG, "Error getting file input stream", e);
                workbookCallback.onFail();
            }

            ftp.logout();
            ftp.disconnect();

        } catch (Exception e) {
            Log.e(TAG, "Error connecting to FTP Server", e);
            workbookCallback.onFail();
        }
    }
}
