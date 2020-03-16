package com.brian.dmgnt.models;

public class EmergencyContact {

    private String contactName, imageUrl, info, contactNo;

    public EmergencyContact() {
    }

    public EmergencyContact(String contactName, String imageUrl, String info, String contactNo) {
        this.contactName = contactName;
        this.imageUrl = imageUrl;
        this.info = info;
        this.contactNo = contactNo;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }
}
