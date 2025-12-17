package com.example.tripit.pages

import com.codeborne.selenide.Condition.visible
import com.codeborne.selenide.Selenide.*
import com.example.tripit.Selectors

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

    fun collectTripLinks(): List<String> {
        val tripLinks = mutableListOf<String>()
        var pageSafeguard = 0
        var pageIndex = 1

        while (true) {
            println("[INFO] Collecting trips from Past Trips page #$pageIndex ...")
            val pageTripLinks = `$$`("a.fw-bold[href*=\"/app/trips/\"]")
                .mapNotNull { it.attr("href") }
                .map { toAbsoluteUrl(it) }
            println("[INFO]  - Found ${pageTripLinks.size} trip links on page #$pageIndex")
            tripLinks.addAll(pageTripLinks)

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

    fun openTrip(url: String, generateDelayMillis: Long): TripPage {
        sleep(generateDelayMillis)
        open(url)
        `$`("div.trip-timeline-section-header:nth-child(1) > span:nth-child(1)").shouldBe(visible)
        return TripPage()
    }
}
