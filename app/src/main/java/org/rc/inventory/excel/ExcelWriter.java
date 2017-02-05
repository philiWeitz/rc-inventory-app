package org.rc.inventory.excel;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.rc.inventory.R;
import org.rc.inventory.model.InventoryItemModel;
import org.rc.inventory.model.InventoryModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelWriter {
    private static final String TAG = "ExcelWriter";

    private static final String FOLDER_NAME =  "inventory";
    private static final String FILE_NAME = "/inventory.xlsx";


    private static ExcelWriter instance;

    static {
        instance = new ExcelWriter();
    }

    public static ExcelWriter getInstance() {
        return instance;
    }

    private ExcelWriter() {

    }


    public File writeInventoryFileToExternalStorage(Context ctx, String userName, InventoryModel inventory) {
        File result = null;

        // create the workbook
        Workbook wb = new XSSFWorkbook();
        // create the sheet
        CreationHelper createHelper = wb.getCreationHelper();
        Sheet sheet = wb.createSheet(inventory.getSubLocation());
        // write the cells
        writeInventoryItems(ctx, sheet, userName, inventory.getInventoryItems());

        try {
            // save the inventory workbook to external storage
            result = writeWorkbookToFile(wb);

        } catch (IOException e) {
            Log.e(TAG, "Error writing workbook", e);
        }

        return result;
    }


    private void writeInventoryItems(Context ctx, Sheet sheet, String userName,
                                     List<InventoryItemModel> inventoryItems) {
        int rowIdx = 0;

        // write the user name
        Row row = sheet.createRow(rowIdx);
        row.createCell(0).setCellValue(ctx.getString(R.string.excel_writer_col_user_name));
        row.createCell(1).setCellValue(userName);
        ++rowIdx;

        // write header
        row = sheet.createRow(++rowIdx);
        row.createCell(0).setCellValue(ctx.getString(R.string.excel_writer_col_material));
        row.createCell(1).setCellValue(ctx.getString(R.string.excel_writer_col_location));
        row.createCell(2).setCellValue(ctx.getString(R.string.excel_writer_col_amount_missing));
        row.createCell(3).setCellValue(ctx.getString(R.string.excel_writer_col_expected_amount));
        row.createCell(4).setCellValue(ctx.getString(R.string.excel_writer_col_to_be_changed));

        for(InventoryItemModel item : inventoryItems) {
            row = sheet.createRow(++rowIdx);
            row.createCell(0).setCellValue(item.getName());
            row.createCell(1).setCellValue(item.getContainerLocation());
            row.createCell(2).setCellValue(item.getAmountMissing());
            row.createCell(3).setCellValue(item.getAmountRequired());
            row.createCell(4).setCellValue(item.getAmountToBeChanged());
        }
    }


    private File writeWorkbookToFile(Workbook workbook) throws IOException {
        File folder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), FOLDER_NAME);

        // check if folder exists
        if(!folder.exists()) {
            if (!folder.mkdirs()) {
                throw new IOException("Error creating workbook folder");
            }
        }

        // delete file if exists
        File file = new File(folder.getPath() + FILE_NAME);
        if(file.exists()) {
            file.delete();
        }

        FileOutputStream fileOut = new FileOutputStream(file.getPath());

        workbook.write(fileOut);
        fileOut.close();

        return file;
    }
}
