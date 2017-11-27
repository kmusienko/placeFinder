package com.example.placeFinder.service.impl

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

    @Override
    List<Place> getNearestPlaces(String latitude, String longitude, Integer radius, String type) {

        URL nearSearchUrl

        if (type == "") {
            nearSearchUrl = new URL(nearBySearchUrl + "?location=" + latitude + "," + longitude + "&radius=" +
                    radius + "&key=" + nearbySearchKey)
        } else {
            nearSearchUrl = new URL(nearBySearchUrl + "?location=" + latitude + "," + longitude + "&radius=" +
                    radius + "&type=" + type + "&key=" + nearbySearchKey)
        }

        def parsedData = new JsonSlurper().parse(nearSearchUrl)
        List<Place> places = new ArrayList<>()

//        while(parsedData.results.size() == 0) {
//            radius*=10
//            nearSearchUrl = new URL(nearBySearchUrl + "?location=" + latitude + "," + longitude + "&radius=" +
//                    radius + "&type=" + type + "&key=" + nearbySearchKey)
//            parsedData = new JsonSlurper().parse(nearBySearchUrl)
//        }

        parsedData.results.each { placeItem ->
            String placeLat = placeItem.geometry.location.lat
            String placeLong = placeItem.geometry.location.lng
            URL gettingDistanceUrl = new URL(distanceMatrixUrl + "?&origins=" + latitude + "," +
                    longitude + "&destinations=" + placeLat + "," + placeLong + "&key=" + distanceMatrixKey)
            def destinationParsedData = new JsonSlurper().parse(gettingDistanceUrl)
            JSONObject distanced = destinationParsedData.rows
            Integer distance = ((JSONObject) distanced.get("elements")).getJSONObject("distance").get("value")
            places.add(new Place(name: placeItem.name, distance: distance))
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
}
