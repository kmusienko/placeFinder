package com.example.placeFinder.service.impl

import com.example.placeFinder.entity.Location
import com.example.placeFinder.entity.PlaceInfo
import com.example.placeFinder.entity.Place
import com.example.placeFinder.entity.enums.ApiProvider
import com.example.placeFinder.entity.enums.DataFormat
import com.example.placeFinder.service.LocationService
import com.example.placeFinder.service.PlaceService
import com.example.placeFinder.validation.GeoCoordinatesValidator
import com.example.placeFinder.validation.StatusCodeValidator
import com.example.placeFinder.validation.TypeValidator
import net.sf.json.JSON
import net.sf.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PlaceServiceImpl implements PlaceService {

    @Autowired
    private TypeValidator typeValidator

    @Autowired
    private StatusCodeValidator statusValidator

    @Autowired
    private GeoCoordinatesValidator geoCoordinatesValidator

    @Autowired
    private LocationService locationService

    @Autowired
    private URLParser urlParser

    @Autowired
    private URLBuilder urlBuilder

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
        Map<String, Object> params = new HashMap<>()
        params.put("placeId", placeId)
        def parsedData = urlParser.parseURL(DataFormat.JSON, urlBuilder.parse(ApiProvider.GOOGLE_PLACEDETAILS, params))
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

    private void fillListOfPlaces(List<Place> places, Double latitude, Double longitude, Integer radius, String type) {
        Map<String, Object> params = new HashMap<>()
        params.put("latitude", latitude)
        params.put("longitude",longitude)
        params.put("radius", radius)
        params.put("type", type)

        def parsedData = urlParser.parseURL(DataFormat.JSON, urlBuilder.parse(ApiProvider.GOOGLE_NEARSEARCH, params))

        statusValidator.checkStatusCode(parsedData)

        String nextPageToken = parsedData.next_page_token
        while(true) {
            parsedData.results.each { placeItem ->
                Double itemLatitude = placeItem.geometry.location.lat
                Double itemLongitude = placeItem.geometry.location.lng
                int directDistance = locationService.calcDistanceBetween(
                        new Location(latitude: latitude, longitude:longitude),
                        new Location(latitude: itemLatitude, longitude: itemLongitude))
                places.add(new Place(name: placeItem.name, rating: placeItem.rating, placeId: placeItem.place_id,
                        distance: directDistance))
            }
            if (nextPageToken == null) break

            params.put("nextPageToken", nextPageToken)
            parsedData = urlParser.parseURL(DataFormat.JSON, urlBuilder.parse(ApiProvider.GOOGLE_NEARSEARCH, params))
            statusValidator.checkStatusCode(parsedData)
            while(parsedData.status=="INVALID_REQUEST") {
                parsedData = urlParser.parseURL(DataFormat.JSON, urlBuilder.parse(ApiProvider.GOOGLE_NEARSEARCH, params))
            }
            nextPageToken = parsedData.next_page_token
        }
    }

    @Override
    List<Place> getNearestPlacesSuperOptimized(Double latitude, Double longitude, Integer radius, String type) {

        geoCoordinatesValidator.checkCoordinatesValidity(latitude, longitude)
        typeValidator.checkTypeValidity(type)

        List<Place> places = new ArrayList<>()

        int optRadius = (radius != null) ? radius : calcRadius(latitude, longitude, type)

        fillListOfPlaces(places, latitude, longitude, optRadius, type)

        return places
    }

    private int calcRadius(Double latitude, Double longitude, String type) {
        //Calculating the radius by binary search
        List<Place> places = new ArrayList<>()
        int begin = 0
        int end = PropertiesProvider.DEFAULT_RADIUS
        fillListOfPlaces(places, latitude, longitude, end, type)
        if (places.size() == PropertiesProvider.MAX_GOOGLE_PLACES) {
            places.clear()
            while(end - begin > PropertiesProvider.EPS) {
                int newRadius = (begin + end) / 2
                places.clear()
                fillListOfPlaces(places, latitude, longitude, newRadius, type)
                if (places.size() < PropertiesProvider.MAX_GOOGLE_PLACES) {
                    begin = newRadius
                } else {
                    end = newRadius
                }
            }
        }
        return begin
    }

}
