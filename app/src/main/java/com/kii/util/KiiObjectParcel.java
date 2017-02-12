package com.kii.util;

import android.os.Parcel;
import android.os.Parcelable;

import com.kii.cloud.storage.Kii;
import com.kii.cloud.storage.KiiObject;

import java.util.ArrayList;

/**
 * Created by Atsuto5 on 2016/08/08.
 */
public class KiiObjectParcel implements Parcelable {

    private ArrayList<KiiObject> Object;

    public KiiObjectParcel(Parcel in) {
        Object = in.createTypedArrayList(KiiObject.CREATOR);
    }

    public KiiObjectParcel(ArrayList<KiiObject> object) {
        this.Object = object;
    }


    public static final Creator<KiiObjectParcel> CREATOR = new Creator<KiiObjectParcel>() {
        @Override
        public KiiObjectParcel createFromParcel(Parcel in) {
            return new KiiObjectParcel(in);
        }

        @Override
        public KiiObjectParcel[] newArray(int size) {
            return new KiiObjectParcel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(Object);
    }

    public ArrayList<KiiObject> getKiiObject(){
        return Object;
    }
}
