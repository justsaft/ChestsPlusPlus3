package com.jamesdpeters.chestsplusplus.services.data.inventory

import com.jamesdpeters.chestsplusplus.services.data.PersistableService
import com.jamesdpeters.chestsplusplus.services.data.chunk.ChunkStorageService
import com.jamesdpeters.chestsplusplus.services.logic.PartyService
import com.jamesdpeters.chestsplusplus.storage.serializable.InventoryStore
import com.jamesdpeters.chestsplusplus.storage.yaml.YamlFileStorage
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.springframework.stereotype.Service
import java.util.*

@Service
open class InventoryStorageService(
    private val chunkStorageService: ChunkStorageService,
    private val partyService: PartyService?
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

    fun inventories(player: OfflinePlayer): List<InventoryStore> {
        return inventoryStorage.getAllByOwner(player.uniqueId)
    }

    fun getInventoryStore(uuid: UUID): InventoryStore? {
        return inventoryStorage[uuid]
    }

    fun inventoryStoreAtLocation(location: Location): InventoryStore? {
        return chunkStorageService.getChestLinkLocation(location)?.let {
            it.inventoryUUID?.let { uuid ->
                getInventoryStore(uuid)
            }
        }
    }

    fun getInventoryStore(player: OfflinePlayer, name: String): InventoryStore? {
        return inventoryStorage.getByOwnerAndName(player.uniqueId, name)
    }

    private fun createInventory(player: OfflinePlayer, name: String): InventoryStore {
        return InventoryStore(player, name).also {
            inventoryStorage.add(it)
        }
    }

    fun getInventoryStoreOrCreate(player: OfflinePlayer, name: String): InventoryStore {
        return getInventoryStore(player, name) ?: createInventory(player, name)
    }

    fun removeInventoryStore(uuid: UUID): InventoryStore? {
        return inventoryStorage.remove(uuid)
    }

    fun getPartyInventories(player: OfflinePlayer) : List<InventoryStore> {
        val partyInventories = mutableListOf<InventoryStore>()
        partyInventories.addAll(inventories(player))
        partyService?.partiesAPI?.getPartyOfPlayer(player.uniqueId)?.let {
            it.members
                .filter { uuid -> player.uniqueId != uuid }
                .forEach { uuid -> partyInventories.addAll(inventoryStorage.getAllByOwner(uuid)) }
        }
        return partyInventories
    }

}