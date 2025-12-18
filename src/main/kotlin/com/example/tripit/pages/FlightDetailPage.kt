package com.example.tripit.pages

import com.codeborne.selenide.Condition.visible
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.Selenide.element
import com.codeborne.selenide.Selenide.`$$`
import com.example.tripit.Selectors
import com.example.tripit.Selectors.TIMELINE_CELL_CONTENT
import com.example.tripit.Selectors.TIMELINE_CELL_TIME
import com.example.tripit.Selectors.TIMELINE_CELL_TIMEZONE
import com.example.tripit.Selectors.TRIP_ITEM_SUBTITLE
import com.example.tripit.model.FlightSegment
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class FlightDetailPage : BasePage() {
    fun open(url: String, delay: Long): FlightDetailPage {
        println("[INFO] Opening Flight Detail page: $url")
        Selenide.open(url)
        element(Selectors.FLIGHT_DETAIL_HEADER).shouldBe(visible)
        sleep(delay)
        return this
    }

    fun extractFlightSegment(dateOverride: String? = null): FlightSegment? {
        return try {
            println("[INFO] Parsing Flight Detail page ...")
            // Extract origin and destination from the h1 heading (e.g., "BER → MUC")
            val heading = element(Selectors.FLIGHT_ROUTE_HEADING).text()
            val routeRegex = Regex("""([A-Z]{3})\s*(?:→|->|—|-|\n)\s*([A-Z]{3})""")
            val routeMatch = routeRegex.find(heading) ?: return null
            val origin = routeMatch.groupValues[1]
            val destination = routeMatch.groupValues[2]

            // Extract flight number from subtitle (e.g., "LH 1957 (Lufthansa)")
            val subtitle = element(TRIP_ITEM_SUBTITLE).text()
            val flightNumRegex = Regex("""([A-Z0-9]+\s+[0-9]+)""")
            val flightNumMatch = flightNumRegex.find(subtitle) ?: return null
            val flightNumber = flightNumMatch.groupValues[1].replace(Regex("""\s+"""), " ")

            // Extract departure info from first timeline cell
            val timelineCells = `$$`(TIMELINE_CELL_CONTENT).toList()
            if (timelineCells.isEmpty()) return null

            val departCell = timelineCells[0]

            // Determine date: prefer header-derived override if present, else fallback to cell text
            val headerDate = dateOverride
            if (headerDate.isNullOrEmpty()) return null

            // Extract departure time and timezone
            val departTime = try {
                departCell.find(TIMELINE_CELL_TIME).text().trim()
            } catch (_: Throwable) {
                ""
            }
            val departTimezone = try {
                departCell.find(TIMELINE_CELL_TIMEZONE).text().trim()
            } catch (_: Throwable) {
                ""
            }

            val arriveTime = try {
                timeTo24h(timelineCells[1].find(TIMELINE_CELL_TIME).text().trim())
            } catch (_: Throwable) {
                ""
            }
            val arriveTimezone = try {
                timelineCells[1].find(TIMELINE_CELL_TIMEZONE).text().trim()
            } catch (_: Throwable) {
                ""
            }

            println("[INFO] Parsed flight detail: $headerDate $departTime $departTimezone $origin -> $destination $flightNumber (date=header)")

            FlightSegment(
                flightDate = headerDate,
                flightTime = timeTo24h(departTime),
                flightTimeZone = departTimezone,
                arriveTime = arriveTime,
                arriveTimeZone = arriveTimezone,
                origin = origin,
                destination = destination,
                flightNumber = flightNumber
            )
        } catch (e: Throwable) {
            System.err.println("[WARN] Failed to parse Flight Detail page: ${e.message}")
            null
        }
    }

    private fun timeTo24h(timeIn12h: String): String {
        return LocalTime.parse(timeIn12h, DateTimeFormatter.ofPattern("hh:mm a", Locale.US)).toString()
    }
}
