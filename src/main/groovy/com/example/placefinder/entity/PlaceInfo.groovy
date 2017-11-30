package com.example.placefinder.entity

class PlaceInfo {

    private final String name
    private final String address
    private final String iconUrl
    private final String phoneNumber
    private final String isOpenNow
    private final List<String> schedule
    private final Double rating
    private final String googleMapUrl
    private final List<String> types

    private PlaceInfo(PlaceInfoBuilder builder) {
        this.name = builder.name
        this.address = builder.address
        this.iconUrl = builder.iconUrl
        this.phoneNumber = builder.phoneNumber
        this.isOpenNow = builder.isOpenNow
        this.schedule = builder.schedule
        this.rating = builder.rating
        this.googleMapUrl = builder.googleMapUrl
        this.types = builder.types
    }

    String getName() {
        return name
    }

    String getAddress() {
        return address
    }

    String getIconUrl() {
        return iconUrl
    }

    String getPhoneNumber() {
        return phoneNumber
    }

    String getIsOpenNow() {
        return isOpenNow
    }

    List<String> getSchedule() {
        return schedule
    }

    Double getRating() {
        return rating
    }

    String getGoogleMapUrl() {
        return googleMapUrl
    }

    List<String> getTypes() {
        return types
    }

    static PlaceInfoBuilder builder() {
        return new PlaceInfoBuilder()
    }

    static class PlaceInfoBuilder {

        private String name
        private String address
        private String iconUrl
        private String phoneNumber
        private String isOpenNow
        private List<String> schedule
        private Double rating
        private String googleMapUrl
        private List<String> types

        PlaceInfoBuilder setName(String name) {
            this.name = name
            return this
        }

        PlaceInfoBuilder setAddress(String address) {
            this.address = address
            return this
        }

        PlaceInfoBuilder setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl
            return this
        }

        PlaceInfoBuilder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber
            return this
        }

        PlaceInfoBuilder setIsOpenNow(String isOpenNow) {
            this.isOpenNow = isOpenNow
            return this
        }

        PlaceInfoBuilder setSchedule(List<String> schedule) {
            this.schedule = schedule
            return this
        }

        PlaceInfoBuilder setRating(Double rating) {
            this.rating = rating
            return this
        }

        PlaceInfoBuilder setGoogleMapUrl(String googleMapUrl) {
            this.googleMapUrl = googleMapUrl
            return this
        }

        PlaceInfoBuilder setTypes(List<String> types) {
            this.types = types
            return this
        }

        PlaceInfo build() {
            return new PlaceInfo(this)
        }
    }
}
