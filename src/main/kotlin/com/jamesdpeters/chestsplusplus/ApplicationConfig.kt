package com.jamesdpeters.chestsplusplus

import com.jamesdpeters.chestsplusplus.services.config.Config
import com.jamesdpeters.chestsplusplus.services.config.ConfigOptions
import fr.minuskube.inv.InventoryManager
import me.gleeming.command.paramter.Processor
import org.bukkit.NamespacedKey
import org.bukkit.Server
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import java.util.logging.Logger
import javax.annotation.PostConstruct

@Configuration
@Import(ConfigOptions::class)
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

    @Bean
    open fun itemFrameKey(plugin: ChestsPlusPlus): NamespacedKey = NamespacedKey(plugin, "ItemFrameKey")

    @Bean
    open fun config(plugin: ChestsPlusPlus,  configValues: List<Config.AbstractValue<out Any>>) : Config {
        return Config(plugin,  configValues)
    }

    @Bean
    open fun inventoryManager(chestsPlusPlus: ChestsPlusPlus): InventoryManager = InventoryManager(chestsPlusPlus)
        .also { it.init() }

    @Autowired
    var processors: List<Processor<out Any>>? = null

    @PostConstruct
    fun init() {
        Log.debug { "Loaded Command Processors: " }
        processors?.forEach {
            Log.debug { it.type.toGenericString() }
        }
    }

}