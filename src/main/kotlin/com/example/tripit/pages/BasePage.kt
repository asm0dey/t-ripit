package com.example.tripit.pages

import com.codeborne.selenide.Condition.visible
import com.codeborne.selenide.Selectors.byClassName
import com.codeborne.selenide.Selectors.byText
import com.codeborne.selenide.Selenide.element
import com.codeborne.selenide.Selenide.switchTo
import com.example.tripit.Selectors

open class BasePage {
    protected fun sleep(ms: Long = 600L) {
        Thread.sleep(ms)
    }

    protected fun toAbsoluteUrl(href: String): String {
        return if (href.startsWith("http")) href else "https://www.tripit.com$href"
    }

    protected fun declineCookiesIfPresent() {
        try {
            switchTo().frame(element(byClassName(Selectors.COOKIE_FRAME)))
            element(byText(Selectors.COOKIE_REJECT_ALL)).shouldBe(visible).click()
        } catch (_: Throwable) {
            // optional banner – ignore if absent
        }
        switchTo().defaultContent()
    }
}
