package com.jamesdpeters.chestsplusplus.services.data.chunk

import com.jamesdpeters.chestsplusplus.storage.serializable.ChestLinkLocation
import org.bukkit.Chunk
import org.bukkit.Location
import java.util.*

class ChestLinkStorageMap : HashMap<Chunk, MutableList<ChestLinkLocation>>() {

    private val locations = HashMap<Location, ChestLinkLocation>()
    private val uuids = HashMap<UUID, MutableList<ChestLinkLocation>>()

    override fun get(key: Chunk): MutableList<ChestLinkLocation>? {
        return super.get(key)
    }

    fun get(uuid: UUID): MutableList<ChestLinkLocation>? {
        return uuids[uuid]
    }

    fun get(location: Location): ChestLinkLocation? {
        return locations[location]
    }

    override fun put(key: Chunk, value: MutableList<ChestLinkLocation>): MutableList<ChestLinkLocation>? {
        value.forEach { addToOtherMaps(it) }
        return super.put(key, value)
    }

    private fun addToOtherMaps(value: ChestLinkLocation): ChestLinkLocation {
        value.location?.let {
            locations[it] = value
        }
        value.inventoryUUID?.let {
            uuids.computeIfAbsent(it) { mutableListOf() }.add(value)
        }
        return value
    }


    private fun removeFromOtherMaps(value: ChestLinkLocation) {
        value.location?.let {
            locations.remove(it)
        }
        value.inventoryUUID?.let {
            uuids[it]?.remove(value)
        }
    }

    fun add(value: ChestLinkLocation) {
        value.location?.chunk?.let {
            computeIfAbsent(it) { mutableListOf() }
                .add(value)

            addToOtherMaps(value)
        }
    }

    fun remove(value: ChestLinkLocation) {
        value.location?.chunk?.let {
            get(it)?.remove(value)
        }
        removeFromOtherMaps(value)
    }

    override fun clear() {
        locations.clear()
        uuids.clear()
        super.clear()
    }

}