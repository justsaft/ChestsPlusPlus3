package com.jamesdpeters.chestsplusplus.services.logic

import com.jamesdpeters.chestsplusplus.Log
import com.jamesdpeters.chestsplusplus.addInventoryOrDrop
import com.jamesdpeters.chestsplusplus.containerAnimation
import com.jamesdpeters.chestsplusplus.removeInventoryOrDrop
import com.jamesdpeters.chestsplusplus.services.data.chunk.ChunkStorageService
import com.jamesdpeters.chestsplusplus.services.data.inventory.InventoryStorageService
import com.jamesdpeters.chestsplusplus.storage.serializable.InventoryStore
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.springframework.stereotype.Service

@Service
open class ChestLinkService(
    private val inventoryStorageService: InventoryStorageService,
    private val chunkStorageService: ChunkStorageService,
    private val permissionsService: PermissionsService,
    private val partyService: PartyService?
) {

    fun openChestInventory(player: Player, inventoryStore: InventoryStore) {
        if (inventoryStore.isPublic != true) {
            partyService?.partiesAPI?.let {
                if (player.uniqueId != inventoryStore.owner.uniqueId && !it.areInTheSameParty(
                        player.uniqueId,
                        inventoryStore.owner.uniqueId
                    )
                ) {
                    player.sendMessage("You don't have access to this ChestLink owned by ${inventoryStore.owner.name}")
                    return
                }
            }
        }
        Log.debug { "$inventoryStore" }
        if (permissionsService.hasChestLinkOpenPermission(player) || inventoryStore.isPublic == true) {
            player.openInventory(inventoryStore.inventory)
            chunkStorageService.getChestLinkLocations(inventoryStore.uuid)
                ?.forEach { it.location?.containerAnimation(true) }
        }
    }

    fun addChestLink(player: Player, location: Location, name: String): Boolean {
        val inventoryStore = inventoryStorageService.getInventoryStoreOrCreate(player, name)
        return addChestLink(player, location, inventoryStore)
    }

    fun addChestLink(player: Player, location: Location, inventoryStore: InventoryStore): Boolean {
        if (permissionsService.hasChestLinkCreatePermission(player)) {
            if (chunkStorageService.createChestLinkLocation(location, inventoryStore.uuid)) {
                location.removeInventoryOrDrop(inventoryStore.inventory) // ? Remove inventory from Container into InvStore
                player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
                player.sendMessage("${ChatColor.DARK_GREEN}Created ChestLink [${inventoryStore.name}]")
                return true
            }
        } else {
            player.sendMessage("${ChatColor.DARK_RED}${player.name} does not have permission to create ChestLinks")
        }
        return false
    }

    fun removeChestLink(location: Location) {
        chunkStorageService.removeChestLinkLocation(location)?.apply {
            inventoryUUID?.let { uuid ->
                if (chunkStorageService.getChestLinkLocations(uuid)?.isEmpty() == true) {
                    inventoryStorageService.removeInventoryStore(uuid)?.apply {
                        location.addInventoryOrDrop(inventory)
                    }
                }
            }
            remove()
        }
    }

}