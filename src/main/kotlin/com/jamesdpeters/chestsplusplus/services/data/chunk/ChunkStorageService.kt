package com.jamesdpeters.chestsplusplus.services.data.chunk

import com.jamesdpeters.chestsplusplus.ChestsPlusPlus
import com.jamesdpeters.chestsplusplus.services.data.PersistableService
import com.jamesdpeters.chestsplusplus.spigot.event.ChestLinkLocationLoadEvent
import com.jamesdpeters.chestsplusplus.spigot.event.HopperFilterLocationLoadEvent
import com.jamesdpeters.chestsplusplus.storage.serializable.ChestLinkLocation
import com.jamesdpeters.chestsplusplus.storage.serializable.HopperFilterLocation
import com.jamesdpeters.chestsplusplus.storage.serializable.InventoryStore
import com.jeff_media.morepersistentdatatypes.DataType
import com.jeff_media.morepersistentdatatypes.datatypes.serializable.ConfigurationSerializableDataType
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.springframework.stereotype.Service
import java.util.*

@Service
class ChunkStorageService : PersistableService {

    private val chestLinkStorage = ChestLinkStorageMap()
    private val hopperStorage = HopperStorageMap()

    private val chestLinkKey = NamespacedKey(ChestsPlusPlus.plugin!!, "ChunkStorage")
    private val chestLinkLocationType = ConfigurationSerializableDataType(ChestLinkLocation::class.java)
    private val chestLinkLocationCollectionType = DataType.asGenericCollection({ mutableListOf() }, chestLinkLocationType)

    private val hopperFilterKey = NamespacedKey(ChestsPlusPlus.plugin!!, "HopperStorage")
    private val hopperFilterLocationType = ConfigurationSerializableDataType(HopperFilterLocation::class.java)
    private val hopperFilterLocationCollectionType = DataType.asGenericCollection({ mutableListOf()}, hopperFilterLocationType)

    override fun persist() {
        Bukkit.getWorlds().forEach { world ->
            world.loadedChunks.forEach { persistChunk(it) }
        }
    }

    override fun reload() {
        chestLinkStorage.clear()
        Bukkit.getWorlds().forEach { world ->
            world.loadedChunks.forEach { loadChunk(it) }
        }
    }

    override fun clear() {
        chestLinkStorage.clear()
    }

    fun persistChunk(chunk: Chunk) {
        getChestLinkLocations(chunk)?.toMutableList()?.let {
            chunk.persistentDataContainer[chestLinkKey, chestLinkLocationCollectionType] = it
        }
        getHopperFilterLocations(chunk)?.toMutableList()?.let {
            chunk.persistentDataContainer[hopperFilterKey, hopperFilterLocationCollectionType] = it
        }
    }

    fun loadChunk(chunk: Chunk) {
        chunk.persistentDataContainer.get(chestLinkKey, chestLinkLocationCollectionType)?.let {
            if (it.isNotEmpty()) chestLinkStorage[chunk] = it
        }
        chunk.persistentDataContainer.get(hopperFilterKey, hopperFilterLocationCollectionType)?.let {
            if (it.isNotEmpty()) hopperStorage[chunk] = it
        }
    }

    fun unloadChunk(chunk: Chunk) {
        chestLinkStorage.remove(chunk)
        hopperStorage.remove(chunk)
    }

    /** [ChestLinkLocation] Functions */

    fun getChestLinkLocations(chunk: Chunk): List<ChestLinkLocation>? {
        return chestLinkStorage[chunk]
    }

    fun getChestLinkLocations(inventoryUUID: UUID): List<ChestLinkLocation>? {
        return chestLinkStorage.get(inventoryUUID)
    }

    fun getChestLinkLocations(inventoryStore: InventoryStore): List<ChestLinkLocation>? {
        return getChestLinkLocations(inventoryStore.uuid)
    }

    fun getChestLinkLocation(location: Location): ChestLinkLocation? {
        return chestLinkStorage.get(location)
    }

    fun createChestLinkLocation(location: Location, inventoryUUID: UUID? = null): Boolean {
        getChestLinkLocation(location).let {
            if (it != null)
                return false
        }

        val chestLinkLocation = ChestLinkLocation(location, inventoryUUID)
        chestLinkStorage.add(chestLinkLocation)
        val event = ChestLinkLocationLoadEvent(chestLinkLocation)
        Bukkit.getServer().pluginManager.callEvent(event)
        return true
    }

    fun removeChestLinkLocation(location: Location): ChestLinkLocation? {
        return getChestLinkLocation(location)?.also {
            chestLinkStorage.remove(it)
        }
    }

    /** [HopperFilterLocation] Functions */

    fun getHopperFilterLocations(chunk: Chunk): List<HopperFilterLocation>? {
        return hopperStorage[chunk]
    }

    fun getHopperFilterLocation(location: Location): HopperFilterLocation {
        return hopperStorage.get(location)
            ?: createHopperFilterLocation(location)
    }

    private fun createHopperFilterLocation(location: Location): HopperFilterLocation {
        val hopperFilterLocation = HopperFilterLocation(location)
        hopperStorage.add(hopperFilterLocation)
        val event = HopperFilterLocationLoadEvent(hopperFilterLocation)
        Bukkit.getServer().pluginManager.callEvent(event)
        return hopperFilterLocation
    }


}