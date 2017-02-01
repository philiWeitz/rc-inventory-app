package org.rc.inventory.model;


import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class LocationModel implements Serializable {

    private String name;

    private String displayName;

    private List<LocationModel> subLocations;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        if (displayName == null || "".equals(displayName)) {
            return name;
        }
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<LocationModel> getSubLocations() {
        if(null == subLocations) {
            subLocations = new LinkedList<>();
        }
        return subLocations;
    }

    public void setSubLocations(List<LocationModel> subLocations) {
        this.subLocations = subLocations;
    }
}
