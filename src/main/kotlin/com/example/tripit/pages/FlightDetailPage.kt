package com.example.tripit.pages

import com.codeborne.selenide.Condition.visible
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.Selenide.`$`
import com.codeborne.selenide.Selenide.`$$`
import com.example.tripit.model.FlightSegment

class FlightDetailPage : BasePage() {
    fun open(url: String, delay: Long): FlightDetailPage {
        println("[INFO] Opening Flight Detail page: $url")
        Selenide.open(url)
        `$`("h1.d-flex").shouldBe(visible)
        sleep(delay)
        return this
    }

    fun extractFlightSegment(dateOverride: String? = null): FlightSegment? {
        return try {
            println("[INFO] Parsing Flight Detail page ...")
            // Extract origin and destination from the h1 heading (e.g., "BER → MUC")
            val heading = `$`("h1.text-32").text()
            val routeRegex = Regex("""([A-Z]{3})\s*(?:→|->|—|-|\n)\s*([A-Z]{3})""")
            val routeMatch = routeRegex.find(heading) ?: return null
            val origin = routeMatch.groupValues[1]
            val destination = routeMatch.groupValues[2]

            // Extract flight number from subtitle (e.g., "LH 1957 (Lufthansa)")
            val subtitle = `$`("span[data-cy=trip-item-subtitle]").text()
            val flightNumRegex = Regex("""([A-Z0-9]+\s+[0-9]+)""")
            val flightNumMatch = flightNumRegex.find(subtitle) ?: return null
            val flightNumber = flightNumMatch.groupValues[1].replace(Regex("""\s+"""), " ")

            // Extract departure info from first timeline cell
            val timelineCells = `$$`("div[data-cy=timeline-cell-content]").toList()
            if (timelineCells.isEmpty()) return null

            val departCell = timelineCells[0]

            // Determine date: prefer header-derived override if present, else fallback to cell text
            val headerDate = dateOverride
            if (headerDate.isNullOrEmpty()) return null

            // Extract departure time and timezone
            val departTime = try { departCell.`$`("span[data-cy=timeline-cell-time]").text().trim() } catch (_: Throwable) { "" }
            val departTimezone = try { departCell.`$`("span[data-cy=timeline-cell-timezone]").text().trim() } catch (_: Throwable) { "" }

            println("[INFO] Parsed flight detail: $headerDate $departTime $departTimezone $origin -> $destination $flightNumber (date=header)")

            FlightSegment(
                flightDate = headerDate,
                flightTime = departTime,
                flightTimeZone = departTimezone,
                origin = origin,
                destination = destination,
                flightNumber = flightNumber
            )
        } catch (e: Throwable) {
            System.err.println("[WARN] Failed to parse Flight Detail page: ${e.message}")
            null
        }
    }
}
