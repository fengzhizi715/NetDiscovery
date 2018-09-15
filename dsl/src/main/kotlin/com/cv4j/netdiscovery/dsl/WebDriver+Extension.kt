package com.cv4j.netdiscovery.dsl

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

/**
 * Created by tony on 2018/9/15.
 */
inline fun WebDriver.element(by: By, init: WebElement.() -> Unit) = findElement(by).init()

inline fun WebDriver.elementById(id: String, init: WebElement.() -> Unit) = findElement(By.id(id)).init()

inline fun <T> WebDriver.elementByClass(className: String, init: WebElement.() -> T): T = findElement(By.className(className)).init()

inline fun WebDriver.elementByName(name: String, init: WebElement.() -> Unit) = findElement(By.name(name)).init()

inline fun <T> WebDriver.elementByTag(tag: String, init: WebElement.() -> T): T = findElement(By.tagName(tag)).init()

inline fun WebDriver.elementBySelector(selector: String, init: WebElement.() -> Unit) = findElement(By.cssSelector(selector)).init()

inline fun WebDriver.elementByXpath(xpath: String, init: WebElement.() -> Unit) = findElement(By.xpath(xpath)).init()

