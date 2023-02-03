package com.jamesdpeters.chestsplusplus.storage.pdc

import com.googlecode.cqengine.ConcurrentIndexedCollection
import com.googlecode.cqengine.index.hash.HashIndex
import com.googlecode.cqengine.index.unique.UniqueIndex
import com.googlecode.cqengine.query.QueryFactory.equal
import com.jamesdpeters.chestsplusplus.ChestsPlusPlus
import com.jamesdpeters.chestsplusplus.spigot.event.LocationInfoLoadEvent
import com.jamesdpeters.chestsplusplus.storage.indexes.LocationInfoAttributes
import com.jamesdpeters.chestsplusplus.storage.serializable.LocationInfo
import com.jeff_media.morepersistentdatatypes.DataType
import com.jeff_media.morepersistentdatatypes.datatypes.serializable.ConfigurationSerializableDataType
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import java.util.UUID

class ChunkStorage(val chunk: Chunk) : AbstractPersistentDataContainerStorage() {

    companion object {
        val key = NamespacedKey(ChestsPlusPlus.plugin(), "ChunkStorage")
    }

    var locationInfoCollection: ConcurrentIndexedCollection<LocationInfo>? = null
        private set

    private val locationInfoType = ConfigurationSerializableDataType(LocationInfo::class.java)
    private val locationInfoCollectionType = DataType.asGenericCollection({ ConcurrentIndexedCollection() }, locationInfoType)

    override val key: NamespacedKey
        get() = Companion.key
    override val persistentDataContainer: PersistentDataContainer
        get() = chunk.persistentDataContainer

    init {
        load()
    }

    /** Override methods */

    override fun persist() {
        locationInfoCollection?.let {
            this[locationInfoCollectionType] = it
        }
    }

    override fun load() {
        locationInfoCollection = get(locationInfoCollectionType) ?: ConcurrentIndexedCollection()
        locationInfoCollection?.addIndex(UniqueIndex.onAttribute(LocationInfoAttributes.location))
        locationInfoCollection?.addIndex(HashIndex.onAttribute(LocationInfoAttributes.invUUID))
    }

    /** Private methods */
    private fun createLocationInfo(location: Location, inventoryUUID: UUID): LocationInfo {
        val locationInfo = LocationInfo(location, inventoryUUID)
        locationInfoCollection?.add(locationInfo)
        val event = LocationInfoLoadEvent(locationInfo)
        Bukkit.getServer().pluginManager.callEvent(event)
        return locationInfo
    }

    /** Public methods */

    fun getUUID(location: Location): UUID? {
        return locationInfoCollection?.retrieve(equal(LocationInfoAttributes.location, location)).let {
            if (it?.isNotEmpty == true) it.uniqueResult()?.inventoryUUID else null
        }
    }

    fun getLocationInfo(location: Location): LocationInfo? {
        return locationInfoCollection?.retrieve(equal(LocationInfoAttributes.location, location)).let {
            if (it?.isNotEmpty == true) it.uniqueResult()
            else null
        }
    }

    fun getLocations(inventoryUUID: UUID): List<LocationInfo> {
        return locationInfoCollection?.retrieve(equal(LocationInfoAttributes.invUUID, inventoryUUID)).use {
            if (it?.isNotEmpty == true) it.toList() else emptyList()
        }
    }

    fun removeLocation(location: Location) {
        getLocationInfo(location)?.let {
            locationInfoCollection?.remove(it)
        }
    }

    fun setInventoryUUID(location: Location, inventoryUUID: UUID) {
        getLocationInfo(location)?.let {
            it.inventoryUUID = inventoryUUID
        } ?: run {
            createLocationInfo(location, inventoryUUID)
        }
    }

}