package com.jamesdpeters.chestsplusplus.storage.indexes

import com.googlecode.cqengine.attribute.MultiValueAttribute
import com.googlecode.cqengine.attribute.SimpleAttribute
import com.googlecode.cqengine.query.option.QueryOptions
import com.jamesdpeters.chestsplusplus.storage.serializable.HopperFilterLocation
import org.bukkit.Chunk
import org.bukkit.Location
import java.util.*

object HopperFilterLocationAttributes {

    val location = object : SimpleAttribute<HopperFilterLocation, Location>() {
        override fun getValue(o: HopperFilterLocation, queryOptions: QueryOptions): Location = o.location!!
    }

    val chunk = object : SimpleAttribute<HopperFilterLocation, Chunk?>() {
        override fun getValue(o: HopperFilterLocation, queryOptions: QueryOptions): Chunk = o.location!!.chunk
    }

    val itemFrameUUID = object : MultiValueAttribute<HopperFilterLocation, UUID>() {
        override fun getValues(o: HopperFilterLocation, queryOptions: QueryOptions?): MutableIterable<UUID> {
            return o.filters!!.map { it.itemFrame!!.uniqueId }.toMutableList()
        }

    }

}