package com.jamesdpeters.chestsplusplus.services.data.chunk

import com.jamesdpeters.chestsplusplus.storage.serializable.HopperFilterLocation
import org.bukkit.Chunk
import org.bukkit.Location

class HopperStorageMap : HashMap<Chunk, MutableList<HopperFilterLocation>>() {

    private val locations = HashMap<Location, HopperFilterLocation>()

    fun get(location: Location) : HopperFilterLocation? {
        return locations[location]
    }

    private fun addToOtherMaps(value: HopperFilterLocation) {
        value.location?.let {
            locations[it] = value
        }
    }

    private fun removeFromOtherMaps(value: HopperFilterLocation) {
        value.location?.let {
            locations.remove(it)
        }
    }

    override fun put(key: Chunk, value: MutableList<HopperFilterLocation>): MutableList<HopperFilterLocation>? {
        value.forEach { addToOtherMaps(it) }
        return super.put(key, value)
    }

    fun add(value: HopperFilterLocation) {
        value.location?.chunk?.let {
            computeIfAbsent(it) { mutableListOf() }
                .add(value)

            addToOtherMaps(value)
        }
    }

    fun remove(value: HopperFilterLocation) {
        value.location?.chunk?.let {
            get(it)?.remove(value)
        }
        removeFromOtherMaps(value)
    }
}