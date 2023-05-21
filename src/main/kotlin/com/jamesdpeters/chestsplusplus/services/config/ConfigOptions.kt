@file:Suppress("LeakingThis")

package com.jamesdpeters.chestsplusplus.services.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ConfigOptions {

    companion object {
        lateinit var instance: ConfigOptions
            private set

        operator fun invoke(): ConfigOptions = instance

        operator fun <T : Any> get(option: (ConfigOptions) -> Config.AbstractValue<T>): T = option.invoke(instance).invoke()
    }

    init {
        instance = this
    }

    // * Chest Link Module
    @get:Bean
    open val isChestLinkEnabled = Config.Value("modules", "chest-links.enabled", Boolean::class, true, listOf("Enables to the use of ChestLinks"))

    @get:Bean
    open val useInvisibleItemFramesChestLink = Config.Value("modules", "chest-links.use-invisible-item-frames", Boolean::class, true, listOf("Toggles whether ItemFrames on the front of ChestLinks should be invisible when an item is present"))

    // * Hopper Filter Module
    @get:Bean
    open val isHopperFilterEnabled = Config.Value("modules", "hopper-filter.enabled", Boolean::class, true, listOf("Enables to the use of Hopper Filters"))


    // * Misc
    @get:Bean
    open val isDebug = Config.Value("debug", Boolean::class, false, listOf("Enables debug logging"))

    @get:Bean
    open val isUpdateCheckerEnabled = Config.Value("update-checker", Boolean::class, true, listOf("Enables the plugin update checker from spigotmc.org"))

}