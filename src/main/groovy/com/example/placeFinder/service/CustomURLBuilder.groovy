package com.example.placeFinder.service

interface CustomURLBuilder {

    URL buildNearSearchUrl(String googleNearbySearchURL, Double latitude, Double longitude, Integer radius,
                           String type, String googleNearSearchKey)

    URL buildNearSearchUrlWithToken(String googleNearbySearchURL, Double latitude, Double longitude, Integer radius,
                                    String type, String googleNearSearchKey, String nextPageToken)

    URL buildGettingDistanceURL(String googleDistanceMatrixURL, Double latitude, Double longitude,
                                StringBuilder destinations, String googleDistanceMatrixKey)

    URL buildPlaceDetailsUrl(String googlePlaceDetailsURL, String placeId, String googleNearSearchKey)

}
