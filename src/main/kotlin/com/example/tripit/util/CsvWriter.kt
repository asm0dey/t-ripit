package com.example.tripit.util

import com.example.tripit.model.FlightSegment
import java.io.BufferedWriter
import java.nio.file.Files
import java.nio.file.Path

object CsvWriter {

    fun ensureHeader(path: Path) {
        Files.createDirectories(path.parent ?: Path.of("."))
        if (!Files.exists(path) || Files.size(path) == 0L) {
            BufferedWriter(
                Files.newBufferedWriter(
                    path,
                    java.nio.file.StandardOpenOption.CREATE,
                    java.nio.file.StandardOpenOption.TRUNCATE_EXISTING
                )
            ).use { w ->
                w.appendLine("flight_date,flight_time,origin,destination,flight_number")
            }
        }
    }

    fun append(path: Path, row: FlightSegment) {
        ensureHeader(path)
        BufferedWriter(
            Files.newBufferedWriter(
                path,
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.APPEND
            )
        ).use { w ->
            w.appendLine(formatRow(row))
        }
    }

    private fun formatRow(r: FlightSegment): String = listOf(
        r.flightDate,
        r.flightTime,
        r.origin,
        r.destination,
        r.flightNumber
    ).joinToString(",") { escapeCsv(it) }

    private fun escapeCsv(value: String): String {
        val needsQuotes = value.contains(',') || value.contains('"') || value.contains('\n') || value.contains('\r')
        val escaped = value.replace("\"", "\"\"")
        return if (needsQuotes) "\"$escaped\"" else escaped
    }
}
