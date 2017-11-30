package com.example.placeFinder.component

import com.example.placeFinder.entity.enums.ApiProvider
import org.springframework.stereotype.Component

@Component
class URLBuilder {

    URL build(ApiProvider apiProvider, Map<String, Object> params) {
        if (apiProvider == ApiProvider.GOOGLE_NEARSEARCH) {
            return parseGoogleNearSearch(params)
        } else if (apiProvider == ApiProvider.GOOGLE_PLACEDETAILS) {
            return parseGooglePlaceDetails(params)
        }
    }

    private URL parseGoogleNearSearch(Map<String, Object> params) {
        StringBuilder stringURL = new StringBuilder()
        stringURL
                .append(PropertiesProvider.GOOGLE_NEARBYSEARCH_URL)
                .append("?location=")
                .append(params.get("latitude"))
                .append(",")
                .append(params.get("longitude"))
                .append("&radius=")
                .append(params.get("radius"))
                .append("&type=")
                .append(params.get("type"))
                .append("&key=")
                .append(PropertiesProvider.GOOGLE_NEARSEARCH_KEY)

        if (params.containsKey("nextPageToken")) {
            stringURL
                    .append("&pagetoken=")
                    .append(params.get("nextPageToken"))
        }

        return new URL(stringURL.toString())
    }

    private URL parseGooglePlaceDetails(Map<String, Object> params) {
        StringBuilder stringURL = new StringBuilder()
        stringURL
        .append(PropertiesProvider.GOOGLE_PLACEDETAILS_URL)
        .append("?placeid=")
        .append(params.get("placeId"))
        .append("&key=")
        .append(PropertiesProvider.GOOGLE_NEARSEARCH_KEY)

        return new URL(stringURL.toString())
    }

}
