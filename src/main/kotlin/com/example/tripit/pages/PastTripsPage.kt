package com.example.tripit.pages

import com.codeborne.selenide.Condition.visible
import com.codeborne.selenide.Selenide.*
import com.example.tripit.Selectors
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class PastTripsPage : BasePage() {
    fun open(delay: Long): PastTripsPage {
        sleep(delay)
        open(Selectors.PAST_TRIPS_URL)
        `$`("li.p-0:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1)").shouldBe(
            visible
        )
        try {
            element(".pagination").shouldBe(visible)
        } catch (_: Exception) {
            System.err.println("No pagination found, won't paginate")
        }
        return this
    }

    fun collectTripLinks(
        startDateInclusive: LocalDate? = null,
        untilDateInclusive: LocalDate? = null
    ): List<String> {
        val tripLinks = mutableListOf<String>()
        var pageSafeguard = 0
        var pageIndex = 1

        while (true) {
            println("[INFO] Collecting trips from Past Trips page #$pageIndex ...")
            // For early filtering, iterate over trip cards and parse the visible trip date range
            val cards = `$$`("div.card-body > div.container").toList()
            var addedOnThisPage = 0
            cards.forEach { card ->
                val link = card.`$`("a.fw-bold[href*=\"/app/trips/\"]")
                if (!link.exists()) return@forEach
                val href = link.attr("href") ?: return@forEach
                val dateText = card.`$`("div[data-cy=\"trip-date-span\"] > span").text()
                val (tripStart, tripEnd) = parseTripDateRange(dateText) ?: (null to null)

                val include = if (tripStart != null && tripEnd != null) {
                    // Include if overlaps with requested window
                    val afterStart = startDateInclusive?.let { !tripEnd.isBefore(it) } ?: true
                    val beforeEnd = untilDateInclusive?.let { !tripStart.isAfter(it) } ?: true
                    afterStart && beforeEnd
                } else {
                    // If can't parse, keep it to avoid false negatives
                    true
                }

                if (include) {
                    tripLinks.add(toAbsoluteUrl(href))
                    addedOnThisPage++
                }
            }
            println("[INFO]  - Added $addedOnThisPage filtered trip links on page #$pageIndex (from ${cards.size} cards)")

            val pageItems = `$$`(Selectors.PAGE_ITEM).toList()
            val nextLi = pageItems.firstOrNull { it.`$`("li:not(.disabled) > button[aria-label=\"Go to next page\"]").exists() }
            if (nextLi == null) break

            val nextLiClass = try {
                nextLi.getAttribute("class") ?: ""
            } catch (_: Throwable) {
                ""
            }
            if (nextLiClass.contains("disabled")) break

            try {
                nextLi.`$`("button.page-link").click()
                `$`(".pagination").shouldBe(visible)
            } catch (_: Throwable) {
                break
            }
            sleep(900)
            pageSafeguard++
            pageIndex++
            if (pageSafeguard > 50) break
        }

        return tripLinks
    }

    private fun parseTripDateRange(text: String): Pair<LocalDate, LocalDate>? {
        if (text.isBlank()) return null
        val trimmed = text.substringBefore(" (").trim()
        val locale = Locale.ENGLISH
        val monthFormatter = DateTimeFormatter.ofPattern("MMM", locale)
        val fullFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", locale)

        // Pattern D: "Oct 22 - Nov 1, 2025" (cross-month, same year or cross-year with only trailing year)
        // Handles both same-year cross-month and implied cross-year when month2 < month1
        val regexD = Regex("^([A-Za-z]{3}) (\\d{1,2}) - ([A-Za-z]{3}) (\\d{1,2}), (\\d{4})$")
        regexD.matchEntire(trimmed)?.let { m ->
            val (m1, d1, m2, d2, y) = m.destructured
            val month1 = java.time.Month.from(monthFormatter.parse(m1)).value
            val month2 = java.time.Month.from(monthFormatter.parse(m2)).value
            val year = y.toInt()
            val startYear = year - if (month2 < month1) 1 else 0
            val start = LocalDate.of(startYear, month1, d1.toInt())
            val end = LocalDate.of(year, month2, d2.toInt())
            return start to end
        }

        // Pattern C: "Dec 31, 2024 - Jan 2, 2025"
        val regexC = Regex("^([A-Za-z]{3}) (\\d{1,2}), (\\d{4}) - ([A-Za-z]{3}) (\\d{1,2}), (\\d{4})$")
        regexC.matchEntire(trimmed)?.let { m ->
            val (m1, d1, y1, m2, d2, y2) = m.destructured
            val start = LocalDate.of(y1.toInt(), java.time.Month.from(monthFormatter.parse(m1)).value, d1.toInt())
            val end = LocalDate.of(y2.toInt(), java.time.Month.from(monthFormatter.parse(m2)).value, d2.toInt())
            return start to end
        }

        // Pattern A: "Nov 20 - 22, 2025" (same month/year)
        val regexA = Regex("^([A-Za-z]{3}) (\\d{1,2}) - (\\d{1,2}), (\\d{4})$")
        regexA.matchEntire(trimmed)?.let { m ->
            val (m1, d1, d2, y) = m.destructured
            val month = java.time.Month.from(monthFormatter.parse(m1)).value
            val year = y.toInt()
            val start = LocalDate.of(year, month, d1.toInt())
            val end = LocalDate.of(year, month, d2.toInt())
            return start to end
        }

        // Pattern B: "Jun 14, 2018" (single day)
        runCatching {
            val date = LocalDate.parse(trimmed, fullFormatter)
            return date to date
        }

        return null
    }

    fun openTrip(url: String, generateDelayMillis: Long): TripPage {
        sleep(generateDelayMillis)
        open(url)
        `$`("div.trip-timeline-section-header:nth-child(1) > span:nth-child(1)").shouldBe(visible)
        return TripPage()
    }
}
