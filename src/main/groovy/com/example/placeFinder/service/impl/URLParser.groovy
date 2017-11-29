package com.example.placeFinder.service.impl

import net.sf.json.groovy.JsonSlurper
import org.springframework.stereotype.Component

@Component
class URLParser {

    def parseURL(URL url) {
        return new JsonSlurper().parse(url)
    }

}
