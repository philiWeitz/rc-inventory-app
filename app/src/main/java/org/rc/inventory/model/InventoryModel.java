package org.rc.inventory.model;


import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class InventoryModel implements Serializable {

    private String mainLocation = "";

    private String subLocation = "";

    private List<InventoryItemModel> inventoryItems;


    public String getMainLocation() {
        return mainLocation;
    }

    public void setMainLocation(String mainLocation) {
        this.mainLocation = mainLocation;
    }

    public String getSubLocation() {
        return subLocation;
    }

    public void setSubLocation(String subLocation) {
        this.subLocation = subLocation;
    }

    public List<InventoryItemModel> getInventoryItems() {
        if(null == inventoryItems) {
            inventoryItems = new LinkedList<>();
        }

        return inventoryItems;
    }

    public void setInventoryItems(List<InventoryItemModel> inventoryItems) {
        this.inventoryItems = inventoryItems;
    }
}
