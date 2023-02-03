package com.jamesdpeters.chestsplusplus.storage.serializable

import com.jamesdpeters.chestsplusplus.ChestsPlusPlus
import com.jamesdpeters.chestsplusplus.serialize.SerializeFieldCache
import org.bukkit.configuration.serialization.ConfigurationSerializable

sealed class SerializableObject : ConfigurationSerializable {

    constructor(map: MutableMap<String?, Any?>) {
        @Suppress("LeakingThis")
        serializer.deserialize(this, map)
    }

    constructor()

    private val serializer = ChestsPlusPlus.getBean(SerializeFieldCache::class.java)!!

    override fun serialize(): MutableMap<String?, Any?> {
        preSerialize()
        return serializer.serialize(this)
    }

    open fun preSerialize() = Unit
}