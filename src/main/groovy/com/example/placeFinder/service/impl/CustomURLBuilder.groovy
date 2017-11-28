package com.example.placeFinder.service.impl

class CustomURLBuilder {

    static URL buildNearSearchUrl(String googleNearbySearchURL, Double latitude, Double longitude, Integer radius,
                                  String type, String googleNearSearchKey) {

        return new URL(googleNearbySearchURL + "?location=" + latitude + "," +
                longitude + "&radius=" + radius + "&type=" + type + "&key=" + googleNearSearchKey)
    }

    static URL buildNearSearchUrlWithToken(String googleNearbySearchURL, Double latitude, Double longitude, Integer radius,
                                  String type, String googleNearSearchKey, String nextPageToken) {

        return new URL(googleNearbySearchURL + "?location=" + latitude + "," + longitude + "&radius=" + radius +
                "&type=" + type + "&key=" + googleNearSearchKey + "&pagetoken=" + nextPageToken)
    }

    static URL buildGettingDistanceURL(String googleDistanceMatrixURL, Double latitude, Double longitude,
                                       StringBuilder destinations, String googleDistanceMatrixKey) {
        return new URL(googleDistanceMatrixURL + "?&origins=" + latitude + "," + longitude +
                "&destinations=" + destinations + "&key=" + googleDistanceMatrixKey)
    }

}
