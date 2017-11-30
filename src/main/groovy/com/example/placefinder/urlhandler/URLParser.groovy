package com.example.placefinder.urlhandler

import com.example.placefinder.entity.enums.DataFormat
import net.sf.json.groovy.JsonSlurper
import org.springframework.stereotype.Component

@Component
class URLParser {

    def parseURL(DataFormat dataFormat, URL url) {
        if (dataFormat == DataFormat.JSON) {
            return new JsonSlurper().parse(url)
        }
    }

}
