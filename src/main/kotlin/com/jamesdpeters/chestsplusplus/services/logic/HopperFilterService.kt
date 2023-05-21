package com.jamesdpeters.chestsplusplus.services.logic

import com.jamesdpeters.chestsplusplus.attachedFaceLocation
import com.jamesdpeters.chestsplusplus.hopper
import com.jamesdpeters.chestsplusplus.services.data.ChunkStorageService
import com.jamesdpeters.chestsplusplus.storage.serializable.HopperFilter
import org.bukkit.Location
import org.bukkit.entity.ItemFrame
import org.bukkit.inventory.ItemStack
import org.springframework.stereotype.Service

@Service
class HopperFilterService(
    private val chunkStorageService: ChunkStorageService
) {

    fun addHopperFilter(itemFrame: ItemFrame) {
        itemFrame.attachedFaceLocation.block.let { block ->
            block.blockData.hopper?.let {
                chunkStorageService.getHopperFilterLocation(block.location).let {
                    it.filters!!.add(HopperFilter(itemFrame))
                }
            }
        }
    }

    fun removeHopperFilter(itemFrame: ItemFrame) {
        itemFrame.attachedFaceLocation.block.let { block ->
            block.blockData.hopper?.let {
                chunkStorageService.getHopperFilterLocation(block.location).let {
                    it.filters!!.remove(HopperFilter(itemFrame))
                }
            }
        }
    }

    fun isItemAllowed(location: Location, itemStack: ItemStack): Boolean {
        return chunkStorageService.getHopperFilterLocation(location)
            .isInFilter(itemStack)
    }

}