package com.jamesdpeters.chestsplusplus.storage.indexes

import com.googlecode.cqengine.attribute.SimpleAttribute
import com.googlecode.cqengine.attribute.SimpleNullableAttribute
import com.googlecode.cqengine.query.option.QueryOptions
import com.jamesdpeters.chestsplusplus.storage.serializable.ChestLinkLocation
import org.bukkit.Chunk
import org.bukkit.Location
import java.util.*

object ChestLinkLocationAttributes {

    val location = object : SimpleAttribute<ChestLinkLocation, Location>() {
        override fun getValue(o: ChestLinkLocation, queryOptions: QueryOptions): Location = o.location!!
    }

    val chunk = object : SimpleAttribute<ChestLinkLocation, Chunk?>() {
        override fun getValue(o: ChestLinkLocation, queryOptions: QueryOptions): Chunk = o.location!!.chunk
    }

    val invUUID = object : SimpleNullableAttribute<ChestLinkLocation, UUID?>() {
        override fun getValue(o: ChestLinkLocation, queryOptions: QueryOptions): UUID? = o.inventoryUUID
    }

}