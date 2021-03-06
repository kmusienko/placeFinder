package com.example.placefinder.provider

import org.springframework.stereotype.Component

@Component
class PropertiesProvider {

    //@Value('${google.key.nearbysearch}')
    final static String GOOGLE_NEARSEARCH_KEY = "AIzaSyB-LxRAoEEl9xPAra8ktpvOdYp5TeGuGb0"

    //@Value('${nearbysearch.url}')
    final static String GOOGLE_NEARBYSEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"

    //@Value('${placedetails.url}')
    final static String GOOGLE_PLACEDETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json"

    final static double EARTH_RADIUS = 6371

    final static int DEFAULT_RADIUS = 1000

    //Accuracy of the optimal radius
    final static int EPS = 20

    final static int MAX_GOOGLE_PLACES = 60

    final static int MIN_PLACES_TO_SHOW = 10
}
