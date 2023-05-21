package com.jamesdpeters.chestsplusplus.services.logic

import com.jamesdpeters.chestsplusplus.addInventoryOrDrop
import com.jamesdpeters.chestsplusplus.containerAnimation
import com.jamesdpeters.chestsplusplus.removeInventoryOrDrop
import com.jamesdpeters.chestsplusplus.services.data.ChunkStorageService
import com.jamesdpeters.chestsplusplus.services.data.InventoryStorageService
import com.jamesdpeters.chestsplusplus.storage.serializable.InventoryStore
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.springframework.stereotype.Service

@Service
class ChestLinkService(
    private val inventoryStorageService: InventoryStorageService,
    private val chunkStorageService: ChunkStorageService
) {

    fun openChestInventory(player: Player, inventoryStore: InventoryStore) {
        player.openInventory(inventoryStore.inventory)
        chunkStorageService.getChestLinkLocations(inventoryStore.uuid).forEach { it.location?.containerAnimation(true) }
    }

    fun addChestLink(player: Player, location: Location, name: String) {
        val inventoryStore = inventoryStorageService.getInventoryStoreOrCreate(player, name)
        location.removeInventoryOrDrop(inventoryStore.inventory) // ? Remove inventory from Container into InvStore
        chunkStorageService.createChestLinkLocation(location, inventoryStore.uuid)
        player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
        player.sendMessage("Created ChestLink [$name]")
    }

    fun removeChestLink(location: Location) {
        chunkStorageService.removeChestLinkLocation(location)?.apply {
            inventoryUUID?.let { uuid ->
                if (chunkStorageService.getChestLinkLocations(uuid).isEmpty()) {
                    inventoryStorageService.removeInventoryStore(uuid)?.apply {
                        location.addInventoryOrDrop(inventory)
                    }
                }
            }
            remove()
        }
    }

}