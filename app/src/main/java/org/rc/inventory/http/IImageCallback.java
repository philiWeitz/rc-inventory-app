package org.rc.inventory.http;


import android.graphics.Bitmap;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public interface IImageCallback {
    void onFail(FtpResponseCode response);
    void onResponse(Bitmap image);
}
