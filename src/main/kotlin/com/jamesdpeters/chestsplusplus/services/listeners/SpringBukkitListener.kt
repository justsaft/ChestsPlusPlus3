@file:Suppress("SpringJavaInjectionPointsAutowiringInspection")

package com.jamesdpeters.chestsplusplus.services.listeners

import com.jamesdpeters.chestsplusplus.ChestsPlusPlus
import org.bukkit.event.Listener
import org.bukkit.plugin.PluginManager
import org.springframework.beans.factory.annotation.Autowired
import javax.annotation.PostConstruct

abstract class SpringBukkitListener : Listener {

    @Autowired
    open var pluginManager: PluginManager? = null

    @PostConstruct
    fun init() {
        if (isEnabled) {
            pluginManager?.registerEvents(this, ChestsPlusPlus.plugin!!)
            onEnable()
        }
    }

    open val isEnabled: Boolean = true
    open fun onEnable() {}
}