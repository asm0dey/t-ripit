package com.example.tripit

import com.example.tripit.util.CsvWriter
import java.nio.file.Path
import java.time.LocalDate
import java.time.LocalDate.parse
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

fun main(args: Array<String>) {
    val (startDateArg, untilDateArg, outPathArg, cliHeaded) = parseArgs(args)
    val startDate: LocalDate? = try {
        startDateArg?.let { parse(it, ISO_LOCAL_DATE) }
    } catch (_: Exception) {
        null
    }
    val untilDate: LocalDate? = try {
        untilDateArg?.let { parse(it, ISO_LOCAL_DATE) }
    } catch (_: Exception) {
        null
    }
    val outPath = Path.of(outPathArg ?: "flights.csv")

    val username = System.getenv("TRIPIT_USERNAME")
    val password = System.getenv("TRIPIT_PASSWORD")
    val envHeaded = System.getenv("TRIPIT_HEADED")?.let { parseBool(it) } ?: false
    // Precedence: CLI flag > env var > default(false)
    val headed = if (cliHeaded) true else envHeaded

    if (username.isNullOrBlank() || password.isNullOrBlank()) {
        System.err.println("TRIPIT_USERNAME and TRIPIT_PASSWORD environment variables are required.")
        return
    }

    TripItScraper(username = username, password = password, headed = headed).use { scraper ->
        println("Logging in to TripIt...")
        scraper.login()
        println("Login successful. Collecting past flights...")
        CsvWriter.ensureHeader(outPath)
        var count = 0
        scraper
            .extractPastFlights(startDate, untilDate)
            .forEach {
                CsvWriter.append(outPath, it)
                count += 1
                if (count % 5 == 0) println("[INFO] Written $count rows so far...")
            }
        // In case some were filtered by takeWhile without a callback, 'count' should equal flights.size.
        // But we trust the callback and report what was written.
        println("Written $count flight segments to ${outPath.toAbsolutePath()}.")
        println("Done.")
    }
}

private fun parseArgs(args: Array<String>): Quadruple<String?, String?, String?, Boolean> {
    var startDate: String? = null
    var untilDate: String? = null
    var out: String? = null
    var headed = false
    var i = 0
    while (i < args.size) {
        when (args[i]) {
            "--start-date" -> {
                startDate = args.getOrNull(i + 1)
                i += 1
            }

            "--until-date", "--end-date" -> {
                untilDate = args.getOrNull(i + 1)
                i += 1
            }

            "--out" -> {
                out = args.getOrNull(i + 1)
                i += 1
            }

            "--headed" -> headed = true
            "-h", "--help" -> {
                printHelp()
                kotlin.system.exitProcess(0)
            }
        }
        i += 1
    }
    return Quadruple(startDate, untilDate, out, headed)
}

private fun parseBool(value: String): Boolean {
    return when (value.trim().lowercase()) {
        "1", "true", "yes", "y", "on" -> true
        else -> false
    }
}

private fun printHelp() {
    println(
        """
        TripIt Flight Extractor
        Usage: java -jar tripit-extractor.jar [--start-date YYYY-MM-DD] [--until-date YYYY-MM-DD] [--out flights.csv] [--headed]

        Environment variables:
          TRIPIT_USERNAME   TripIt email/username
          TRIPIT_PASSWORD   TripIt password
          TRIPIT_HEADED     If set to a truthy value (true/1/yes/on), launches a visible browser. Default: false

        Options:
          --start-date      Include flights from this date (exclusive). Processing stops when a flight is on or before this date
          --until-date      Skip trips that happened after this date (inclusive upper bound). Only include flights on or before this date
          --out             Output CSV path. Default: ./flights.csv
          --headed          Launch visible browser (default is headless)

        Precedence: CLI flag (--headed) > env (TRIPIT_HEADED) > default (false)
        """.trimIndent()
    )
}

// Simple 4-tuple to avoid adding dependencies
data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
