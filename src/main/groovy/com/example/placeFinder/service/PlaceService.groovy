package com.example.placeFinder.service

import com.example.placeFinder.entity.InfoPlace
import com.example.placeFinder.entity.Place

interface PlaceService {

    List<Place> getNearestPlaces(Double latitude, Double longitude, Integer radius, String type)

    List<Place> sortPlacesByDistance(List<Place> places)

    List<Place> getNearestPlacesOptimized(Double latitude, Double longitude, Integer radius, String type)

    InfoPlace getInfo(String placeId)

    List<Integer> getDistances(URL gettingDistanceUrl)

}