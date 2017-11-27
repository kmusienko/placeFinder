package com.example.placeFinder.entity

class Place {

    private String name
    private Integer distance
    private Integer rating
    private String placeId

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    Integer getDistance() {
        return distance
    }

    void setDistance(Integer distance) {
        this.distance = distance
    }

    Integer getRating() {
        return rating
    }

    void setRating(Integer rating) {
        this.rating = rating
    }

    String getPlaceId() {
        return placeId
    }

    void setPlaceId(String placeId) {
        this.placeId = placeId
    }
}
