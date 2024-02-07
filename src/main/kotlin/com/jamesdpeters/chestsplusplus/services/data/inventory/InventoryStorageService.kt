package com.jamesdpeters.chestsplusplus.services.data.inventory

import com.jamesdpeters.chestsplusplus.services.data.PersistableService
import com.jamesdpeters.chestsplusplus.services.data.chunk.ChunkStorageService
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

    private val inventoryStorage = InventoryStorageMap()

    /** Overridden functions */

    override fun persist() {
        save(inventoryStorage.values.toList())
    }

    override fun reload() {
        inventoryStorage.clear()
        load<List<InventoryStore>>()?.let {
            inventoryStorage.addAll(it)
        }
    }

    override val directory: String
        get() = "storage"

    override val filename: String
        get() = "inventories.yml"

    /** Public functions */

    fun inventories(player: OfflinePlayer): Collection<InventoryStore>? {
        return inventoryStorage.getAllByOwner(player.uniqueId)
    }

    fun inventoryStore(uuid: UUID): InventoryStore? {
        return inventoryStorage[uuid]
    }

    fun inventoryStoreAtLocation(location: Location): InventoryStore? {
        return chunkStorageService.getChestLinkLocation(location)?.let {
            it.inventoryUUID?.let { uuid ->
                inventoryStore(uuid)
            }
        }
    }

    fun inventoryStore(player: OfflinePlayer, name: String): InventoryStore? {
        return inventoryStorage.getByOwnerAndName(player.uniqueId, name)
    }

    private fun createInventory(player: OfflinePlayer, name: String): InventoryStore {
        return InventoryStore(player, name).also {
            inventoryStorage.add(it)
        }
    }

    fun getInventoryStoreOrCreate(player: OfflinePlayer, name: String): InventoryStore {
        return inventoryStore(player, name) ?: createInventory(player, name)
    }

    fun removeInventoryStore(uuid: UUID): InventoryStore? {
        return inventoryStorage.remove(uuid)
    }

}