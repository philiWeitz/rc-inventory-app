package org.rc.inventory.http;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.rc.inventory.App;
import org.rc.inventory.R;

import java.io.InputStream;


public class ImageEndpoint extends AbstractFTPEndpoint {
    private static final String TAG = "ImageEndpoint";

    // singleton instance
    private static final ImageEndpoint instance;


    static {
        // instantiate the singleton
        instance = new ImageEndpoint();
    }


    public static ImageEndpoint getInstance() {
        return instance;
    }


    private ImageEndpoint() {

    }


    // runs on a new Thread not the UI Thread!
    public void getImage(final String imageName, final IImageCallback imageCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                final String serverUrl = App.getInstance().getString(R.string.ftp_url);
                final String imagePath = App.getInstance().getString(R.string.ftp_image_path);

                FTPClient ftp = null;

                try {
                    // connect to FTP Server
                    ftp = connectToFTPServer(serverUrl, FTP.BINARY_FILE_TYPE);
                    // read only mode
                    ftp.enterLocalPassiveMode();
                    // enter image directory
                    ftp.changeWorkingDirectory(imagePath);

                    try {
                        // download the file
                        InputStream is = ftp.retrieveFileStream(imageName);
                        Bitmap image = BitmapFactory.decodeStream(is);
                        is.close();

                        if (null != image) {
                            imageCallback.onResponse(image);
                        } else {
                            imageCallback.onFail(FtpResponseCode.STREAM_ERROR);
                        }

                    } catch (Exception e) {
                        Log.e(TAG, "FTP - Error getting image input stream", e);
                        imageCallback.onFail(FtpResponseCode.STREAM_ERROR);
                    }

                    ftp.logout();
                    ftp.disconnect();

                } catch (Exception e) {
                    Log.e(TAG, "FTP - Connecting exception", e);
                    imageCallback.onFail(FtpResponseCode.NETWORK_ERROR);
                }
            }
        }).start();
    }
}
