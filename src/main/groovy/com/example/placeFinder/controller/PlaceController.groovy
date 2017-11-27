package com.example.placeFinder.controller

import com.example.placeFinder.entity.FullPlace
import com.example.placeFinder.entity.Place
import com.example.placeFinder.service.PlaceService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class PlaceController {

    private PlaceService placeService

    @Autowired
    void setPlaceService(PlaceService placeService) {
        this.placeService = placeService
    }

    @GetMapping(value = "/places")
    List<Place> getNearestPlaces(@RequestParam(value = "latitude") Double latitude,
                             @RequestParam(value = "longitude") Double longitude,
                             @RequestParam(value = "radius", required = false, defaultValue = "500") Integer radius,
                             @RequestParam(value = "type", required = false, defaultValue = "") String type) {

        List<Place> nearestPlaces = placeService.getNearestPlaces(latitude, longitude, radius, type)
        placeService.sortPlacesByDistance(nearestPlaces)

        return nearestPlaces
    }

    @GetMapping(value = "/place/{id}")
    FullPlace getPlaceFullInfo(@PathVariable Integer id) {
        return null
    }
}
