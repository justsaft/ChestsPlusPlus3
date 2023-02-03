package com.jamesdpeters.chestsplusplus.storage.indexes

import com.googlecode.cqengine.attribute.SimpleAttribute
import com.googlecode.cqengine.attribute.SimpleNullableAttribute
import com.googlecode.cqengine.query.option.QueryOptions
import com.jamesdpeters.chestsplusplus.storage.serializable.LocationInfo
import org.bukkit.Location
import java.util.*

object LocationInfoAttributes {

    val location = object : SimpleAttribute<LocationInfo, Location?>() {
        override fun getValue(o: LocationInfo, queryOptions: QueryOptions): Location? = o.location
    }

    val invUUID = object : SimpleNullableAttribute<LocationInfo, UUID?>() {
        override fun getValue(o: LocationInfo, queryOptions: QueryOptions): UUID? = o.inventoryUUID
    }

}