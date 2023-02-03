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

        fun logger() = plugin().logger
    }

    override fun onEnable() {
        // Plugin startup logic
        val compoundClassLoader = CompoundClassLoader(classLoader, Thread.currentThread().contextClassLoader)

        app = AnnotationConfigApplicationContext()
        app?.classLoader = compoundClassLoader
        app?.register(ApplicationConfig::class.java)
        app?.refresh()

        app?.getBeansOfType(PersistableService::class.java)?.forEach { (s, persistableService) ->
            logger.info("Loading service: $s")
            persistableService.reload()
        }
    }

    override fun onDisable() {
        // Plugin shutdown logic

        app?.getBeansOfType(PersistableService::class.java)?.forEach { (s, persistableService) ->
            logger.info("Persisting service: $s")
            persistableService.persist()
        }

        HandlerList.unregisterAll(this)
        app = null
    }



}