package com.jamesdpeters.chestsplusplus.storage.serializable

import com.googlecode.cqengine.ConcurrentIndexedCollection
import com.googlecode.cqengine.index.hash.HashIndex
import com.googlecode.cqengine.index.unique.UniqueIndex
import com.googlecode.cqengine.query.QueryFactory.equal
import com.jamesdpeters.chestsplusplus.serialize.ConfigSerialize
import com.jamesdpeters.chestsplusplus.serialize.serializers.ConcurrentIndexedCollectionSerializer
import com.jamesdpeters.chestsplusplus.serialize.serializers.OfflinePlayerUUIDSerializer
import com.jamesdpeters.chestsplusplus.storage.indexes.InventoryStoreAttributes
import org.bukkit.OfflinePlayer
import java.util.UUID

class PlayerStore : SerializableObject {

    @ConfigSerialize(OfflinePlayerUUIDSerializer::class)
    lateinit var player: OfflinePlayer

    @ConfigSerialize(ConcurrentIndexedCollectionSerializer::class)
    var inventories: ConcurrentIndexedCollection<InventoryStore>? = null

    @Suppress("unused")
    constructor(map: MutableMap<String?, Any?>) : super(map) {
        initIndexes()
    }

    constructor(player: OfflinePlayer) {
        this.player = player
        initIndexes()
    }

    fun initIndexes() {
        if (inventories == null) inventories = ConcurrentIndexedCollection()
        inventories!!.addIndex(UniqueIndex.onAttribute(InventoryStoreAttributes.uuid))
        inventories!!.addIndex(HashIndex.onAttribute(InventoryStoreAttributes.name))
        println("Initialised PlayerStore for player ${player.name}")
    }

    fun inventoryStore(name: String): InventoryStore {
        return inventories!!.retrieve(equal(InventoryStoreAttributes.name, name)).let {
            if (it.isNotEmpty) it.uniqueResult()
            else {
                val store = InventoryStore(name)
                inventories!!.add(store)
                return store
            }
        }
    }

    fun inventoryStore(uuid: UUID): InventoryStore? {
        return inventories!!.retrieve(equal(InventoryStoreAttributes.uuid, uuid)).let {
            if (it.isNotEmpty) it.uniqueResult() else null
        }
    }
}