package com.jamesdpeters.chestsplusplus.services.listeners

import com.jamesdpeters.chestsplusplus.*
import com.jamesdpeters.chestsplusplus.services.config.ConfigOptions
import com.jamesdpeters.chestsplusplus.services.config.ServerType
import com.jamesdpeters.chestsplusplus.services.config.SpigotConfig
import com.jamesdpeters.chestsplusplus.services.logic.HopperFilterService
import org.bukkit.block.Hopper
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.springframework.stereotype.Service

@Service
class HopperFilterModule(
    private val configOptions: ConfigOptions,
    private val hopperFilterService: HopperFilterService,
    private val serverType: ServerType,
    private val spigotConfig: SpigotConfig
) : SpringBukkitListener() {

    override val isEnabled: Boolean
        get() = configOptions.isHopperFilterEnabled()

    override fun onEnable() {
        Log.info { "Hopper Filter module enabled." }
    }

    @EventHandler
    fun EntitySpawnEvent.event() {
        entity.itemFrame?.let { itemFrame ->
            itemFrame.attachedFaceLocation.block.blockData.hopper?.let {
                Log.info { "Placed item frame against " }
                hopperFilterService.addHopperFilter(itemFrame)
            }
        }
    }

    @EventHandler
    fun EntityDeathEvent.itemFrameDestroyEvent() {
        entity.itemFrame?.let { itemFrame ->
            itemFrame.attachedFaceLocation.block.blockData.hopper?.let {
                hopperFilterService.removeHopperFilter(itemFrame)
            }
        }
    }

    @EventHandler
    fun EntityInteractEvent.itemFrame() {
        entity.itemFrame?.let {
            Log.info { "Item frame interact!" }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun InventoryMoveItemEvent.filter() {
        if (isCancelled) return

        if (destination.holder is Hopper) {
            if (destination.location?.block?.isBlockPowered == true) return

            if (destination.location != null) {
                isCancelled = !hopperFilterService.isItemAllowed(destination.location!!, this.item)
            }

            if (isCancelled && serverType.type == ServerType.Type.PAPER) {
                var index = source.first(item)
                val hopperAmount = spigotConfig.getWorldSettings(source.location).hopperAmount

                while (true) {
                    if (index >= source.size)
                        return

                    val item = source.getItem(index++) ?: continue

                    if (hopperFilterService.isItemAllowed(destination.location!!, item)) {
                        source.moveTo(destination, item, hopperAmount)
                        return
                    }
                }
            }
        }
    }

}