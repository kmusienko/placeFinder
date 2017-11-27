package com.example.placeFinder.service.impl

import com.example.placeFinder.entity.FullPlace
import com.example.placeFinder.entity.Place
import com.example.placeFinder.service.PlaceService
import net.sf.json.JSONObject
import net.sf.json.groovy.JsonSlurper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class PlaceServiceImpl implements PlaceService {

    @Value('${google.key.nearbysearch}')
    private final String nearbySearchKey

    @Value('${google.key.distancematrix}')
    private final String distanceMatrixKey

    @Value('${nearbysearch.url}')
    private final String nearBySearchUrl

    @Value('${distancematrix.url}')
    private final String distanceMatrixUrl

    @Value('${placedetails.url}')
    private final String placeInfoUrl

    @Override
    List<Place> getNearestPlaces(Double latitude, Double longitude, Integer radius, String type) {

        URL nearSearchUrl = new URL(nearBySearchUrl + "?location=" + latitude + "," + longitude + "&radius=" + radius +
                 "&type=" + type + "&key=" + nearbySearchKey)

        def parsedData = new JsonSlurper().parse(nearSearchUrl)
        String nextPageToken = parsedData.next_page_token
        List<Place> places = new ArrayList<>()

        def readData = {
            parsedData.results.each { placeItem ->
                String placeLat = placeItem.geometry.location.lat
                String placeLong = placeItem.geometry.location.lng
                URL gettingDistanceUrl = new URL(distanceMatrixUrl + "?&origins=" + latitude + "," +
                        longitude + "&destinations=" + placeLat + "," + placeLong + "&key=" + distanceMatrixKey)
                def destinationParsedData = new JsonSlurper().parse(gettingDistanceUrl)
                JSONObject distanced = destinationParsedData.rows
                Integer distance = ((JSONObject) distanced.get("elements")).getJSONObject("distance").get("value")
                places.add(new Place(name: placeItem.name, distance: distance, rating: placeItem.rating))
            }
        }

        readData.call()

        while(nextPageToken !=null) {
            nearSearchUrl = new URL(nearBySearchUrl + "?location=" + latitude + "," + longitude + "&radius=" + radius +
                    "&type=" + type + "&key=" + nearbySearchKey + "&pagetoken=" + nextPageToken)
            parsedData = new JsonSlurper().parse(nearSearchUrl)
            nextPageToken = parsedData.next_page_token
            readData.call()
        }


        return places
    }

    @Override
    List<Place> sortPlacesByDistance(List<Place> places) {
        places.sort(new Comparator<Place>() {
            @Override
            int compare(Place o1, Place o2) {
                return o1.getDistance() - o2.getDistance()
            }
        })

        return places
    }

    @Override
    FullPlace getFullInfo(String placeId) {
        URL placeDetailsUrl = new URL(placeInfoUrl + "?placeId=" + placeId + "&key=" + nearbySearchKey)
        def parsedData = new JsonSlurper().parse(placeDetailsUrl)
        String address = parsedData.result.formatted_address

        return null
    }


    List<Place> getNearestPlacesV2(Double latitude, Double longitude, Integer radius, String type) {

        URL nearSearchUrl = new URL(nearBySearchUrl + "?location=" + latitude + "," + longitude + "&rankby=distance" +
//                "&radius=" + radius +
                "&type=" + type + "&key=" + nearbySearchKey)
        def parsedData = new JsonSlurper().parse(nearSearchUrl)
        List<Place> places = new ArrayList<>()

        parsedData.results.each { placeItem ->
            String placeLat = placeItem.geometry.location.lat
            String placeLong = placeItem.geometry.location.lng
            URL gettingDistanceUrl = new URL(distanceMatrixUrl + "?&origins=" + latitude + "," +
                    longitude + "&destinations=" + placeLat + ","  + placeLong + "&mode=walking" + "&key=" + distanceMatrixKey)
            def destinationParsedData = new JsonSlurper().parse(gettingDistanceUrl)
            JSONObject distanced = destinationParsedData.rows
            Integer distance = ((JSONObject) distanced.get("elements")).getJSONObject("distance").get("value")
            places.add(new Place(name: placeItem.name, distance: distance))
        }

        return places
    }
}
