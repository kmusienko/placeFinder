package com.example.placeFinder.entity

class FullPlace extends Place {

    private String address
    private iconUrl
    private phoneNumber
    private isOpenNow
    private String googleMapUrl

    String getAddress() {
        return address
    }

    void setAddress(String address) {
        this.address = address
    }

    def getIconUrl() {
        return iconUrl
    }

    void setIconUrl(iconUrl) {
        this.iconUrl = iconUrl
    }

    def getPhoneNumber() {
        return phoneNumber
    }

    void setPhoneNumber(phoneNumber) {
        this.phoneNumber = phoneNumber
    }

    def getIsOpenNow() {
        return isOpenNow
    }

    void setIsOpenNow(isOpenNow) {
        this.isOpenNow = isOpenNow
    }

    String getGoogleMapUrl() {
        return googleMapUrl
    }

    void setGoogleMapUrl(String googleMapUrl) {
        this.googleMapUrl = googleMapUrl
    }
}
