package com.example.tripit.pages

import com.codeborne.selenide.Condition.visible
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.Selenide.element
import com.example.tripit.Selectors
import kotlin.random.Random.Default.nextLong

class LoginPage : BasePage() {
    fun open(): LoginPage {
        Selenide.open(Selectors.LOGIN_URL)
        declineCookiesIfPresent()
        return this
    }

    fun login(username: String, password: String, politeDelayMs: Long): PastTripsPage {
        element(Selectors.USERNAME_INPUT).value = username
        element(Selectors.PASSWORD_INPUT).value = password
        element(Selectors.SIGNIN_BUTTON).click()
        element(Selectors.NAV_LINK).shouldBe(visible)
        // Redirect to Past trips page for our workflow
        return PastTripsPage().open(nextLong(politeDelayMs-200,politeDelayMs+200))
    }
}
