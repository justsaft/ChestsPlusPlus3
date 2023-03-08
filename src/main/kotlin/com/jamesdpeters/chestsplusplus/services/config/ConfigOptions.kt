package com.jamesdpeters.chestsplusplus.services.config

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class ConfigOptions {

    companion object {
        var instance: ConfigOptions? = null
            private set
    }

    init {
        instance = this
    }

    @get:Bean
    val isDebug = Config.Value("debug", Boolean::class, false, listOf("Enables debug logging"))

    @get:Bean
    val isUpdateCheckerEnabled = Config.Value("update-checker", Boolean::class, true, listOf("Enables the plugin update checker from spigotmc.org"))

}