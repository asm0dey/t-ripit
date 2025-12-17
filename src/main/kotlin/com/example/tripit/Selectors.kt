package com.example.tripit

object Selectors {
    // Login page
    const val LOGIN_URL = "https://www.tripit.com/account/signin"
    const val USERNAME_INPUT = "#email_address"
    const val PASSWORD_INPUT = "#password"
    const val SIGNIN_BUTTON = "#signin-submit-btn"

    // After login
    const val PAST_TRIPS_URL = "https://www.tripit.com/app/trips?trips_filter=past"


    // Pagination controls (from samples/past_trips.html, Bootstrap-like)
    const val PAGE_ITEM = ".pagination .page-item"
}
