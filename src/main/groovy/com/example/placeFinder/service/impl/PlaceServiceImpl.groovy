package com.example.placeFinder.service.impl

import com.example.placeFinder.entity.InfoPlace
import com.example.placeFinder.entity.Place
import com.example.placeFinder.service.PlaceService
import com.example.placeFinder.validation.StatusCodeValidator
import com.example.placeFinder.validation.TypeValidator
import net.sf.json.JSONObject
import net.sf.json.groovy.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PlaceServiceImpl implements PlaceService {

    private TypeValidator typeValidator

    private StatusCodeValidator statusValidator

    @Autowired
    void setTypeValidator(TypeValidator typeValidator) {
        this.typeValidator = typeValidator
    }

    @Autowired
    void setStatusValidator(StatusCodeValidator statusValidator) {
        this.statusValidator = statusValidator
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
    InfoPlace getInfo(String placeId) {

        URL placeDetailsUrl = new URL(PropertiesProvider.GOOGLE_PLACEDETAILS_URL + "?placeid=" + placeId + "&key=" + PropertiesProvider.GOOGLE_NEARSEARCH_KEY)
        def parsedData = new JsonSlurper().parse(placeDetailsUrl)
        String address = parsedData.result.formatted_address
        String iconUrl = parsedData.result.icon
        String phoneNumber = parsedData.result.international_phone_number
        JSONObject resultJson = parsedData.result
        String isOpenNow =  ((JSONObject) resultJson.get("opening_hours")).get("open_now")
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

        def parsedData = new JsonSlurper().parse(CustomURLBuilder.buildNearSearchUrl(PropertiesProvider.GOOGLE_NEARBYSEARCH_URL,
                latitude, longitude, radius, type, PropertiesProvider.GOOGLE_NEARSEARCH_KEY))

        statusValidator.checkStatusCode(parsedData)

        String nextPageToken = parsedData.next_page_token
        List<Place> places = new ArrayList<>()
        StringBuilder destinations = new StringBuilder()

        def readDataOpt = {
            parsedData.results.each { placeItem ->
                destinations.append("place_id:" + placeItem.place_id + "|")
                places.add(new Place(name: placeItem.name, rating: placeItem.rating, placeId: placeItem.place_id))
            }
        }

        readDataOpt.call()

        while(nextPageToken !=null) {
            URL nearSearchUrl = CustomURLBuilder.buildNearSearchUrlWithToken(PropertiesProvider.GOOGLE_NEARBYSEARCH_URL, latitude, longitude, radius, type,
                    PropertiesProvider.GOOGLE_NEARSEARCH_KEY, nextPageToken)
            parsedData = new JsonSlurper().parse(nearSearchUrl)
            statusValidator.checkStatusCode(parsedData)
            while(parsedData.status=="INVALID_REQUEST") {
                parsedData = new JsonSlurper().parse(nearSearchUrl)
            }
            nextPageToken = parsedData.next_page_token
            readDataOpt.call()
        }

        List<Integer> distances = getDistances(CustomURLBuilder.buildGettingDistanceURL(PropertiesProvider.GOOGLE_DISTANCEMATRIX_URL,
        latitude, longitude, destinations, PropertiesProvider.GOOGLE_DISTANCEMATRIX_KEY))
        for (int i=0; i<places.size(); i++) {
            places.get(i).distance = distances.get(i)
        }

        return places
    }

    @Override
    List<Integer> getDistances(URL gettingDistanceUrl) {
        def destinationParsedData = new JsonSlurper().parse(gettingDistanceUrl)
        statusValidator.checkStatusCode(destinationParsedData)
        JSONObject distanced = destinationParsedData.rows
        List<Integer> distances = new ArrayList<>()
        distanced.elements.each { item ->
            Integer distance = item.distance.value
            distances.add(distance)
        }
        return distances
    }

    //deprecated
    @Override
    List<Place> getNearestPlaces(Double latitude, Double longitude, Integer radius, String type) {

        def parsedData = new JsonSlurper().parse(CustomURLBuilder.buildNearSearchUrl(PropertiesProvider.GOOGLE_NEARBYSEARCH_URL,
                latitude, longitude, radius, type, PropertiesProvider.GOOGLE_NEARSEARCH_KEY))

        String nextPageToken = parsedData.next_page_token
        List<Place> places = new ArrayList<>()

        def readData = {
            parsedData.results.each { placeItem ->
                String placeLat = placeItem.geometry.location.lat
                String placeLong = placeItem.geometry.location.lng


                StringBuilder destinations = new StringBuilder()
                destinations.append("place_id:" + placeItem.place_id + "|")

                URL gettingDistanceUrl = new URL(PropertiesProvider.GOOGLE_DISTANCEMATRIX_URL + "?&origins=" + latitude + "," +
                        longitude + "&destinations=" + placeLat + "," + placeLong + "&key=" + PropertiesProvider.GOOGLE_DISTANCEMATRIX_KEY)
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
            parsedData = new JsonSlurper().parse(CustomURLBuilder.buildNearSearchUrlWithToken(
                    PropertiesProvider.GOOGLE_NEARBYSEARCH_URL, latitude, longitude, radius, type,
                    PropertiesProvider.GOOGLE_NEARSEARCH_KEY, nextPageToken))
            nextPageToken = parsedData.next_page_token
            readData.call()
        }

        return places
    }
}
