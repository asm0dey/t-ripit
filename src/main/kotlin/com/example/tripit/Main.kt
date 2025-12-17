package com.example.tripit

import com.example.tripit.model.FlightSegment
import com.example.tripit.util.CsvWriter
import java.nio.file.Path
import java.time.LocalDate
import java.time.LocalDate.parse
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

fun main(args: Array<String>) {
    val (startDateArg, outPathArg, cliHeaded) = parseArgs(args)
    val startDate: LocalDate? = try {
        startDateArg?.let { parse(it, ISO_LOCAL_DATE) }
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
        val flights: List<FlightSegment> = scraper.extractPastFlights(startDate)
        println("Found ${flights.size} flight segments. Writing CSV to ${outPath.toAbsolutePath()} ...")
        CsvWriter.write(outPath, flights)
        println("Done.")
    }
}

private fun parseArgs(args: Array<String>): Triple<String?, String?, Boolean> {
    var startDate: String? = null
    var out: String? = null
    var headed = false
    var i = 0
    while (i < args.size) {
        when (args[i]) {
            "--start-date" -> {
                startDate = args.getOrNull(i + 1)
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
    return Triple(startDate, out, headed)
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
        Usage: java -jar tripit-extractor.jar [--start-date YYYY-MM-DD] [--out flights.csv] [--headed]

        Environment variables:
          TRIPIT_USERNAME   TripIt email/username
          TRIPIT_PASSWORD   TripIt password
          TRIPIT_HEADED     If set to a truthy value (true/1/yes/on), launches a visible browser. Default: false

        Options:
          --start-date      Include flights from this date (inclusive). Default: all past flights
          --out             Output CSV path. Default: ./flights.csv
          --headed          Launch visible browser (default is headless)

        Precedence: CLI flag (--headed) > env (TRIPIT_HEADED) > default (false)
        """.trimIndent()
    )
}
