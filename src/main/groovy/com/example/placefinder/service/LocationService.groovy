package com.example.placefinder.service

import com.example.placefinder.entity.Location

interface LocationService {

    int calcDistanceBetween(Location from, Location to)

}