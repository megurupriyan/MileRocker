package com.agencyemr.milerocker.gpstracker.Utils;

/**
 * Created by Grishma on 17/5/16.
 */
public class LocationVo {

    private double mLatitude, mLongitude;
    private int mLocId;
    private String mLocAddress;

    public int getLocId() {
        return mLocId;
    }

    public void setLocId(int mLocId) {
        this.mLocId = mLocId;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public String getLocAddress() {
        return mLocAddress;
    }

    public void setLocAddress(String mLocAddress) {
        this.mLocAddress = mLocAddress;
    }
}
