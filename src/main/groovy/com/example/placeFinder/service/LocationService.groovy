package com.example.placeFinder.service

import com.example.placeFinder.entity.Location

interface LocationService {

    int calcDistanceBetween(Location from, Location to)

}