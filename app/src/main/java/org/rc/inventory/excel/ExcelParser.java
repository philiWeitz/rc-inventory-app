package org.rc.inventory.excel;


import android.text.TextUtils;
import android.util.Log;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.rc.inventory.http.FtpResponseCode;
import org.rc.inventory.http.IWorkbookCallback;
import org.rc.inventory.http.WorkbookEndpoint;
import org.rc.inventory.model.InventoryItemModel;
import org.rc.inventory.model.LocationModel;

import java.net.HttpURLConnection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ExcelParser {

    private static final String TAG = "ExcelParser";
    private static ExcelParser instance;

    static {
        instance = new ExcelParser();
    }

    public static ExcelParser getInstance() {
        return instance;
    }

    private ExcelParser() {

    }

    private FtpResponseCode mFtpResponse = FtpResponseCode.OK;

    private boolean mLoadingDone = false;

    private XSSFWorkbook mExcelWorkBook;

    private List<LocationModel> mLocationList = new LinkedList<>();

    public List<LocationModel> getLocations() {
        return mLocationList;
    }


    public boolean isDoneLoading() {
        return mLoadingDone;
    }


    public boolean isValid() {
        return (null != mExcelWorkBook);
    }


    public FtpResponseCode getResponseCode() {
        return mFtpResponse;
    }


    public List<InventoryItemModel> loadInventoryItems(String subLocationName) {
        List<InventoryItemModel> inventoryItemsList = new LinkedList<>();

        if(null != mExcelWorkBook) {
            XSSFSheet sheet = mExcelWorkBook.getSheet(subLocationName);

            if(null != sheet) {
                inventoryItemsList = extractInventoryItems(sheet);
            } else {
                Log.e(TAG, "No worksheet with the name '" + subLocationName + "' exists!");
            }
        } else {
            Log.e(TAG, "Excel worksheet is null!");
        }

        return inventoryItemsList;
    }


    public void loadLocationStructureAsync() {
        mLoadingDone = false;

        WorkbookEndpoint.getInstance().getWorkbook(new IWorkbookCallback() {
            @Override
            public void onFail(FtpResponseCode response) {
                mFtpResponse = response;
                mLoadingDone = true;
            }

            @Override
            public void onResponse(XSSFWorkbook workbook) {
                // set the workbook
                mExcelWorkBook = workbook;
                // read the storage locations and amount
                XSSFSheet sheet = mExcelWorkBook.getSheetAt(0);

                if(null != sheet) {
                    mLocationList = readStorageLocations(sheet);
                    mFtpResponse = FtpResponseCode.OK;
                } else {
                    Log.e(TAG, "Error loading first sheet from workbook");
                    mFtpResponse = FtpResponseCode.STREAM_ERROR;
                }

                mLoadingDone = true;
            }
        });
    }


    private List<InventoryItemModel> extractInventoryItems(XSSFSheet sheet) {
        List<InventoryItemModel> inventoryItemList = new LinkedList<>();
        Iterator<Row> rowIterator = sheet.iterator();

        String itemContainerName = "";

        // read all items
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            if(row.getLastCellNum() >= 3
                    && row.getCell(0, Row.CREATE_NULL_AS_BLANK).getCellType() == Cell.CELL_TYPE_STRING) {

                // do we have a container row?
                if (row.getCell(1, Row.CREATE_NULL_AS_BLANK).getCellType() == Cell.CELL_TYPE_STRING) {
                    itemContainerName = row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue();

                // do we have an item cell?
                } else if(row.getCell(1, Row.CREATE_NULL_AS_BLANK).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    // we found an item
                    InventoryItemModel item = new InventoryItemModel();
                    item.setContainerLocation(itemContainerName);
                    item.setName(row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue());
                    item.setAmountRequired(String.valueOf((int) row.getCell(1, Row.CREATE_NULL_AS_BLANK).getNumericCellValue()));
                    inventoryItemList.add(item);
                }
            }
        }
        return inventoryItemList;
    }


    private List<LocationModel> readStorageLocations(XSSFSheet sheet) {
        LocationModel currentLocation = new LocationModel();
        List<LocationModel> resultList = new LinkedList<>();

        Iterator<Row> rowIterator = sheet.iterator();

        // parse the sheet
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            if(row.getLastCellNum() >= 3) {

                String subLocation = "";
                String mainLocation = "";

                if(row.getCell(0, Row.CREATE_NULL_AS_BLANK).getCellType() == Cell.CELL_TYPE_STRING) {
                    mainLocation = row.getCell(0, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                }
                if(row.getCell(1, Row.CREATE_NULL_AS_BLANK).getCellType() == Cell.CELL_TYPE_STRING) {
                    subLocation = row.getCell(1, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
                }

                // new parent element?
                if(!TextUtils.isEmpty(mainLocation)) {
                    // add the parent location item
                    if(!currentLocation.getSubLocations().isEmpty()) {
                        resultList.add(currentLocation);
                    }

                    // set the new location
                    currentLocation = new LocationModel();
                    currentLocation.setName(mainLocation);
                }

                // new child item?
                if(!TextUtils.isEmpty(subLocation)
                        && row.getCell(2, Row.CREATE_NULL_AS_BLANK).getCellType() == Cell.CELL_TYPE_NUMERIC
                        && row.getCell(2, Row.CREATE_NULL_AS_BLANK).getNumericCellValue() > 0) {

                    // amount of child items
                    int childCount = (int) (row.getCell(2, Row.CREATE_NULL_AS_BLANK).getNumericCellValue());
                    Date lastCheck = row.getCell(3, Row.CREATE_NULL_AS_BLANK).getDateCellValue();

                    // name of the location
                    String childLocationName = subLocation;

                    if(childCount <= 1) {
                        // only a single child item
                        LocationModel childLocation = new LocationModel();
                        childLocation.setName(childLocationName);
                        childLocation.setDisplayName(childLocationName);
                        childLocation.setLastCheck(lastCheck);
                        currentLocation.getSubLocations().add(childLocation);

                    } else {
                        // multiple child items
                        for (int i = 1; i <= childCount; ++i) {
                            LocationModel childLocation = new LocationModel();
                            childLocation.setName(childLocationName);
                            childLocation.setDisplayName(childLocationName + " " + i);
                            childLocation.setLastCheck(lastCheck);
                            currentLocation.getSubLocations().add(childLocation);
                        }
                    }
                }
            }
        }

        return resultList;
    }
}
