package com.example.placeFinder.validation.impl

import com.example.placeFinder.exception.InvalidTypePlaceException
import com.example.placeFinder.validation.TypeValidator
import org.springframework.stereotype.Component

import java.nio.file.Files
import java.nio.file.Paths

@Component
class TypeValidatorImpl implements TypeValidator {

    @Override
    void checkTypeValidity(String type) {
        List<String> allowableTypes = Files.readAllLines(Paths.get(System.getProperty("user.dir") +
                "/src/main/resources/places/allowable-types.txt"))
        if (type != "" && !allowableTypes.contains(type)) {
            throw new InvalidTypePlaceException("Incorrect type place")
        }
    }
}
