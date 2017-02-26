package org.rc.inventory.model;


import org.rc.inventory.util.InventoryUtil;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class InventoryItemModel implements Serializable {

    private String name = "";

    private String containerLocation = "";

    private String amountMissing = "0";

    private String amountRequired = "0";

    private String amountToBeChanged = "0";

    private String locationImageName = "placeholder_image.png";

    private Date expirationDate = new Date();


    public InventoryItemModel() {
        setExpirationDate();
    }

    private void setExpirationDate() {
        Date referenceDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(referenceDate);
        c.add(Calendar.MONTH, InventoryUtil.MONTH_TILL_EXPIRING);
        expirationDate = c.getTime();
    }

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

    public String getAmountMissing() {
        return amountMissing;
    }

    public void setAmountMissing(String amountMissing) {
        this.amountMissing = amountMissing;
    }

    public String getAmountRequired() {
        return amountRequired;
    }

    public void setAmountRequired(String amountRequired) {
        this.amountRequired = amountRequired;
    }

    public String getAmountToBeChanged() {
        return amountToBeChanged;
    }

    public void setAmountToBeChanged(String amountToBeChanged) {
        this.amountToBeChanged = amountToBeChanged;
    }

    public String getLocationImageName() {
        return locationImageName;
    }

    public void setLocationImageName(String locationImageName) {
        this.locationImageName = locationImageName;
    }

    public String getExpirationDateString() {
        return InventoryUtil.dateToString(expirationDate);
    }
}
