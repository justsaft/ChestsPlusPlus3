package com.jamesdpeters.chestsplusplus.services.data

import com.googlecode.cqengine.ConcurrentIndexedCollection
import com.googlecode.cqengine.index.hash.HashIndex
import com.googlecode.cqengine.index.radix.RadixTreeIndex
import com.googlecode.cqengine.index.unique.UniqueIndex
import com.googlecode.cqengine.query.QueryFactory.and
import com.googlecode.cqengine.query.QueryFactory.equal
import com.jamesdpeters.chestsplusplus.storage.indexes.InventoryStoreAttributes
import com.jamesdpeters.chestsplusplus.storage.serializable.InventoryStore
import com.jamesdpeters.chestsplusplus.storage.yaml.YamlFileStorage
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.springframework.stereotype.Service
import java.util.*

@Service
class InventoryStorageService(
    private val chunkStorageService: ChunkStorageService
) : PersistableService, YamlFileStorage<List<InventoryStore>>() {

    private val inventoryStores = ConcurrentIndexedCollection<InventoryStore>()

    init {
        inventoryStores.addIndex(UniqueIndex.onAttribute(InventoryStoreAttributes.uuid))
        inventoryStores.addIndex(HashIndex.onAttribute(InventoryStoreAttributes.owner))
        inventoryStores.addIndex(RadixTreeIndex.onAttribute(InventoryStoreAttributes.name))
    }

    /** Overridden functions */

    override fun persist() {
        save(inventoryStores.toList())
    }

    override fun reload() {
        inventoryStores.clear()
        load<List<InventoryStore>>()?.let {
            inventoryStores.addAll(it)
        }
    }

    override val directory: String
        get() = "storage"

    override val filename: String
        get() = "inventories.yml"

    /** Public functions */

    fun inventories(player: OfflinePlayer): List<InventoryStore> {
        inventoryStores.retrieve(equal(InventoryStoreAttributes.owner, player.uniqueId)).use { query ->
            return if (query.isNotEmpty) query.toList() else emptyList()
        }
    }

    fun inventoryStore(uuid: UUID): InventoryStore? {
        return inventoryStores.retrieve(equal(InventoryStoreAttributes.uuid, uuid)).let {
            if (it.isNotEmpty) it.uniqueResult() else null
        }
    }

    fun inventoryStoreAtLocation(location: Location): InventoryStore? {
        return chunkStorageService.getChestLinkLocation(location)?.let {
            it.inventoryUUID?.let { uuid ->
                inventoryStore(uuid)
            }
        }
    }

    fun inventoryStore(player: OfflinePlayer, name: String): InventoryStore? {
        return inventoryStores.retrieve(and(equal(InventoryStoreAttributes.owner, player.uniqueId), equal(InventoryStoreAttributes.name, name)))?.let {
            return if (it.isNotEmpty) it.uniqueResult() else null
        }
    }

    private fun createInventory(player: OfflinePlayer, name: String): InventoryStore {
        return InventoryStore(player, name).also {
            inventoryStores.add(it)
        }
    }

    fun getInventoryStoreOrCreate(player: OfflinePlayer, name: String): InventoryStore {
        return inventoryStore(player, name) ?: createInventory(player, name)
    }

    fun removeInventoryStore(uuid: UUID): InventoryStore? {
        return inventoryStore(uuid)?.also {
            inventoryStores.remove(it)
        }
    }

}