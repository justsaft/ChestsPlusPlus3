package com.jamesdpeters.chestsplusplus.services.listeners

import com.jamesdpeters.chestsplusplus.ChestsPlusPlus
import com.jamesdpeters.chestsplusplus.services.data.ChunkStorageService
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.event.world.WorldSaveEvent
import org.springframework.stereotype.Service

@Service
class ChunkStorageListener(
    private val chunkStorageService: ChunkStorageService
) : SpringBukkitListener() {

    private val log = ChestsPlusPlus.logger()

    @EventHandler
    fun worldSaveEvent(event: WorldSaveEvent) {
        for (loadedChunk in event.world.loadedChunks) {
            chunkStorageService.persistChunk(loadedChunk)
        }
    }

    @EventHandler
    fun chunkLoad(event: ChunkLoadEvent) {
        chunkStorageService.loadChunk(event.chunk)
    }

    @EventHandler
    fun chunkUnload(event: ChunkUnloadEvent) {
        chunkStorageService.unloadChunk(event.chunk)
    }

}