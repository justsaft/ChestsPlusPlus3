package com.jamesdpeters.chestsplusplus.services.logic

import com.jamesdpeters.chestsplusplus.ChestsPlusPlus
import com.jamesdpeters.chestsplusplus.containerAnimation
import com.jamesdpeters.chestsplusplus.isChunkLoaded
import com.jamesdpeters.chestsplusplus.services.data.ChunkStorageService
import com.jamesdpeters.chestsplusplus.services.data.PlayerStorageService
import com.jamesdpeters.chestsplusplus.storage.serializable.InventoryStore
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.block.Lidded
import org.bukkit.entity.Player
import org.springframework.stereotype.Service

@Service
class ChestLinkService(
    private val playerStorageService: PlayerStorageService,
    private val chunkStorageService: ChunkStorageService
) {

    fun openChestInventory(player: Player, inventoryStore: InventoryStore) {
        player.openInventory(inventoryStore.inventory)
        chunkStorageService.getInventoryLocations(inventoryStore.uuid).forEach { it.containerAnimation(true) }
    }

    fun addChestLink(player: Player, location: Location, name: String) {
        val inventoryStore = playerStorageService.playerStore(player).inventoryStore(name)
        chunkStorageService.setInventoryUUID(location, inventoryStore.uuid)
        player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)
    }

}