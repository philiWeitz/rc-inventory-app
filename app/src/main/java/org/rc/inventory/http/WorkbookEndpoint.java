package org.rc.inventory.http;


import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.rc.inventory.App;
import org.rc.inventory.R;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;


public class WorkbookEndpoint extends AbstractFTPEndpoint {
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


    // runs on a new Thread not the UI Thread!
    public void getWorkbook(final IWorkbookCallback workbookCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String fileName = App.getInstance().getString(R.string.ftp_excel_file_name);
                final String serverUrl = App.getInstance().getString(R.string.ftp_url);

                FTPClient ftp = null;

                try {
                    // connect to FTP Server
                    ftp = connectToFTPServer(serverUrl, FTP.ASCII_FILE_TYPE);
                    // read only mode
                    ftp.enterLocalPassiveMode();

                    try {
                        // download the file
                        InputStream is = ftp.retrieveFileStream(fileName);
                        XSSFWorkbook excelWorkbook = new XSSFWorkbook(is);
                        is.close();

                        if(null != excelWorkbook) {
                            workbookCallback.onResponse(excelWorkbook);
                        } else {
                            workbookCallback.onFail(FtpResponseCode.STREAM_ERROR);
                        }

                    } catch (Exception e) {
                        // DEX problem
                        if(e instanceof InvocationTargetException) {
                            Log.e(TAG, "InvocationTargetException", e);
                            workbookCallback.onFail(FtpResponseCode.INVOCATION_ERROR);

                        } else {
                            Log.e(TAG, "FTP - Error getting file input stream", e);
                            workbookCallback.onFail(FtpResponseCode.STREAM_ERROR);
                        }
                    }

                    ftp.logout();
                    ftp.disconnect();

                } catch (Exception e) {
                    Log.e(TAG, "FTP - Connecting exception", e);
                    workbookCallback.onFail(FtpResponseCode.NETWORK_ERROR);
                }
            }
        }).run();
    }
}
