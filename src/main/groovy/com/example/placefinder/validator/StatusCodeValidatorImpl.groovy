package com.example.placefinder.validator

import com.example.placefinder.exception.OverQueryLimitException
import com.example.placefinder.validator.StatusCodeValidator
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
