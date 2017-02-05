package org.rc.inventory.util;


import java.text.SimpleDateFormat;
import java.util.Date;

public class InventoryUtil {

    private InventoryUtil() {

    }

    public static String dateToString(Date date) {
        SimpleDateFormat sf = new SimpleDateFormat(DATE_FORMAT);
        return sf.format(date);
    }

    private static final String DATE_FORMAT = "dd.MM.yyyy";
    public static final int MONTH_TILL_EXPIRING = 6;

    public static final String EXTRA_SUB_LOCATION_LIST = "extraSubLocationList";
    public static final String EXTRA_INVENTORY_ITEM = "extraInventoryItem";
    public static final String EXTRA_INVENTORY_ITEM_IDX = "extraInventoryItemIxd";
}
