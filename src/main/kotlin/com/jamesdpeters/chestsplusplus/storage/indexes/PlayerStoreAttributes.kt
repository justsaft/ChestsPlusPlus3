package com.jamesdpeters.chestsplusplus.storage.indexes

import com.googlecode.cqengine.attribute.MultiValueAttribute
import com.googlecode.cqengine.attribute.SimpleAttribute
import com.googlecode.cqengine.query.option.QueryOptions
import com.jamesdpeters.chestsplusplus.storage.serializable.PlayerStore
import java.util.*

object PlayerStoreAttributes {

    var player = object : SimpleAttribute<PlayerStore, UUID>() {
        override fun getValue(obj: PlayerStore, queryOptions: QueryOptions): UUID = obj.player.uniqueId
    }

    var inventoryUUID = object : MultiValueAttribute<PlayerStore, UUID>() {
        override fun getValues(obj: PlayerStore, queryOptions: QueryOptions): MutableIterable<UUID> =
            obj.inventories!!.map { it.uuid }.toMutableList()
    }

}