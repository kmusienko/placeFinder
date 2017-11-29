package com.example.placeFinder.entity

class PlaceInfo {

    private String name
    private String address
    private String iconUrl
    private String phoneNumber
    private String isOpenNow
    private Double rating
    private String googleMapUrl
    private List<String> types

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    String getAddress() {
        return address
    }

    void setAddress(String address) {
        this.address = address
    }

    String getIconUrl() {
        return iconUrl
    }

    void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl
    }

    String getPhoneNumber() {
        return phoneNumber
    }

    void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber
    }

    String getIsOpenNow() {
        return isOpenNow
    }

    void setIsOpenNow(String isOpenNow) {
        this.isOpenNow = isOpenNow
    }

    Double getRating() {
        return rating
    }

    void setRating(Double rating) {
        this.rating = rating
    }

    String getGoogleMapUrl() {
        return googleMapUrl
    }

    void setGoogleMapUrl(String googleMapUrl) {
        this.googleMapUrl = googleMapUrl
    }

    List<String> getTypes() {
        return types
    }

    void setTypes(List<String> types) {
        this.types = types
    }
}
