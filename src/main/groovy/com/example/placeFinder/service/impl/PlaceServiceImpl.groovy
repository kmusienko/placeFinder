package com.example.placeFinder.service.impl

import com.example.placeFinder.entity.InfoPlace
import com.example.placeFinder.entity.Place
import com.example.placeFinder.service.PlaceService
import com.example.placeFinder.validation.TypeValidator
import net.sf.json.JSONObject
import net.sf.json.groovy.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
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

    private TypeValidator typeValidator

    @Autowired
    void setTypeValidator(TypeValidator typeValidator) {
        this.typeValidator = typeValidator
    }

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


                StringBuilder destinations = new StringBuilder()
                destinations.append("place_id:" + placeItem.place_id + "|")

                URL gettingDistanceUrl = new URL(distanceMatrixUrl + "?&origins=" + latitude + "," +
                        longitude + "&destinations=" + placeLat + "," + placeLong + "&key=" + distanceMatrixKey)
                def destinationParsedData = new JsonSlurper().parse(gettingDistanceUrl)
                JSONObject distanced = destinationParsedData.rows
                Integer distance = ((JSONObject) distanced.get("elements")).getJSONObject("distance").get("value")
                String name = placeItem.name
                Double rating = placeItem.rating
                String placeId = placeItem.place_id

                places.add(new Place(name: name, distance: distance, rating: rating, placeId: placeId))
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
    InfoPlace getFullInfo(String placeId) {
        URL placeDetailsUrl = new URL(placeInfoUrl + "?placeid=" + placeId + "&key=" + nearbySearchKey)
        def parsedData = new JsonSlurper().parse(placeDetailsUrl)
        String address = parsedData.result.formatted_address
        String iconUrl = parsedData.result.icon
        String phoneNumber = parsedData.result.international_phone_number
        JSONObject resultJson = parsedData.result
        Boolean isOpenNow =  ((JSONObject) resultJson.get("opening_hours")).get("open_now")
        String googleMapUrl = parsedData.result.url
        Double rating = parsedData.result.rating
        String name = parsedData.result.name
        List<String> types = parsedData.result.types

        InfoPlace infoPlace = new InfoPlace(name: name, address: address, iconUrl: iconUrl, phoneNumber: phoneNumber,
                isOpenNow: isOpenNow, rating: rating, googleMapUrl: googleMapUrl, types: types)

        return infoPlace
    }

    @Override
    List<Place> getNearestPlacesOptimized(Double latitude, Double longitude, Integer radius, String type) {

        typeValidator.checkTypeValidity(type)

        URL nearSearchUrl = new URL(nearBySearchUrl + "?location=" + latitude + "," + longitude + "&radius=" + radius +
                "&type=" + type + "&key=" + nearbySearchKey)

        def parsedData = new JsonSlurper().parse(nearSearchUrl)
        String nextPageToken = parsedData.next_page_token

        List<Place> places = new ArrayList<>()
        StringBuilder destinations = new StringBuilder()

        def readDataOpt = {
            parsedData.results.each { placeItem ->
                destinations.append("place_id:" + placeItem.place_id + "|")
                String name = placeItem.name
                Double rating = placeItem.rating
                String placeId = placeItem.place_id
                places.add(new Place(name: name, rating: rating, placeId: placeId))
            }
        }

        readDataOpt.call()

        while(nextPageToken !=null) {
      //      Thread.sleep(1800)
            nearSearchUrl = new URL(nearBySearchUrl + "?location=" + latitude + "," + longitude + "&radius=" + radius +
                    "&type=" + type + "&key=" + nearbySearchKey + "&pagetoken=" + nextPageToken)
            parsedData = new JsonSlurper().parse(nearSearchUrl)
            while(parsedData.status=="INVALID_REQUEST") {
                parsedData = new JsonSlurper().parse(nearSearchUrl)
            }
            nextPageToken = parsedData.next_page_token
            readDataOpt.call()
        }

        URL gettingDistanceUrl = new URL(distanceMatrixUrl + "?&origins=" + latitude + "," +
                longitude + "&destinations=" + destinations + "&key=" + distanceMatrixKey)
        List<Integer> distances = getDistances(gettingDistanceUrl)
        for (int i=0; i<places.size(); i++) {
            places.get(i).distance = distances.get(i)
        }

        return places
    }

    @Override
    List<Integer> getDistances(URL gettingDistanceUrl) {
        def destinationParsedData = new JsonSlurper().parse(gettingDistanceUrl)
        JSONObject distanced = destinationParsedData.rows
        List<Integer> distances = new ArrayList<>()
        distanced.elements.each { item ->
            Integer distance = item.distance.value
            distances.add(distance)
        }
        return distances
    }


    @Override
    List<Place> getNearestPlacesUsingRankBy(Double latitude, Double longitude, Integer radius, String type) {

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
