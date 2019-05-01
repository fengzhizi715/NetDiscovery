package cn.netdiscovery.kotlin.dsl

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.WebDriverWait

/**
 * Created by tony on 2018/9/15.
 */
fun WebDriver.element(by: By, init: WebElement.() -> Unit) = findElement(by).init()

fun WebDriver.elementById(id: String, init: WebElement.() -> Unit) = findElement(By.id(id)).init()

fun <T> WebDriver.elementByClass(className: String, init: WebElement.() -> T): T = findElement(By.className(className)).init()

fun WebDriver.elementByName(name: String, init: WebElement.() -> Unit) = findElement(By.name(name)).init()

fun <T> WebDriver.elementByTag(tag: String, init: WebElement.() -> T): T = findElement(By.tagName(tag)).init()

fun WebDriver.elementBySelector(selector: String, init: WebElement.() -> Unit) = findElement(By.cssSelector(selector)).init()

fun WebDriver.elementByXpath(xpath: String, init: WebElement.() -> Unit) = findElement(By.xpath(xpath)).init()

fun WebDriver.wait(timeout: Long, sleepTimeout: Long = 500, init: WebDriverWait.() -> Unit) {
    WebDriverWait(this, timeout, sleepTimeout).init()
}