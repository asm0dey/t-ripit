package com.example.tripit.pages

import com.codeborne.selenide.Condition.visible
import com.codeborne.selenide.Selectors.byClassName
import com.codeborne.selenide.Selectors.byText
import com.codeborne.selenide.Selenide.`$`
import com.codeborne.selenide.Selenide.switchTo

open class BasePage {
    protected fun sleep(ms: Long = 600L) {
        Thread.sleep(ms)
    }

    protected fun toAbsoluteUrl(href: String): String {
        return if (href.startsWith("http")) href else "https://www.tripit.com$href"
    }

    protected fun declineCookiesIfPresent() {
        try {
            switchTo().frame(`$`(byClassName("truste_popframe")))
            `$`(byText("Reject All")).shouldBe(visible).click()
        } catch (_: Throwable) {
            // optional banner – ignore if absent
        }
        switchTo().defaultContent()
    }
}
