package com.example.placeFinder.service

import com.example.placeFinder.entity.PlaceInfo
import com.example.placeFinder.entity.Place
import net.sf.json.JSON

interface PlaceService {

    List<Place> getNearestPlacesSuperOptimized(Double latitude, Double longitude, Integer radius, String type)

    List<Place> sortPlacesByDistance(List<Place> places)

    PlaceInfo getInfo(String placeId)

    List<Integer> getDistances(URL gettingDistanceUrl)

    PlaceInfo createPlaceInfoObject(JSON parsedData)

    int getDirectDistance(Double fromLatitude, Double fromLongitude, Double toLatitude, Double toLongitude)

    int calcRadius(Double latitude, Double longitude, String type)

    List<Place> getNearestPlaces(Double latitude, Double longitude, Integer radius, String type)

    List<Place> getNearestPlacesOptimized(Double latitude, Double longitude, Integer radius, String type)

}