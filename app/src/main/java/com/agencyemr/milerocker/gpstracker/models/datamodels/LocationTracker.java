package com.agencyemr.milerocker.gpstracker.models.datamodels;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Gurupriyan on 10/18/2016.
 */

@Entity
public class LocationTracker {

    @Id
    Long id;

    double latitude;
    double longitude;
    double deviation;
    int modifiedUserId;
    String startLocationName;
    String imei;

    public LocationTracker() {
    }



    @Generated(hash = 1923967758)
    public LocationTracker(Long id, double latitude, double longitude,
            double deviation, int modifiedUserId, String startLocationName,
            String imei) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.deviation = deviation;
        this.modifiedUserId = modifiedUserId;
        this.startLocationName = startLocationName;
        this.imei = imei;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDeviation() {
        return deviation;
    }

    public void setDeviation(double deviation) {
        this.deviation = deviation;
    }

    public int getModifiedUserId() {
        return modifiedUserId;
    }

    public void setModifiedUserId(int modifiedUserId) {
        this.modifiedUserId = modifiedUserId;
    }

    public String getStartLocationName() {
        return startLocationName;
    }

    public void setStartLocationName(String startLocationName) {
        this.startLocationName = startLocationName;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}
