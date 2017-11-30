package com.example.placefinder.service

import com.example.placefinder.provider.PropertiesProvider
import com.example.placefinder.entity.Location
import org.springframework.stereotype.Service

@Service
class LocationServiceImpl implements LocationService {

    @Override
    int calcDistanceBetween(Location from, Location to) {
        // Calculating the direct distance between two points
        double radLng = Math.toRadians(from.longitude - to.longitude)
        double radLat = Math.toRadians(from.latitude - to.latitude)
        double a = Math.sin(radLat / 2) * Math.sin(radLat / 2) + Math.cos(Math.toRadians(to.latitude))*
                Math.cos(Math.toRadians(from.latitude))* Math.sin(radLng / 2) * Math.sin(radLng / 2)
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        // receiving the distance in meters
        return (int) (c * PropertiesProvider.EARTH_RADIUS * 1000)
    }
}
