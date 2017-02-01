package org.rc.inventory.model;


import android.util.Log;

import org.rc.inventory.util.InventoryUtil;

import java.io.Serializable;
import java.util.Date;

public class InventoryItemModel implements Serializable {

    private String name = "";

    private String containerLocation = "";

    private String amount = "";

    private int amountExpected = 0;

    private String amountToBeChanged = "0";

    private Date expirationDate = new Date();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContainerLocation() {
        return containerLocation;
    }

    public void setContainerLocation(String containerLocation) {
        this.containerLocation = containerLocation;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getAmountExpected() {
        return amountExpected;
    }

    public void setAmountExpected(int amountExpected) {
        this.amountExpected = amountExpected;
    }

    public String getAmountToBeChanged() {
        return amountToBeChanged;
    }

    public void setAmountToBeChanged(String amountToBeChanged) {
        this.amountToBeChanged = amountToBeChanged;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public String getExpirationDateString() {
        return InventoryUtil.dateToString(expirationDate);
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public int getMissingItems() {
        try {
            return (getAmountExpected() - Integer.parseInt(getAmount()));
        } catch(Exception e) {
            Log.e("ExcelWriter", "Error parsing amount integer string", e);
        }
        return 0;
    }

}
