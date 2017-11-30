package com.example.placefinder.service

import com.example.placefinder.provider.PropertiesProvider
import com.example.placefinder.urlhandler.URLCreator
import com.example.placefinder.urlhandler.URLParser
import com.example.placefinder.entity.Location
import com.example.placefinder.entity.PlaceInfo
import com.example.placefinder.entity.Place
import com.example.placefinder.entity.enums.ApiProvider
import com.example.placefinder.entity.enums.DataFormat
import com.example.placefinder.validator.GeoCoordinatesValidator
import com.example.placefinder.validator.StatusCodeValidator
import com.example.placefinder.validator.TypeValidator
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
    private URLCreator urlCreator

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
        params.put("placeid", placeId)
        def parsedData = urlParser.parseURL(DataFormat.JSON, urlCreator.create(ApiProvider.GOOGLE_PLACEDETAILS, params))
        return buildPlaceInfo(parsedData)
    }

    @Override
    PlaceInfo buildPlaceInfo(JSON parsedData) {

        String iconUrl = parsedData.result.icon
        String phoneNumber = parsedData.result.international_phone_number
        JSONObject resultJson = parsedData.result
        String isOpenNow =  ((JSONObject) resultJson.get("opening_hours")).get("open_now")
        List<String> schedule = parsedData.result.opening_hours.weekday_text
        String googleMapUrl = parsedData.result.url
        Double rating = parsedData.result.rating
        String name = parsedData.result.name
        List<String> types = parsedData.result.types

        return PlaceInfo.builder()
                .setName(name)
                .setAddress(parsedData.result.formatted_address)
                .setIconUrl(iconUrl)
                .setPhoneNumber(phoneNumber)
                .setIsOpenNow(isOpenNow)
                .setSchedule(schedule)
                .setRating(rating)
                .setGoogleMapUrl(googleMapUrl)
                .setTypes(types)
                .build()
    }

    @Override
    List<Place> getNearestPlaces(Double latitude, Double longitude, Integer radius, String type) {

        geoCoordinatesValidator.checkCoordinatesValidity(latitude, longitude)
        typeValidator.checkTypeValidity(type)

        List<Place> places = new ArrayList<>()
        Location location = new Location(latitude:latitude, longitude:longitude)

     //   int optRadius = (radius != null) ? radius : calcRadius(latitude, longitude, type)
        if (radius != null) {
            fillListOfPlaces(places, location, radius, type)
        } else {
            places = calcOptPlaces(location, type)
        }

        return places
    }

    private List<Place> calcOptPlaces(Location location, String type) {
        List<Place> places = new ArrayList<>()
        readOnce(places, location, PropertiesProvider.DEFAULT_RADIUS, type)
        places.sort(new Comparator<Place>() {
            @Override
            int compare(Place o1, Place o2) {
                return o1.getDistance() - o2.getDistance()
            }
        })
        List<Place> nearestPlaces = new ArrayList<>()

        readOnce(nearestPlaces, location, places.get(0).distance, type)
        //int amount = nearestPlaces.size()

        for(int i=1; places.size() - 1; i++) {
            if (nearestPlaces.size() > PropertiesProvider.MIN_PLACES_TO_SHOW) break
            nearestPlaces.clear()
            readOnce(nearestPlaces, location, places.get(i).distance, type)
        }
        return nearestPlaces
    }

    private void readOnce(List<Place> places, Location location, Integer radius, String type) {
        Map<String, Object> params = new HashMap<>()
        params.put("location", location.latitude + "," + location.longitude)
        params.put("radius", radius)
        params.put("type", type)

        def parsedData = urlParser.parseURL(DataFormat.JSON, urlCreator.create(ApiProvider.GOOGLE_NEARSEARCH, params))
        statusValidator.checkStatusCode(parsedData)

        parsedData.results.each { placeItem ->
            Double itemLatitude = placeItem.geometry.location.lat
            Double itemLongitude = placeItem.geometry.location.lng
            int directDistance = locationService.calcDistanceBetween(
                    location, new Location(latitude: itemLatitude, longitude: itemLongitude))
            places.add(new Place(
                    name: placeItem.name,
                    rating: placeItem.rating,
                    placeId: placeItem.place_id,
                    distance: directDistance))
        }
    }

    private void fillListOfPlaces(List<Place> places, Location location, Integer radius, String type) {
        Map<String, Object> params = new HashMap<>()

        params.put("location", location.latitude + "," + location.longitude)
        params.put("radius", radius)
        params.put("type", type)

        def parsedData = urlParser.parseURL(DataFormat.JSON, urlCreator.create(ApiProvider.GOOGLE_NEARSEARCH, params))

        statusValidator.checkStatusCode(parsedData)

        String nextPageToken = parsedData.next_page_token
        while(true) {
            parsedData.results.each { placeItem ->
                Double itemLatitude = placeItem.geometry.location.lat
                Double itemLongitude = placeItem.geometry.location.lng
                int directDistance = locationService.calcDistanceBetween(
                        location, new Location(latitude: itemLatitude, longitude: itemLongitude))
                places.add(new Place(
                        name: placeItem.name,
                        rating: placeItem.rating,
                        placeId: placeItem.place_id,
                        distance: directDistance))
            }
            if (nextPageToken == null) break

            params.put("pagetoken", nextPageToken)
            parsedData = urlParser.parseURL(DataFormat.JSON, urlCreator.create(ApiProvider.GOOGLE_NEARSEARCH, params))
            statusValidator.checkStatusCode(parsedData)
            while(parsedData.status=="INVALID_REQUEST") {
                parsedData = urlParser.parseURL(DataFormat.JSON, urlCreator.create(ApiProvider.GOOGLE_NEARSEARCH, params))
            }
            nextPageToken = parsedData.next_page_token
        }
    }

    @Deprecated
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
