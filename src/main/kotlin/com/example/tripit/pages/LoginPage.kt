package com.example.tripit.pages

import com.codeborne.selenide.Condition.visible
import com.codeborne.selenide.Selenide
import com.codeborne.selenide.Selenide.`$`
import com.example.tripit.Selectors
import kotlin.random.Random.Default.nextLong

class LoginPage : BasePage() {
    fun open(): LoginPage {
        Selenide.open(Selectors.LOGIN_URL)
        declineCookiesIfPresent()
        return this
    }

    fun login(username: String, password: String, politeDelayMs: Long): PastTripsPage {
        `$`(Selectors.USERNAME_INPUT).value = username
        `$`(Selectors.PASSWORD_INPUT).value = password
        `$`(Selectors.SIGNIN_BUTTON).click()
        `$`("li.nav-item a.nav-link").shouldBe(visible)
        // Redirect to Past trips page for our workflow
        return PastTripsPage().open(nextLong(politeDelayMs-200,politeDelayMs+200))
    }
}
