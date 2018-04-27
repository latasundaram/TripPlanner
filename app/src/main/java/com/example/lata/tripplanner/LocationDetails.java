package com.example.lata.tripplanner;

import java.io.Serializable;

/**
 * Created by Lata on 28-04-2017.
 */

public class LocationDetails implements Serializable{
    String locationId,locationName,locationAddress;
    Double locLatitude,locLongitude;

    public LocationDetails(String locationId, String locationName, String locationAddress, Double locLatitude, Double locLongitude) {
        this.locationId = locationId;
        this.locationName = locationName;
        this.locationAddress = locationAddress;
        this.locLatitude = locLatitude;
        this.locLongitude = locLongitude;
    }

    public LocationDetails() {
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public Double getLocLatitude() {
        return locLatitude;
    }

    public void setLocLatitude(Double locLatitude) {
        this.locLatitude = locLatitude;
    }

    public Double getLocLongitude() {
        return locLongitude;
    }

    public void setLocLongitude(Double locLongitude) {
        this.locLongitude = locLongitude;
    }
}
