package com.example.placefinder.validator

import com.example.placefinder.exception.InvalidGeoCoordinatesException
import com.example.placefinder.validator.GeoCoordinatesValidator
import org.springframework.stereotype.Component

@Component
class GeoCoordinatesValidatorImpl implements GeoCoordinatesValidator {

    @Override
    void checkCoordinatesValidity(Double latitude, Double longitude) {
        if (latitude >= 90 || latitude <= -90 || longitude >= 180 || longitude <= -180) {
            throw new InvalidGeoCoordinatesException("Invalid latitude or longitude value")
        }
    }

}
