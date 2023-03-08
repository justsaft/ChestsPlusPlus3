package com.jamesdpeters.chestsplusplus.storage.indexes

import com.googlecode.cqengine.attribute.SimpleAttribute
import com.googlecode.cqengine.query.option.QueryOptions
import com.jamesdpeters.chestsplusplus.storage.serializable.InventoryStore
import java.util.*

object InventoryStoreAttributes {

    var name = object : SimpleAttribute<InventoryStore, String>() {
        override fun getValue(o: InventoryStore, queryOptions: QueryOptions): String = o.name
    }
    var uuid = object : SimpleAttribute<InventoryStore, UUID>() {
        override fun getValue(o: InventoryStore, queryOptions: QueryOptions): UUID = o.uuid
    }
    var owner = object : SimpleAttribute<InventoryStore, UUID>() {
        override fun getValue(o: InventoryStore, queryOptions: QueryOptions?): UUID = o.owner.uniqueId
    }
}