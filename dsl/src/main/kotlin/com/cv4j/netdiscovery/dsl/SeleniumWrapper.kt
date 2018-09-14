package com.cv4j.netdiscovery.dsl

import com.cv4j.netdiscovery.selenium.Browser
import com.cv4j.netdiscovery.selenium.action.SeleniumAction
import com.cv4j.netdiscovery.selenium.downloader.SeleniumDownloader
import com.cv4j.netdiscovery.selenium.pool.WebDriverPool
import com.cv4j.netdiscovery.selenium.pool.WebDriverPoolConfig

/**
 * Created by tony on 2018/9/14.
 */
class SeleniumWrapper {

    var path: String? = null

    var browser: Browser? = null

    private val actions = mutableListOf<SeleniumAction>()

    fun addAction(block: ActionWrapper.() -> Unit) {

        val actionWrapper = ActionWrapper()
        actionWrapper.block()

        actionWrapper?.action?.let {
            actions.add(it)
        }
    }

    fun getActions() = actions
}

class ActionWrapper{

    var action:SeleniumAction?=null
}

fun seleniumDownloader(init: SeleniumWrapper.() -> Unit): SeleniumDownloader {

    val wrap = SeleniumWrapper()

    wrap.init()

    return configSeleniumDownloader(wrap)
}

private fun configSeleniumDownloader(wrap: SeleniumWrapper): SeleniumDownloader {

    val config = WebDriverPoolConfig(wrap.path, wrap.browser)
    WebDriverPool.init(config)

    return SeleniumDownloader(wrap.getActions())
}
