package com.jamesdpeters.chestsplusplus.services.listeners

import com.jamesdpeters.chestsplusplus.Log
import com.jamesdpeters.chestsplusplus.services.data.InventoryStorageService
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.world.WorldSaveEvent
import org.springframework.stereotype.Service

@Service
class PlayerListener(
    private val inventoryStorageService: InventoryStorageService
) : SpringBukkitListener() {

    @EventHandler
    fun PlayerLoginEvent.playerLogin() {
        Log.debug {
            val stores = inventoryStorageService.inventories(player)
            "${player.name} logged in. Found ${stores.size} storages"
        }
    }

    @Suppress("UnusedReceiverParameter")
    @EventHandler
    fun WorldSaveEvent.worldSaveEvent() {
        inventoryStorageService.persist()
    }

}