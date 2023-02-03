package com.jamesdpeters.chestsplusplus

import org.bukkit.NamespacedKey
import org.bukkit.Server
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import java.util.logging.Logger

@Configuration
@ComponentScan("com.jamesdpeters.chestsplusplus")
open class ApplicationConfig {

    @Bean
    open fun plugin(): ChestsPlusPlus = JavaPlugin.getPlugin(ChestsPlusPlus::class.java)

    @Bean
    open fun server(plugin: ChestsPlusPlus): Server = plugin.server

    @Bean
    open fun pluginManager(server: Server): PluginManager = server.pluginManager

    @Bean
    open fun logger(plugin: ChestsPlusPlus): Logger = plugin.logger

//    @Bean
//    fun messageSource(): MessageSource {
//        val messageSource = ResourceBundleMessageSource()
//        messageSource.setBasename("lang/res")
//        return messageSource
//    }

    @Bean
    open fun itemFrameKey(plugin: ChestsPlusPlus): NamespacedKey = NamespacedKey(plugin, "ItemFrameKey")

}