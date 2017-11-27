package com.example.placeFinder.service

import com.example.placeFinder.entity.Place

interface PlaceService {

    List<Place> getNearestPlaces(String latitude, String longitude, Integer radius, String type)

    List<Place> sortPlacesByDistance(List<Place> places)

}