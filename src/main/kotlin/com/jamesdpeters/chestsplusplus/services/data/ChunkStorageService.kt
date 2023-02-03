package com.jamesdpeters.chestsplusplus.services.data

import com.googlecode.cqengine.ConcurrentIndexedCollection
import com.googlecode.cqengine.index.unique.UniqueIndex
import com.googlecode.cqengine.query.QueryFactory.equal
import com.jamesdpeters.chestsplusplus.ChestsPlusPlus
import com.jamesdpeters.chestsplusplus.storage.indexes.LocationStorageAttributes
import com.jamesdpeters.chestsplusplus.storage.pdc.ChunkStorage
import com.jamesdpeters.chestsplusplus.storage.serializable.LocationInfo
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors

@Service
class ChunkStorageService : PersistableService {

    private val chunkStorage = ConcurrentIndexedCollection<ChunkStorage>()
    private val log = ChestsPlusPlus.logger()

    init {
        chunkStorage.addIndex(UniqueIndex.onAttribute(LocationStorageAttributes.chunk))
    }

    /** Override functions */

    override fun persist() {
        chunkStorage.forEach {
            it.persist()
        }
    }

    override fun reload() {
        for (world in Bukkit.getWorlds()) {
            for (loadedChunk in world.loadedChunks) {
                loadChunk(loadedChunk)
            }
        }
    }

    /** Public functions */

    fun loadChunk(chunk: Chunk) {
        val loadedStorage = getChunkStorage(chunk)
        val size = loadedStorage.locationInfoCollection?.size ?: 0
        if (size > 0)
            log.info("Loaded chunk" + loadedStorage.chunk + " with locations: " + size)
    }

    fun unloadChunk(chunk: Chunk) {
        getNullableChunkStorage(chunk)?.let {
            chunkStorage.remove(it)
        }
    }

    fun getChunkStorage(chunk: Chunk): ChunkStorage {
        chunkStorage.retrieve(equal(LocationStorageAttributes.chunk, chunk)).use { query ->
            return if (query.isNotEmpty) {
                query.uniqueResult()
            } else {
                val locationStorage = ChunkStorage(chunk)
                chunkStorage.add(locationStorage)
                locationStorage
            }
        }
    }

    fun getNullableChunkStorage(chunk: Chunk): ChunkStorage? {
        chunkStorage.retrieve(equal(LocationStorageAttributes.chunk, chunk)).use { query ->
            return if (query.isNotEmpty) query.uniqueResult() else null
        }
    }

    fun getInventoryLocationInfos(inventoryUUID: UUID): Set<LocationInfo> {
        chunkStorage.retrieve(equal(LocationStorageAttributes.inventoryUUID, inventoryUUID)).use { query ->
            if (query.isNotEmpty) {
                val loc = query.map {
                    it.getLocations(inventoryUUID)
                }
                val flat = loc.flatten()
                return flat.toSet()
            }
            else {
                return emptySet()
            }
        }
    }

    fun getInventoryLocations(inventoryUUID: UUID): Set<Location> {
        return getInventoryLocationInfos(inventoryUUID)
            .mapNotNull { locationInfo -> locationInfo.location }
            .toSet()
    }

    fun getInventoryUUID(location: Location): UUID? = getChunkStorage(location.chunk).getUUID(location)
    fun getLocationInfo(location: Location): LocationInfo? = getChunkStorage(location.chunk).getLocationInfo(location)
    fun persistChunk(chunk: Chunk) = getNullableChunkStorage(chunk)?.persist()

    fun removeLocationInfo(location: Location) {
        getNullableChunkStorage(location.chunk)?.removeLocation(location)
    }

    fun setInventoryUUID(location: Location, inventoryUUID: UUID) {
        getChunkStorage(location.chunk).setInventoryUUID(location, inventoryUUID)
    }


}