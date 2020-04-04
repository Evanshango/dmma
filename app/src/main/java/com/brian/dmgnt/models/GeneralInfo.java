package com.brian.dmgnt.models;

import android.os.Parcel;
import android.os.Parcelable;

public class GeneralInfo implements Parcelable {

    private String infoId, infoType;

    public GeneralInfo() {
    }

    protected GeneralInfo(Parcel in) {
        infoId = in.readString();
        infoType = in.readString();
    }

    public static final Creator<GeneralInfo> CREATOR = new Creator<GeneralInfo>() {
        @Override
        public GeneralInfo createFromParcel(Parcel in) {
            return new GeneralInfo(in);
        }

        @Override
        public GeneralInfo[] newArray(int size) {
            return new GeneralInfo[size];
        }
    };

    public String getInfoId() {
        return infoId;
    }

    public void setInfoId(String infoId) {
        this.infoId = infoId;
    }

    public String getInfoType() {
        return infoType;
    }

    public void setInfoType(String infoType) {
        this.infoType = infoType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(infoId);
        dest.writeString(infoType);
    }
}
