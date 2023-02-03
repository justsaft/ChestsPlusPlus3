package com.jamesdpeters.chestsplusplus.storage.pdc

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.util.function.Supplier

abstract class AbstractPersistentDataContainerStorage {

    abstract val key: NamespacedKey
    abstract val persistentDataContainer: PersistentDataContainer

    abstract fun persist()
    abstract fun load()

    protected open operator fun <T : Any> set(dataType: PersistentDataType<*, T>, value: T) =
        persistentDataContainer.set(key, dataType, value)

    protected open operator fun <T> get(dataType: PersistentDataType<*, T>): T? =
        persistentDataContainer.get(key, dataType)

    protected open fun <T> getOrCreate(dataType: PersistentDataType<*, T>, supplier: Supplier<T>): T {
        return get(dataType) ?: supplier.get()
    }
}