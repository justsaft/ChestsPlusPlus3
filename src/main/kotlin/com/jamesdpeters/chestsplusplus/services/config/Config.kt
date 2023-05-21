package com.jamesdpeters.chestsplusplus.services.config

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.Plugin
import kotlin.reflect.KClass
import kotlin.reflect.cast

open class Config(val plugin: Plugin, configValues: List<AbstractValue<out Any>>) {

    private val values: MutableMap<String?, MutableList<AbstractValue<out Any>>> = mutableMapOf()
    private var configuration: FileConfiguration = plugin.config

    init {
        configValues.forEach { it.init(this) }

        configuration.options().setHeader(listOf(
            "***********************************************",
            "*********** ChestsPlusPlus Config *************",
            "***********************************************",
            ))
        configuration.options().parseComments(true)

        values.forEach { (configSection, values) ->
            if (configSection != null) {
                val section = configuration.getConfigurationSection(configSection)
                    ?: configuration.createSection(configSection)
                values.forEach { it.set(section.get(it.path)) }
            } else {
                values.forEach { it.set(configuration.get(it.path)) }
            }
        }

        setCommentsAndDefaults()
        plugin.saveConfig()
    }

    private fun setCommentsAndDefaults() {
        values.forEach { (configSection, values) ->
            if (configSection != null) {
                val section = configuration.getConfigurationSection(configSection)
                    ?: configuration.createSection(configSection)
                values.forEach {
                    if (section.getConfigurationSection(it.path) == null) {
                        section.set(it.path, it.invoke())
                    }
                    section.setComments(it.path, it.comments)
                }
            } else {
                values.forEach {
                    if (configuration.getConfigurationSection(it.path) == null) {
                        configuration.set(it.path, it.invoke())
                    }
                    configuration.setComments(it.path, it.comments)
                }
            }
        }
        configuration.options().copyDefaults(true)
    }


    abstract class AbstractValue<T : Any>(private val configurationSection: String?, val path: String, val defaultValue: T, val comments: List<String> = listOf()) {
        protected var value: T? = defaultValue

        fun init(config: Config) {
            val list = config.values.computeIfAbsent(configurationSection) { mutableListOf() }
            if (list.any { it.path == path }) {
                throw IllegalArgumentException("Two values with the same path have been added to the Config '$path'")
            }
            list.add(this)
        }

        operator fun invoke() : T = value ?: defaultValue
        operator fun invoke(value: T) {
            this.value = value
        }

        abstract fun set(value: Any?)
    }

    class Value<T : Any>(configurationSection: String?, path: String, val type: KClass<T>, defaultValue: T, comments: List<String> = listOf())
        : AbstractValue<T>(configurationSection, path, defaultValue, comments)
    {
        constructor(path: String, type: KClass<T>, defaultValue: T, comments: List<String> = listOf()) : this(null, path, type, defaultValue, comments)

        override fun set(value: Any?) {
            this.value =
                if (type.isInstance(value)) {
                    type.cast(value)
                } else {
                    defaultValue
                }
        }
    }

    class ListValue<T : Any>(configurationSection: String?, path: String, val type: KClass<T>, defaultValue: MutableList<T?>, comments: List<String> = listOf())
        : AbstractValue<MutableList<T?>>(configurationSection, path, defaultValue, comments)
    {
        constructor(path: String, type: KClass<T>, defaultValue: MutableList<T?>, comments: List<String> = listOf()) : this(null, path, type, defaultValue, comments)

        override fun set(value: Any?) {
            val list = if (value is List<*>) value else defaultValue
            this.value = mutableListOf()

            list.forEach {
                if (type.isInstance(it))
                    this.value!!.add(type.cast(it))
            }
        }

    }

}