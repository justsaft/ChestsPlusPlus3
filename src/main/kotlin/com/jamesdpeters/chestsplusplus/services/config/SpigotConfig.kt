package com.jamesdpeters.chestsplusplus.services.config

import com.jamesdpeters.chestsplusplus.ChestsPlusPlus
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.configuration.ConfigurationSection
import org.springframework.stereotype.Component

@Component
class SpigotConfig(
    plugin: ChestsPlusPlus,
    serverType: ServerType
) {

    val worlds: HashMap<String, WorldSettings> = hashMapOf()
    var default: WorldSettings = WorldSettings()
        private set

    init {
        if (serverType.hasSpigotConfig) {
            val worldSettings = plugin.server.spigot().config.getConfigurationSection("world-settings")
            if (worldSettings != null) {
                worldSettings.getValues(false).forEach { (worldName: String, _: Any?) ->
                    val worldSetting = worldSettings.getConfigurationSection(worldName)
                    if (worldName != "default" && worldSetting != null)
                        worlds[worldName] = WorldSettings(worldSetting)
                }
                val section = worldSettings.getConfigurationSection("default")
                if (section != null) default = WorldSettings(section)
            }
        }
    }

    fun getWorldSettings(world: World?): WorldSettings {
        return world?.let { worlds.getOrDefault(it.name, default) } ?: default
    }

    fun getWorldSettings(location: Location?): WorldSettings {
        return location?.let { getWorldSettings(it.world) } ?: default
    }


    class WorldSettings {
        //Values
        val ticksPerHopperTransfer: Int
        val hopperAmount: Int

        constructor(settings: ConfigurationSection) {
            ticksPerHopperTransfer = settings.getInt("ticks-per.hopper-transfer")
            hopperAmount = settings.getInt("hopper-amount")
        }

        //Default class used as a fallback if Spigot isn't being used etc.
        constructor() {
            ticksPerHopperTransfer = 8
            hopperAmount = 1
        }
    }

}