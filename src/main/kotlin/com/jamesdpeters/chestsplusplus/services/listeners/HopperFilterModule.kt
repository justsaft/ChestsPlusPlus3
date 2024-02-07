package com.jamesdpeters.chestsplusplus.services.listeners

import com.jamesdpeters.chestsplusplus.*
import com.jamesdpeters.chestsplusplus.services.config.ConfigOptions
import com.jamesdpeters.chestsplusplus.services.config.ServerType
import com.jamesdpeters.chestsplusplus.services.config.SpigotConfig
import com.jamesdpeters.chestsplusplus.services.data.chunk.ChunkStorageService
import com.jamesdpeters.chestsplusplus.services.logic.HopperFilterService
import org.bukkit.ChatColor
import org.bukkit.block.Hopper
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.hanging.HangingPlaceEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.springframework.stereotype.Service

@Service
class HopperFilterModule(
    private val configOptions: ConfigOptions,
    private val hopperFilterService: HopperFilterService,
    private val serverType: ServerType,
    private val spigotConfig: SpigotConfig,
    private val chunkStorageService: ChunkStorageService
) : SpringBukkitListener() {

    override val isEnabled: Boolean
        get() = configOptions.isHopperFilterEnabled()

    override fun onEnable() {
        Log.info { "Hopper Filter module enabled." }
    }

    @EventHandler
    fun HangingPlaceEvent.event() {
        entity.itemFrame?.let { itemFrame ->
            itemFrame.attachedFaceLocation.block.blockData.hopper?.let {
                Log.info { "Placed item frame against " }
                hopperFilterService.addHopperFilter(itemFrame)
            }
        }
    }

    @EventHandler
    fun HangingBreakEvent.itemFrameDestroyEvent() {
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

    @EventHandler
    fun PlayerInteractAtEntityEvent.interact() {
        rightClicked.itemFrame?.let { itemFrame ->
            itemFrame.attachedFaceLocation.block.let { block ->
                block.blockData.hopper?.let {
                    val filter = chunkStorageService.getHopperFilterLocation(block.location)
                    val hopperFilter = filter.filters?.find { it.itemFrame == itemFrame }
                    hopperFilter?.let {
                        ChestsPlusPlus.scheduleTask(1) {
                            if (it.ignoreFilter()) {
                                player.sendMessage("${ChatColor.DARK_RED}Filter disabled")
                            } else {
                                player.sendMessage("Item ${if (it.filterByItemMeta()) " with similar tags " else ""}will ${ChatColor.BOLD}${if (it.dontAllowThisItem()) "${ChatColor.DARK_RED}not be allowed" else "${ChatColor.DARK_GREEN}be allowed"} ${ChatColor.RESET}through the filter")
                            }
                        }
                    }
                }
            }
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