package com.github.dourl.app_lib_soomthmarker.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author: douruanliang
 * @date: 2019-12-03
 */
public class CarLatLng implements Parcelable {

    public String carId;
    public double lat;
    public double lng;

    public CarLatLng(String carId, double lat, double lng) {
        this.carId = carId;
        this.lat = lat;
        this.lng = lng;
    }

    protected CarLatLng(Parcel in) {
        carId = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(carId);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CarLatLng> CREATOR = new Creator<CarLatLng>() {
        @Override
        public CarLatLng createFromParcel(Parcel in) {
            return new CarLatLng(in);
        }

        @Override
        public CarLatLng[] newArray(int size) {
            return new CarLatLng[size];
        }
    };
}
