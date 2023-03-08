package com.jamesdpeters.chestsplusplus.services.data

import com.googlecode.cqengine.ConcurrentIndexedCollection
import com.googlecode.cqengine.index.hash.HashIndex
import com.googlecode.cqengine.index.unique.UniqueIndex
import com.googlecode.cqengine.query.QueryFactory.equal
import com.jamesdpeters.chestsplusplus.ChestsPlusPlus
import com.jamesdpeters.chestsplusplus.spigot.event.LocationInfoLoadEvent
import com.jamesdpeters.chestsplusplus.storage.indexes.LocationInfoAttributes
import com.jamesdpeters.chestsplusplus.storage.serializable.InventoryStore
import com.jamesdpeters.chestsplusplus.storage.serializable.LocationInfo
import com.jeff_media.morepersistentdatatypes.DataType
import com.jeff_media.morepersistentdatatypes.datatypes.serializable.ConfigurationSerializableDataType
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.springframework.stereotype.Service
import java.util.*

@Service
class LocationStorageService : PersistableService {

    private var locations = ConcurrentIndexedCollection<LocationInfo>()

    private val key = NamespacedKey(ChestsPlusPlus.plugin(), "ChunkStorage")
    private val locationInfoType = ConfigurationSerializableDataType(LocationInfo::class.java)
    private val locationInfoCollectionType = DataType.asGenericCollection({ mutableListOf() }, locationInfoType)

    init {
        locations.addIndex(UniqueIndex.onAttribute(LocationInfoAttributes.location))
        locations.addIndex(HashIndex.onAttribute(LocationInfoAttributes.chunk))
        locations.addIndex(HashIndex.onAttribute(LocationInfoAttributes.invUUID))
    }

    override fun persist() {
        Bukkit.getWorlds().forEach { world ->
            world.loadedChunks.forEach { persistChunk(it) }
        }
    }

    override fun reload() {
        locations.clear()
        Bukkit.getWorlds().forEach { world ->
            world.loadedChunks.forEach { loadChunk(it) }
        }
    }

    fun persistChunk(chunk: Chunk) {
        val locations = getLocations(chunk)
        chunk.persistentDataContainer[key, locationInfoCollectionType] = locations.toMutableList()
    }

    fun loadChunk(chunk: Chunk) {
        chunk.persistentDataContainer.get(key, locationInfoCollectionType)?.let {
            locations.addAll(it)
        }
    }

    /** [LocationInfo] Functions */

    fun getLocations(chunk: Chunk): List<LocationInfo> {
        return locations.retrieve(equal(LocationInfoAttributes.chunk, chunk)).let {
            if (it.isNotEmpty) it.toList() else emptyList()
        }
    }

    fun getLocations(inventoryUUID: UUID): List<LocationInfo> {
        return locations.retrieve(equal(LocationInfoAttributes.invUUID, inventoryUUID)).let {
            if (it.isNotEmpty) it.toList() else emptyList()
        }
    }

    fun getLocations(inventoryStore: InventoryStore): List<LocationInfo> {
        return getLocations(inventoryStore.uuid)
    }

    fun getLocation(location: Location): LocationInfo? {
        return locations.retrieve(equal(LocationInfoAttributes.location, location)).let {
            if (it.isNotEmpty) it.uniqueResult() else null
        }
    }

    fun createLocation(location: Location, inventoryUUID: UUID? = null): LocationInfo {
        val locationInfo = LocationInfo(location, inventoryUUID)
        locations.add(locationInfo)
        val event = LocationInfoLoadEvent(locationInfo)
        Bukkit.getServer().pluginManager.callEvent(event)
        return locationInfo
    }

    fun removeLocation(location: Location): LocationInfo? {
        return getLocation(location)?.also {
            locations.remove(it)
        }
    }

    fun unloadChunk(chunk: Chunk) {
        getLocations(chunk).forEach {
            locations.remove(it)
        }
    }


}