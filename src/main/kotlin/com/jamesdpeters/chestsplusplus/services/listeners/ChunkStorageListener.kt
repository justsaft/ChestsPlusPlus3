package com.jamesdpeters.chestsplusplus.services.listeners

import com.jamesdpeters.chestsplusplus.services.data.chunk.ChunkStorageService
import org.bukkit.event.EventHandler
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.event.world.WorldSaveEvent
import org.springframework.stereotype.Service

@Service
class ChunkStorageListener(
    private val chunkStorageService: ChunkStorageService
) : SpringBukkitListener() {

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