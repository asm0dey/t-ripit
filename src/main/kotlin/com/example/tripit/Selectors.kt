package com.example.tripit

object Selectors {
    // Login page
    const val LOGIN_URL = "https://www.tripit.com/account/signin"
    const val USERNAME_INPUT = "#email_address"
    const val PASSWORD_INPUT = "#password"
    const val SIGNIN_BUTTON = "#signin-submit-btn"
    const val NAV_LINK = "li.nav-item a.nav-link"

    // After login
    const val PAST_TRIPS_URL = "https://www.tripit.com/app/trips?trips_filter=past"

    // Past Trips Page
    const val TRIP_LIST_CONTAINER = "li.p-0:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1) > div:nth-child(1)"
    const val PAGINATION_CONTAINER = ".pagination"
    const val TRIP_CARD = "div.card-body > div.container"
    const val TRIP_LINK = "a.fw-bold[href*=\"/app/trips/\"]"
    const val TRIP_DATE_SPAN = "div[data-cy=\"trip-date-span\"] > span"
    const val PAGE_ITEM = ".pagination .page-item"
    const val NEXT_PAGE_BUTTON = "li:not(.disabled) > button[aria-label=\"Go to next page\"]"
    const val PAGE_LINK_BUTTON = "button.page-link"
    const val TRIP_PAGE_LOAD_INDICATOR = "div.trip-timeline-section-header:nth-child(1) > span:nth-child(1)"

    // Trip Page
    const val TIMELINE_LIST = "div[role=\"list\"]"
    const val TIMELINE_ITEMS = "div[data-cy=trip-timeline-section-header], div[data-cy=trip-timeline-segment]"
    const val TIMELINE_HEADER_DATE = "span[data-cy=timeline-header-date]"
    const val FLIGHT_ICON = "svg[aria-label='flight']"
    const val TIMELINE_TITLE_LINK = "a[data-cy=timeline-title]"
    const val TRIP_YEAR_INDICATOR = "span.p-0:nth-child(1)"

    // Flight Detail Page
    const val FLIGHT_DETAIL_HEADER = "h1.d-flex"
    const val FLIGHT_ROUTE_HEADING = "h1.text-32"
    const val TRIP_ITEM_SUBTITLE = "span[data-cy=trip-item-subtitle]"
    const val TIMELINE_CELL_CONTENT = "div[data-cy=timeline-cell-content]"
    const val TIMELINE_CELL_TIME = "span[data-cy=timeline-cell-time]"
    const val TIMELINE_CELL_TIMEZONE = "span[data-cy=timeline-cell-timezone]"

    // Base Page (Cookies)
    const val COOKIE_FRAME = "truste_popframe"
    const val COOKIE_REJECT_ALL = "Reject All"
}
