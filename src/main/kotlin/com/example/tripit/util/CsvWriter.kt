package com.example.tripit.util

import com.example.tripit.model.FlightSegment
import java.io.BufferedWriter
import java.nio.file.Files
import java.nio.file.Path

object CsvWriter {
    fun write(path: Path, rows: List<FlightSegment>) {
        Files.createDirectories(path.parent ?: Path.of("."))
        BufferedWriter(Files.newBufferedWriter(path)).use { w ->
            w.appendLine("flight_date,flight_time,origin,destination,flight_number")
            for (r in rows) {
                w.appendLine(
                    listOf(
                        r.flightDate,
                        r.flightTime,
                        r.origin,
                        r.destination,
                        r.flightNumber
                    ).joinToString(",") { escapeCsv(it) }
                )
            }
        }
    }

    private fun escapeCsv(value: String): String {
        val needsQuotes = value.contains(',') || value.contains('"') || value.contains('\n') || value.contains('\r')
        val escaped = value.replace("\"", "\"\"")
        return if (needsQuotes) "\"$escaped\"" else escaped
    }
}
