package com.example.placefinder.service

import com.example.placefinder.entity.PlaceInfo
import com.example.placefinder.entity.Place
import net.sf.json.JSON

interface PlaceService {

    List<Place> getNearestPlaces(Double latitude, Double longitude, Integer radius, String type)

    List<Place> sortPlacesByDistance(List<Place> places)

    PlaceInfo getInfo(String placeId)

    PlaceInfo buildPlaceInfo(JSON parsedData)

}