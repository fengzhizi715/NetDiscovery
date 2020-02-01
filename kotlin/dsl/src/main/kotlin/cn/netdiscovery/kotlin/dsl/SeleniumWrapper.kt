package cn.netdiscovery.kotlin.dsl

import cn.netdiscovery.downloader.selenium.Browser
import cn.netdiscovery.downloader.selenium.action.SeleniumAction
import cn.netdiscovery.downloader.selenium.downloader.SeleniumDownloader
import cn.netdiscovery.downloader.selenium.pool.WebDriverPool
import cn.netdiscovery.downloader.selenium.pool.WebDriverPoolConfig

/**
 * Created by tony on 2018/9/14.
 */
class SeleniumWrapper {

    var path: String? = null

    var browser: Browser? = null

    private val actions = mutableListOf<SeleniumAction>()

    fun addAction(block: ActionWrapper.() -> Unit) {

        ActionWrapper().apply {
            block()
            action?.let {
                actions.add(it)
            }
        }
    }

    internal fun getActions() = actions
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
