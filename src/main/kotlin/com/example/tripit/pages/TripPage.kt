package com.example.tripit.pages

import com.codeborne.selenide.Selenide.`$`
import com.codeborne.selenide.Selenide.element

class TripPage : BasePage() {
    /**
     * Returns list of pairs: (absolute flight detail URL, closest header date text)
     * The date is taken from the nearest preceding timeline section header element
     * e.g., div[data-cy="trip-timeline-section-header"] span[data-cy="timeline-header-date"].
     */
    fun flightLinks(): List<Pair<String, String>> {
        val year = extractYear()
        var currentDate = ""
        val result = mutableListOf<Pair<String, String>>()
        element("div[role=\"list\"]")
            .`$$`("div[data-cy=trip-timeline-section-header], div[data-cy=trip-timeline-segment]")
            .forEach {
                if (it.getAttribute("data-cy") == "trip-timeline-section-header") {
                    currentDate = parseDate(it.`$`("span[data-cy=timeline-header-date]").text, year) ?: currentDate
                } else if (it.getAttribute("data-cy") == "trip-timeline-segment" &&
                    it.`$`("svg[aria-label='flight']").exists()
                ) {
                    val link = it.`$`("a[data-cy=timeline-title]").getAttribute("href")!!
                    result.add(Pair(toAbsoluteUrl(link), currentDate))
                }
            }
        return result
    }

    private fun extractYear(): Int {
        val yearPattern = Regex(""",\s*(\d{4})""")
        val match = yearPattern.find(`$`("span.p-0:nth-child(1)").text())
        return match?.groupValues?.get(1)?.toIntOrNull()!!
    }
}

private val monthMap = mapOf(
    "jan" to 1, "january" to 1,
    "feb" to 2, "february" to 2,
    "mar" to 3, "march" to 3,
    "apr" to 4, "april" to 4,
    "may" to 5,
    "jun" to 6, "june" to 6,
    "jul" to 7, "july" to 7,
    "aug" to 8, "august" to 8,
    "sep" to 9, "sept" to 9, "september" to 9,
    "oct" to 10, "october" to 10,
    "nov" to 11, "november" to 11,
    "dec" to 12, "december" to 12
)

fun parseDate(fullStr: String, year: Int): String? {
    // parse date from text in format "Thu, Sept 18" or "Sat, Dec 28 2024"
    if (fullStr == "No Date") return null

    val datePattern = Regex("""\w+,\s*(\w+)\s+(\d+)(?:\s+(\d{4}))?""")
    val match = datePattern.find(fullStr) ?: return null
    val monthStr = match.groupValues[1]
    val day = match.groupValues[2].toIntOrNull() ?: return null
    val actualYear = match.groupValues[3].toIntOrNull() ?: year
    val month = monthMap[monthStr.lowercase()] ?: return null
    return "%04d-%02d-%02d".format(actualYear, month, day)
}

