package com.example.placeFinder.service.impl

import com.example.placeFinder.entity.PlaceInfo
import com.example.placeFinder.entity.Place
import com.example.placeFinder.service.CustomURLBuilder
import com.example.placeFinder.service.PlaceService
import com.example.placeFinder.validation.GeoCoordinatesValidator
import com.example.placeFinder.validation.StatusCodeValidator
import com.example.placeFinder.validation.TypeValidator
import com.google.maps.model.Geometry
import net.sf.json.JSON
import net.sf.json.JSONObject
import net.sf.json.groovy.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PlaceServiceImpl implements PlaceService {

    private TypeValidator typeValidator

    private StatusCodeValidator statusValidator

    private GeoCoordinatesValidator geoCoordinatesValidator
    
    private CustomURLBuilder customURLBuilder

    private URLParser urlParser

    @Autowired
    void setTypeValidator(TypeValidator typeValidator) {
        this.typeValidator = typeValidator
    }

    @Autowired
    void setStatusValidator(StatusCodeValidator statusValidator) {
        this.statusValidator = statusValidator
    }

    @Autowired
    void setGeoCoordinatesValidator(GeoCoordinatesValidator geoCoordinatesValidator) {
        this.geoCoordinatesValidator = geoCoordinatesValidator
    }

    @Autowired
    void setCustomURLBuilder(CustomURLBuilder customURLBuilder) {
        this.customURLBuilder = customURLBuilder
    }

    @Autowired
    void setUrlParser(URLParser urlParser) {
        this.urlParser = urlParser
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
    PlaceInfo getInfo(String placeId) {

        def parsedData = urlParser.parseURL(customURLBuilder.buildPlaceDetailsUrl(
                PropertiesProvider.GOOGLE_PLACEDETAILS_URL, placeId, PropertiesProvider.GOOGLE_NEARSEARCH_KEY))

        return createPlaceInfoObject(parsedData)
    }

    @Override
    PlaceInfo createPlaceInfoObject(JSON parsedData) {

        String address = parsedData.result.formatted_address
        String iconUrl = parsedData.result.icon
        String phoneNumber = parsedData.result.international_phone_number
        JSONObject resultJson = parsedData.result
        String isOpenNow =  ((JSONObject) resultJson.get("opening_hours")).get("open_now")
        String googleMapUrl = parsedData.result.url
        Double rating = parsedData.result.rating
        String name = parsedData.result.name
        List<String> types = parsedData.result.types

        PlaceInfo infoPlace = new PlaceInfo(name: name, address: address, iconUrl: iconUrl, phoneNumber: phoneNumber,
                isOpenNow: isOpenNow, rating: rating, googleMapUrl: googleMapUrl, types: types)

        return infoPlace
    }

    void fillListOfPlaces(List<Place> places, Double latitude, Double longitude, Integer radius, String type) {

        def parsedData = urlParser.parseURL(customURLBuilder.buildNearSearchUrl(PropertiesProvider.GOOGLE_NEARBYSEARCH_URL,
                latitude, longitude, radius, type, PropertiesProvider.GOOGLE_NEARSEARCH_KEY))

        statusValidator.checkStatusCode(parsedData)

        String nextPageToken = parsedData.next_page_token

        while(true) {
            parsedData.results.each { placeItem ->
                Double itemLatitude = placeItem.geometry.location.lat
                Double itemLongitude = placeItem.geometry.location.lng
                int directDistance = getDirectDistance(latitude, longitude, itemLatitude, itemLongitude)
                places.add(new Place(name: placeItem.name, rating: placeItem.rating, placeId: placeItem.place_id,
                        distance: directDistance))
            }

            if (nextPageToken == null) break

            URL nearSearchUrl = customURLBuilder.buildNearSearchUrlWithToken(PropertiesProvider.GOOGLE_NEARBYSEARCH_URL,
                    latitude, longitude, radius, type, PropertiesProvider.GOOGLE_NEARSEARCH_KEY, nextPageToken)
            parsedData = urlParser.parseURL(nearSearchUrl)
            statusValidator.checkStatusCode(parsedData)
            while(parsedData.status=="INVALID_REQUEST") {
                parsedData = new JsonSlurper().parse(nearSearchUrl)
            }

            nextPageToken = parsedData.next_page_token
        }
    }

    @Override
    List<Place> getNearestPlacesSuperOptimized(Double latitude, Double longitude, Integer radius, String type) {

        geoCoordinatesValidator.checkCoordinatesValidity(latitude, longitude)
        typeValidator.checkTypeValidity(type)

        List<Place> places = new ArrayList<>()

        if (radius != null) {
            fillListOfPlaces(places, latitude, longitude, radius, type)

        } else {
            radius = 500
            fillListOfPlaces(places, latitude, longitude, radius, type)
        }
        return places
    }

    @Override
    int getDirectDistance(Double fromLatitude, Double fromLongitude, Double toLatitude, Double toLongitude) {
        // Calculating a distance between two points
        double radLng = Math.toRadians(fromLongitude - toLongitude)
        double radLat = Math.toRadians(fromLatitude - toLatitude)
        double a = Math.sin(radLat / 2) * Math.sin(radLat / 2) + Math.cos(Math.toRadians(toLatitude))*
                Math.cos(Math.toRadians(fromLatitude))* Math.sin(radLng / 2) * Math.sin(radLng / 2)
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        // receiving the distance in meters
        return (int) (c * PropertiesProvider.EARTH_RADIUS * 1000)
    }

    @Override
    List<Integer> getDistances(URL gettingDistanceUrl) {
        def destinationParsedData = urlParser.parseURL(gettingDistanceUrl)
        statusValidator.checkStatusCode(destinationParsedData)
        JSONObject distanced = destinationParsedData.rows
        List<Integer> distances = new ArrayList<>()
        distanced.elements.each { item ->
            Integer distance = item.distance.value
            distances.add(distance)
        }
        return distances
    }

    @Deprecated
    @Override
    List<Place> getNearestPlacesOptimized(Double latitude, Double longitude, Integer radius, String type) {

        geoCoordinatesValidator.checkCoordinatesValidity(latitude, longitude)
        typeValidator.checkTypeValidity(type)

        def parsedData = urlParser.parseURL(customURLBuilder.buildNearSearchUrl(PropertiesProvider.GOOGLE_NEARBYSEARCH_URL,
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
            URL nearSearchUrl = customURLBuilder.buildNearSearchUrlWithToken(PropertiesProvider.GOOGLE_NEARBYSEARCH_URL,
                    latitude, longitude, radius, type, PropertiesProvider.GOOGLE_NEARSEARCH_KEY, nextPageToken)
            parsedData = urlParser.parseURL(nearSearchUrl)
            statusValidator.checkStatusCode(parsedData)
            while(parsedData.status=="INVALID_REQUEST") {
                parsedData = new JsonSlurper().parse(nearSearchUrl)
            }
            nextPageToken = parsedData.next_page_token
            readDataOpt.call()
        }

        List<Integer> distances = getDistances(customURLBuilder.buildGettingDistanceURL(PropertiesProvider.GOOGLE_DISTANCEMATRIX_URL,
                latitude, longitude, destinations, PropertiesProvider.GOOGLE_DISTANCEMATRIX_KEY))
        for (int i=0; i<places.size(); i++) {
            places.get(i).distance = distances.get(i)
        }
        return places
    }

    @Deprecated
    @Override
    List<Place> getNearestPlaces(Double latitude, Double longitude, Integer radius, String type) {

        def parsedData = new JsonSlurper().parse(customURLBuilder.buildNearSearchUrl(PropertiesProvider.GOOGLE_NEARBYSEARCH_URL,
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
            parsedData = new JsonSlurper().parse(customURLBuilder.buildNearSearchUrlWithToken(
                    PropertiesProvider.GOOGLE_NEARBYSEARCH_URL, latitude, longitude, radius, type,
                    PropertiesProvider.GOOGLE_NEARSEARCH_KEY, nextPageToken))
            nextPageToken = parsedData.next_page_token
            readData.call()
        }

        return places
    }
}
