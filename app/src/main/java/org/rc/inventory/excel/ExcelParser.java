package org.rc.inventory.excel;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.common.base.Strings;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.rc.inventory.R;
import org.rc.inventory.model.InventoryItemModel;
import org.rc.inventory.model.LocationModel;

import java.io.InputStream;
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


    private XSSFWorkbook mExcelWorkBook;

    private List<LocationModel> mLocationList = new LinkedList<>();

    public List<LocationModel> getLocations() {
        return mLocationList;
    }


    public boolean isFileLoaded() {
        return !(mLocationList.size() <= 0);
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


    public void loadLocationStructure(Context context) {
        final InputStream is = context.getResources().openRawResource(R.raw.globale_bestandsliste);

        try {
            mExcelWorkBook = new XSSFWorkbook(is);
            // close the input stream
            is.close();
            // read the storage locations and amount
            mLocationList = readStorageLocations(mExcelWorkBook.getSheetAt(0));

        } catch (Exception e) {
            Log.e(TAG, "Error reading XLS file", e);
        }
    }


    private List<InventoryItemModel> extractInventoryItems(XSSFSheet sheet) {
        List<InventoryItemModel> inventoryItemList = new LinkedList<>();
        Iterator<Row> rowIterator = sheet.iterator();

        String itemContainerName = "";

        // read all items
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            if(row.getLastCellNum() >= 3
                    && row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {

                // do we have a container row?
                if (row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
                    itemContainerName = row.getCell(0).getStringCellValue();

                // do we have an item cell?
                } else if(row.getCell(1).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    // we found an item
                    InventoryItemModel item = new InventoryItemModel();
                    item.setContainerLocation(itemContainerName);
                    item.setName(row.getCell(0).getStringCellValue());
                    item.setAmountExpected((int) row.getCell(1).getNumericCellValue());
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

                if(row.getCell(0).getCellType() == Cell.CELL_TYPE_STRING) {
                    mainLocation = row.getCell(0).getStringCellValue();
                }
                if(row.getCell(1).getCellType() == Cell.CELL_TYPE_STRING) {
                    subLocation = row.getCell(1).getStringCellValue();
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
                        && row.getCell(2).getCellType() == Cell.CELL_TYPE_NUMERIC
                        && row.getCell(2).getNumericCellValue() > 0) {

                    // amount of child items
                    int childCount = (int) (row.getCell(2).getNumericCellValue());
                    // name of the location
                    String childLocationName = subLocation;

                    if(childCount <= 1) {
                        // only a single child item
                        LocationModel childLocation = new LocationModel();
                        childLocation.setName(childLocationName);
                        childLocation.setDisplayName(childLocationName);
                        currentLocation.getSubLocations().add(childLocation);

                    } else {
                        // multiple child items
                        for (int i = 1; i <= childCount; ++i) {
                            LocationModel childLocation = new LocationModel();
                            childLocation.setName(childLocationName);
                            childLocation.setDisplayName(childLocationName + " " + i);
                            currentLocation.getSubLocations().add(childLocation);
                        }
                    }
                }
            }
        }

        return resultList;
    }
}
