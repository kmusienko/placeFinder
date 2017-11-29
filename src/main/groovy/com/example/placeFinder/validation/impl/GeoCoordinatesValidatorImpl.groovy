package com.example.placeFinder.validation.impl

import com.example.placeFinder.exception.InvalidGeoCoordinatesException
import com.example.placeFinder.validation.GeoCoordinatesValidator
import org.springframework.stereotype.Component

@Component
class GeoCoordinatesValidatorImpl implements GeoCoordinatesValidator {

    void checkCoordinatesValidity(Double latitude, Double longitude) {
        if (latitude >= 90 || latitude <= -90 || longitude >= 180 || longitude <= -180) {
            throw new InvalidGeoCoordinatesException("Invalid latitude or longitude value")
        }
    }

}
