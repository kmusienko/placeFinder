package com.example.placeFinder.controller

import com.example.placeFinder.exception.InvalidTypeException

import java.nio.file.Files
import java.nio.file.Paths

class Main {
    public static void main(String[] args) {
        Double a = 48.464816616831456
        println a
        Double b = 35.05094647407532
        println b
        String gg = "fsa"
        List<String> allowableTypes = Files.readAllLines(Paths.get(System.getProperty("user.dir") +
                "/src/main/resources/places/allowable-types.txt"))
        if (!allowableTypes.contains(gg)) {
            throw new InvalidTypeException("dfs")
        }
    }
}
