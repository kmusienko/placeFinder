package com.example.placefinder.urlhandler

import com.example.placefinder.entity.enums.ApiProvider
import com.example.placefinder.provider.PropertiesProvider
import org.springframework.stereotype.Component

@Component
class URLCreator {

    URL create(ApiProvider apiProvider, Map<String, Object> params) {
        String apiStatement
        String apiKey
        switch(apiProvider) {
            case ApiProvider.GOOGLE_NEARSEARCH :
                apiStatement = PropertiesProvider.GOOGLE_NEARBYSEARCH_URL
                apiKey = PropertiesProvider.GOOGLE_NEARSEARCH_KEY
                break
            case ApiProvider.GOOGLE_PLACEDETAILS :
                apiStatement = PropertiesProvider.GOOGLE_PLACEDETAILS_URL
                apiKey = PropertiesProvider.GOOGLE_NEARSEARCH_KEY
                break
        }
        StringBuilder url = new StringBuilder()
        url.append(apiStatement).append("?")
        params.each {param ->
            url
                    .append(param.getKey())
                    .append("=")
                    .append(param.getValue())
                    .append("&")
        }
        if (apiKey != null) {
            url.append("key=").append(apiKey)
        }
        return new URL(url.toString())
    }
}
