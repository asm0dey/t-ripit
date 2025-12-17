package com.example.tripit

import com.codeborne.selenide.Configuration
import com.codeborne.selenide.Selenide
import com.example.tripit.model.FlightSegment
import com.example.tripit.pages.FlightDetailPage
import com.example.tripit.pages.LoginPage
import com.example.tripit.pages.PastTripsPage
import java.time.LocalDate
import kotlin.random.Random.Default.nextLong

class TripItScraper(
    private val username: String,
    private val password: String,
    headed: Boolean = false,
    private val politeDelayMs: Long = 600L
) : AutoCloseable {

    init {
        // Configure Selenide / Selenium
        Configuration.browser = System.getenv("TRIPIT_BROWSER") ?: "chrome"
        Configuration.headless = !headed
        Configuration.browserSize = "1400x900"
        Configuration.timeout = 20000
    }

    override fun close() {
        Selenide.closeWebDriver()
    }

    fun login() {
        LoginPage().open().login(username, password, politeDelayMs)
    }

    fun extractPastFlights(
        startDateInclusive: LocalDate? = null,
        untilDateInclusive: LocalDate? = null
    ): Sequence<FlightSegment> {

        return PastTripsPage()
            .open(generateDelayMillis())
            .collectTripLinks()
            .asSequence()
            .onEach { println("[INFO] Processing trip link: $it") }
            .flatMap {
                PastTripsPage()
                    .open(generateDelayMillis())
                    .openTrip(it, generateDelayMillis())
                    .flightLinks()
                    .filter { (_, headerDate) ->
                        val parse = LocalDate.parse(headerDate)
                        (startDateInclusive == null || parse >= startDateInclusive) &&
                                (untilDateInclusive == null || parse <= untilDateInclusive)
                    }
            }
            .mapNotNull { (flightDetailUrl, headerDate) ->
                FlightDetailPage()
                    .open(flightDetailUrl, generateDelayMillis())
                    .extractFlightSegment(dateOverride = headerDate)
            }
            // Preserve existing lower bound short-circuit: stop once we reach or pass the startDateInclusive
            .takeWhile { startDateInclusive == null || LocalDate.parse(it.flightDate) > startDateInclusive }

    }

    private fun generateDelayMillis(): Long = nextLong(politeDelayMs - 200, politeDelayMs + 200)
}
