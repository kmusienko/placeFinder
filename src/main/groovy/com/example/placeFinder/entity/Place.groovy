package com.example.placeFinder.entity

class Place {

    private String name
    private Integer distance
    private Double rating
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

    Double getRating() {
        return rating
    }

    void setRating(Double rating) {
        this.rating = rating
    }

    String getPlaceId() {
        return placeId
    }

    void setPlaceId(String placeId) {
        this.placeId = placeId
    }
}
