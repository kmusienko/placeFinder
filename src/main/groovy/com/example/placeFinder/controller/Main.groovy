package com.example.placeFinder.controller

import com.example.placeFinder.service.impl.PropertiesProvider

class Main {
    public static void main(String[] args) {
//        PropertiesProvider propertiesProvider = new PropertiesProvider()
//        String str = propertiesProvider.GOOGLE_NEARSEARCH_KEY
//        println()
        int g = 15/2
        println g
        List<Integer> numbers = new ArrayList<>()
        for (int i=0;i<10;i++) {
            numbers.add(i)
        }
        //6
        int a = 0
        int b = numbers.size() - 1
        while(b - a > 1) {
            int c = (a + b) / 2
            if (numbers.get(c) < 6) {
                a=c
            } else {
                b=c
            }
        }
        println(a)
    }
}
