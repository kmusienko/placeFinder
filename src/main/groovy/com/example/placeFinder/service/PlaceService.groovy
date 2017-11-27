package com.example.placeFinder.service

import com.example.placeFinder.entity.Place

interface PlaceService {

    List<Place> getNearestPlaces(Double latitude, Double longitude, Integer radius, String type)

    List<Place> sortPlacesByDistance(List<Place> places)

    List<Place> getNearestPlacesV2(Double latitude, Double longitude, Integer radius, String type)

}