package com.jamesdpeters.chestsplusplus.services.listeners

import com.jamesdpeters.chestsplusplus.services.data.LocationStorageService
import org.bukkit.event.EventHandler
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.bukkit.event.world.WorldSaveEvent
import org.springframework.stereotype.Service

@Service
class ChunkStorageListener(
    private val locationStorageService: LocationStorageService
) : SpringBukkitListener() {

    @EventHandler
    fun worldSaveEvent(event: WorldSaveEvent) {
        for (loadedChunk in event.world.loadedChunks) {
            locationStorageService.persistChunk(loadedChunk)
        }
    }

    @EventHandler
    fun chunkLoad(event: ChunkLoadEvent) {
        locationStorageService.loadChunk(event.chunk)
    }

    @EventHandler
    fun chunkUnload(event: ChunkUnloadEvent) {
        locationStorageService.unloadChunk(event.chunk)
    }

}