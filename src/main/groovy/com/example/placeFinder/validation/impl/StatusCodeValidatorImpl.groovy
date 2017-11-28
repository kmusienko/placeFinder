package com.example.placeFinder.validation.impl

import com.example.placeFinder.exception.OverQueryLimitException
import com.example.placeFinder.validation.StatusCodeValidator
import org.springframework.stereotype.Component

@Component
class StatusCodeValidatorImpl implements StatusCodeValidator {

    @Override
    void checkStatusCode(Object parsedData) {
        if (parsedData.status == "OVER_QUERY_LIMIT") {
            throw new OverQueryLimitException((String)parsedData.status)
        }
    }
}
