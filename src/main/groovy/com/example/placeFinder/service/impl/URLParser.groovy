package com.example.placeFinder.service.impl

import com.example.placeFinder.entity.enums.DataFormat
import net.sf.json.groovy.JsonSlurper
import org.springframework.stereotype.Component

@Component
class URLParser {

    def parseURL(DataFormat dataFormat, URL url) {
        if (dataFormat.name() == "JSON") {
            return new JsonSlurper().parse(url)
        }

    }

}
