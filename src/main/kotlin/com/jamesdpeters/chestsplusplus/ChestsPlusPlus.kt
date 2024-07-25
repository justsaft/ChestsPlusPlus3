package com.jamesdpeters.chestsplusplus

import com.jamesdpeters.chestsplusplus.services.data.PersistableService
import com.jamesdpeters.chestsplusplus.util.CompoundClassLoader
import me.gleeming.command.CommandHandler
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.event.HandlerList
import org.bukkit.plugin.java.JavaPlugin
import org.springframework.context.annotation.AnnotationConfigApplicationContext

open class ChestsPlusPlus : JavaPlugin() {

    companion object {
        var app: AnnotationConfigApplicationContext? = null
        var plugin: ChestsPlusPlus? = null

        fun <T> getBean(beanClass: Class<T>): T? = app?.getBean(beanClass)


        fun scheduleTask(delay: Long, task: () -> Unit) {
            plugin?.let {
                Bukkit.getScheduler().scheduleSyncDelayedTask(it, task, delay)
            }
        }

        var itemTags: Iterable<Tag<Material>>? = null
        var blockTags: Iterable<Tag<Material>>? = null

    }

    override fun onEnable() {
        // Plugin startup logic
        val compoundClassLoader = CompoundClassLoader(classLoader, Thread.currentThread().contextClassLoader)

        CommandHandler.setPlugin(this)
        plugin = this

        app = AnnotationConfigApplicationContext()
        app?.classLoader = compoundClassLoader
        app?.register(ApplicationConfig::class.java)
        app?.refresh()

        Log.debug { "Debugging enabled!" }

        app?.getBeansOfType(PersistableService::class.java)?.forEach { (s, persistableService) ->
            Log.debug { "Loading service: $s" }
            persistableService.reload()
        }

        itemTags = server.getTags(Tag.REGISTRY_ITEMS, Material::class.java)
        blockTags = server.getTags(Tag.REGISTRY_BLOCKS, Material::class.java)

        Log.info { "Enabled ChestsPlusPlus" }
    }

    override fun onLoad() {
        super.onLoad()
    }

    override fun onDisable() {
        // Plugin shutdown logic
        if (app?.isActive == true) {
            app?.getBeansOfType(PersistableService::class.java)?.forEach { (s, persistableService) ->
                Log.debug { "Persisting service: $s" }
                persistableService.persist()
            }
        }

        HandlerList.unregisterAll(this)
        app = null
    }



}