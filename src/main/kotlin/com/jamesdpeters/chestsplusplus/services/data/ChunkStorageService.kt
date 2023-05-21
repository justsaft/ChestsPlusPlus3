package com.jamesdpeters.chestsplusplus.services.data

import com.googlecode.cqengine.ConcurrentIndexedCollection
import com.googlecode.cqengine.index.hash.HashIndex
import com.googlecode.cqengine.index.unique.UniqueIndex
import com.googlecode.cqengine.query.QueryFactory.equal
import com.jamesdpeters.chestsplusplus.ChestsPlusPlus
import com.jamesdpeters.chestsplusplus.spigot.event.ChestLinkLocationLoadEvent
import com.jamesdpeters.chestsplusplus.spigot.event.HopperFilterLocationLoadEvent
import com.jamesdpeters.chestsplusplus.storage.indexes.ChestLinkLocationAttributes
import com.jamesdpeters.chestsplusplus.storage.indexes.HopperFilterLocationAttributes
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

    private var chestLinks = ConcurrentIndexedCollection<ChestLinkLocation>()
    private var hoppers = ConcurrentIndexedCollection<HopperFilterLocation>()

    private val chestLinkKey = NamespacedKey(ChestsPlusPlus.plugin(), "ChunkStorage")
    private val chestLinkLocationType = ConfigurationSerializableDataType(ChestLinkLocation::class.java)
    private val chestLinkLocationCollectionType = DataType.asGenericCollection({ mutableListOf() }, chestLinkLocationType)

    private val hopperFilterKey = NamespacedKey(ChestsPlusPlus.plugin(), "HopperStorage")
    private val hopperFilterLocationType = ConfigurationSerializableDataType(HopperFilterLocation::class.java)
    private val hopperFilterLocationCollectionType = DataType.asGenericCollection({ mutableListOf()}, hopperFilterLocationType)

    init {
        chestLinks.addIndex(UniqueIndex.onAttribute(ChestLinkLocationAttributes.location))
        chestLinks.addIndex(HashIndex.onAttribute(ChestLinkLocationAttributes.chunk))
        chestLinks.addIndex(HashIndex.onAttribute(ChestLinkLocationAttributes.invUUID))

        hoppers.addIndex(UniqueIndex.onAttribute(HopperFilterLocationAttributes.location))
        hoppers.addIndex(HashIndex.onAttribute(HopperFilterLocationAttributes.chunk))
    }

    override fun persist() {
        Bukkit.getWorlds().forEach { world ->
            world.loadedChunks.forEach { persistChunk(it) }
        }
    }

    override fun reload() {
        chestLinks.clear()
        Bukkit.getWorlds().forEach { world ->
            world.loadedChunks.forEach { loadChunk(it) }
        }
    }

    fun persistChunk(chunk: Chunk) {
        chunk.persistentDataContainer[chestLinkKey, chestLinkLocationCollectionType] = getChestLinkLocations(chunk).toMutableList()
        chunk.persistentDataContainer[hopperFilterKey, hopperFilterLocationCollectionType] = getHopperFilterLocations(chunk).toMutableList()
    }

    fun loadChunk(chunk: Chunk) {
        chunk.persistentDataContainer.get(chestLinkKey, chestLinkLocationCollectionType)?.let {
            chestLinks.addAll(it)
        }
        chunk.persistentDataContainer.get(hopperFilterKey, hopperFilterLocationCollectionType)?.let {
            hoppers.addAll(it)
        }
    }

    fun unloadChunk(chunk: Chunk) {
        getChestLinkLocations(chunk).forEach {
            chestLinks.remove(it)
        }
        getHopperFilterLocations(chunk).forEach {
            hoppers.remove(it)
        }
    }

    /** [ChestLinkLocation] Functions */

    fun getChestLinkLocations(chunk: Chunk): List<ChestLinkLocation> {
        return chestLinks.retrieve(equal(ChestLinkLocationAttributes.chunk, chunk)).let {
            if (it.isNotEmpty) it.toList() else emptyList()
        }
    }

    fun getChestLinkLocations(inventoryUUID: UUID): List<ChestLinkLocation> {
        return chestLinks.retrieve(equal(ChestLinkLocationAttributes.invUUID, inventoryUUID)).let {
            if (it.isNotEmpty) it.toList() else emptyList()
        }
    }

    fun getChestLinkLocations(inventoryStore: InventoryStore): List<ChestLinkLocation> {
        return getChestLinkLocations(inventoryStore.uuid)
    }

    fun getChestLinkLocation(location: Location): ChestLinkLocation? {
        return chestLinks.retrieve(equal(ChestLinkLocationAttributes.location, location)).let {
            if (it.isNotEmpty) it.uniqueResult() else null
        }
    }

    fun createChestLinkLocation(location: Location, inventoryUUID: UUID? = null): ChestLinkLocation {
        val chestLinkLocation = ChestLinkLocation(location, inventoryUUID)
        chestLinks.add(chestLinkLocation)
        val event = ChestLinkLocationLoadEvent(chestLinkLocation)
        Bukkit.getServer().pluginManager.callEvent(event)
        return chestLinkLocation
    }

    fun removeChestLinkLocation(location: Location): ChestLinkLocation? {
        return getChestLinkLocation(location)?.also {
            chestLinks.remove(it)
        }
    }

    /** [HopperFilterLocation] Functions */

    fun getHopperFilterLocations(chunk: Chunk): List<HopperFilterLocation> {
        return hoppers.retrieve(equal(HopperFilterLocationAttributes.chunk, chunk)).let {
            if (it.isNotEmpty) it.toList() else emptyList()
        }
    }

    fun getHopperFilterLocation(location: Location): HopperFilterLocation {
        return hoppers.retrieve(equal(HopperFilterLocationAttributes.location, location)).let {
            if (it.isNotEmpty) it.uniqueResult() else createHopperFilterLocation(location)
        }
    }

    fun createHopperFilterLocation(location: Location): HopperFilterLocation {
        val hopperFilterLocation = HopperFilterLocation(location)
        hoppers.add(hopperFilterLocation)
        val event = HopperFilterLocationLoadEvent(hopperFilterLocation)
        Bukkit.getServer().pluginManager.callEvent(event)
        return hopperFilterLocation
    }


}