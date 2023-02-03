package com.jamesdpeters.chestsplusplus.storage.yaml

import com.jamesdpeters.chestsplusplus.ChestsPlusPlus
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.springframework.context.annotation.DependsOn
import java.io.File

@DependsOn("serializeFieldCache")
abstract class YamlFileStorage<T : ConfigurationSerializable> {

    /** Overridden functions */

    /**
     * Directory relative to plugin folder
     */
    abstract val directory: String

    /**
     * Returns a filename for the object
     */
    abstract fun filename(obj: T): String

    /** Private functions */

    protected fun storageDirectory() = File(ChestsPlusPlus.plugin().dataFolder, directory)

    fun storageDirectory(filename: String): File {
        val file = File(storageDirectory(), filename)
        file.parentFile.mkdirs()
        if (!file.exists() && !file.isDirectory)
            file.createNewFile()
        return file
    }

    /** Protected functions */

    protected fun save(obj: T) {
        val yamlConfiguration = YamlConfiguration()
        yamlConfiguration["root"] = obj
        yamlConfiguration.save(storageDirectory(filename(obj)))
    }

    protected inline fun <reified T> load(filename: String): T? {
        val yaml = YamlConfiguration.loadConfiguration(storageDirectory(filename))
        val obj = yaml.get("root")
        if (obj != null && obj is T?)
            return obj
        return null
    }

    protected inline fun <reified T> loadAllInDirectory(): List<T> {
        val files = storageDirectory().list()
        return files?.mapNotNull { load<T>(it) } ?: listOf<T & Any>()
    }

}