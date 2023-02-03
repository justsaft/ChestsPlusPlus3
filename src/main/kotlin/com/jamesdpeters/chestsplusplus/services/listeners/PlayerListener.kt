package com.jamesdpeters.chestsplusplus.services.listeners

import com.jamesdpeters.chestsplusplus.services.data.ChunkStorageService
import com.jamesdpeters.chestsplusplus.services.data.PlayerStorageService
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.WorldSaveEvent
import org.springframework.stereotype.Service
import java.util.*

@Service
class PlayerListener(
    private val chunkStorageService: ChunkStorageService,
    private val playerStorageService: PlayerStorageService
) : SpringBukkitListener() {

    @EventHandler
    fun playerLogin(event: PlayerLoginEvent) {
        val store = playerStorageService.playerStore(event.player)
        println("Player logged in, loaded storage: ${store.player.name}")
    }

    @EventHandler
    fun playerLogout(event: PlayerQuitEvent) {
        playerStorageService.savePlayer(event.player)
    }

    @EventHandler
    fun worldSaveEvent(event: WorldSaveEvent) {
        event.world.players.forEach {
            playerStorageService.savePlayer(it)
        }
    }

}