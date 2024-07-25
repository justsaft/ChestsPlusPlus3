package com.jamesdpeters.chestsplusplus.storage.yaml

import com.jamesdpeters.chestsplusplus.ChestsPlusPlus
import org.bukkit.configuration.file.YamlConfiguration
import org.springframework.context.annotation.DependsOn
import java.io.File

@DependsOn("serializeFieldCache")
abstract class YamlFileStorage<T> {

    /** Overridden functions */

    /**
     * Directory relative to plugin folder
     */
    abstract val directory: String

    /**
     * Returns a filename for the object
     */
    abstract val filename: String

    /** Private functions */

    private fun storageDirectory() = File(ChestsPlusPlus.plugin!!.dataFolder, directory)

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
        yamlConfiguration.save(storageDirectory(filename))
    }

    protected inline fun <reified T> load(): T? {
        val yaml = YamlConfiguration.loadConfiguration(storageDirectory(filename))
        val obj = yaml.get("root")
        if (obj != null && obj is T?)
            return obj
        return null
    }

}