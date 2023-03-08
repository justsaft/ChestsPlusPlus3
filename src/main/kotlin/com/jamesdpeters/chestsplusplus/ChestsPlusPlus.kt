package com.jamesdpeters.chestsplusplus

import com.jamesdpeters.chestsplusplus.services.data.PersistableService
import com.jamesdpeters.chestsplusplus.util.CompoundClassLoader
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.springframework.context.annotation.AnnotationConfigApplicationContext

class ChestsPlusPlus : JavaPlugin() {

    companion object {
        var app: AnnotationConfigApplicationContext? = null

        fun <T> getBean(beanClass: Class<T>): T? = app?.getBean(beanClass)

        fun plugin() = getPlugin(ChestsPlusPlus::class.java)
    }

    override fun onEnable() {
        // Plugin startup logic
        val compoundClassLoader = CompoundClassLoader(classLoader, Thread.currentThread().contextClassLoader)

        app = AnnotationConfigApplicationContext()
        app?.classLoader = compoundClassLoader
        app?.register(ApplicationConfig::class.java)
        app?.refresh()

        Log.debug { "Debugging enabled!" }

        app?.getBeansOfType(PersistableService::class.java)?.forEach { (s, persistableService) ->
            Log.debug { "Loading service: $s" }
            persistableService.reload()
        }
        Log.info { "Enabled ChestsPlusPlus" }
    }

    override fun onDisable() {
        // Plugin shutdown logic

        app?.getBeansOfType(PersistableService::class.java)?.forEach { (s, persistableService) ->
            Log.debug { "Persisting service: $s" }
            persistableService.persist()
        }

        HandlerList.unregisterAll(this)
        app = null
    }



}