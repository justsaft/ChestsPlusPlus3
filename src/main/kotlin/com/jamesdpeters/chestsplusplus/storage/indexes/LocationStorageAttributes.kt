package com.jamesdpeters.chestsplusplus.storage.indexes

import com.googlecode.cqengine.attribute.Attribute
import com.googlecode.cqengine.attribute.MultiValueAttribute
import com.googlecode.cqengine.attribute.SimpleAttribute
import com.googlecode.cqengine.query.option.QueryOptions
import com.jamesdpeters.chestsplusplus.storage.pdc.ChunkStorage
import com.jamesdpeters.chestsplusplus.storage.serializable.LocationInfo
import org.bukkit.Chunk
import org.bukkit.Location
import java.util.*

object LocationStorageAttributes {

    var chunk: Attribute<ChunkStorage, Chunk> = object : SimpleAttribute<ChunkStorage, Chunk>() {
        override fun getValue(o: ChunkStorage, queryOptions: QueryOptions): Chunk = o.chunk
    }

    val inventoryUUID: Attribute<ChunkStorage, UUID> = object : MultiValueAttribute<ChunkStorage, UUID>() {
        override fun getValues(o: ChunkStorage, queryOptions: QueryOptions): Iterable<UUID?> =
            o.locationInfoCollection?.stream()?.map(LocationInfo::inventoryUUID)?.toList() ?: ArrayList()
    }

    val location: Attribute<ChunkStorage, Location> = object : MultiValueAttribute<ChunkStorage, Location>() {
        override fun getValues(o: ChunkStorage, queryOptions: QueryOptions): Iterable<Location?> =
            o.locationInfoCollection?.stream()?.map(LocationInfo::location)?.toList() ?: ArrayList()
    }

}