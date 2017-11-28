package com.example.placeFinder.service.impl

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class PropertiesProvider {

    //@Value('${google.key.nearbysearch}')
    final static String GOOGLE_NEARSEARCH_KEY = "AIzaSyC8XKSfZMXsQg27J6iZzV7POa53DxKnx7U"

    //@Value('${google.key.distancematrix}')
    final static String GOOGLE_DISTANCEMATRIX_KEY = "AIzaSyCg7fbFoVhOUUt1gRTp2OVoijVEyDTkCBs"

    //@Value('${nearbysearch.url}')
    final static String GOOGLE_NEARBYSEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"

    //@Value('${distancematrix.url}')
    final static String GOOGLE_DISTANCEMATRIX_URL = "https://maps.googleapis.com/maps/api/distancematrix/json"

    //@Value('${placedetails.url}')
    final static String GOOGLE_PLACEDETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json"
}