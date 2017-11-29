package com.example.placeFinder.service.impl

import com.example.placeFinder.service.CustomURLBuilder
import org.springframework.stereotype.Component

@Component
class CustomURLBuilderImpl implements CustomURLBuilder{

    @Override
    URL buildNearSearchUrl(String googleNearbySearchURL, Double latitude, Double longitude, Integer radius,
                                  String type, String googleNearSearchKey) {

        return new URL(googleNearbySearchURL + "?location=" + latitude + "," +
                longitude + "&radius=" + radius + "&type=" + type + "&key=" + googleNearSearchKey)
    }

    @Override
    URL buildNearSearchUrlWithToken(String googleNearbySearchURL, Double latitude, Double longitude, Integer radius,
                                  String type, String googleNearSearchKey, String nextPageToken) {

        return new URL(googleNearbySearchURL + "?location=" + latitude + "," + longitude + "&radius=" + radius +
                "&type=" + type + "&key=" + googleNearSearchKey + "&pagetoken=" + nextPageToken)
    }

    @Override
    URL buildGettingDistanceURL(String googleDistanceMatrixURL, Double latitude, Double longitude,
                                       StringBuilder destinations, String googleDistanceMatrixKey) {
        return new URL(googleDistanceMatrixURL + "?&origins=" + latitude + "," + longitude +
                "&destinations=" + destinations + "&key=" + googleDistanceMatrixKey)
    }

    @Override
    URL buildPlaceDetailsUrl(String googlePlaceDetailsURL, String placeId, String googleNearSearchKey) {

        return new URL(googlePlaceDetailsURL + "?placeid=" + placeId + "&key=" + googleNearSearchKey)
    }
}
