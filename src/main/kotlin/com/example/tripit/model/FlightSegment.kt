package com.example.tripit.model

data class FlightSegment(
    val flightDate: String,      // YYYY-MM-DD
    val flightTime: String,      // e.g., "9:23 PM PST"
    val origin: String,          // IATA code e.g., SFO
    val destination: String,     // IATA code e.g., PIT
    val flightNumber: String,     // e.g., UA794
    val flightTimeZone: String
)
