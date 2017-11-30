package com.example.placeFinder.service

import com.example.placeFinder.entity.PlaceInfo
import com.example.placeFinder.entity.Place
import net.sf.json.JSON

interface PlaceService {

    List<Place> getNearestPlacesSuperOptimized(Double latitude, Double longitude, Integer radius, String type)

    List<Place> sortPlacesByDistance(List<Place> places)

    PlaceInfo getInfo(String placeId)

    PlaceInfo createPlaceInfoObject(JSON parsedData)

}