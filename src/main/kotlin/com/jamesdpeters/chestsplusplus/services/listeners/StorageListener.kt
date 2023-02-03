package com.jamesdpeters.chestsplusplus.services.listeners

import com.jamesdpeters.chestsplusplus.*
import com.jamesdpeters.chestsplusplus.services.data.ChunkStorageService
import com.jamesdpeters.chestsplusplus.services.data.PlayerStorageService
import com.jamesdpeters.chestsplusplus.services.logic.ChestLinkService
import com.jamesdpeters.chestsplusplus.spigot.event.LocationInfoLoadEvent
import com.jamesdpeters.chestsplusplus.storage.serializable.InventoryStore
import com.jamesdpeters.chestsplusplus.storage.serializable.InventoryStore.Companion.inventoryStore
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.data.Directional
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.persistence.PersistentDataType
import org.springframework.stereotype.Service
import java.util.*

@Service
class StorageListener(
    private val chestLinkService: ChestLinkService,
    private val playerStorageService: PlayerStorageService,
    private val chunkStorageService: ChunkStorageService,
    private val itemFrameKey: NamespacedKey
) : SpringBukkitListener() {

    private val log = ChestsPlusPlus.logger()

    // ? Chest Link Inventory Events

    @EventHandler
    fun chestOpen(event: PlayerInteractEvent) {
        if (event.player.isSneaking || event.action != Action.RIGHT_CLICK_BLOCK)
            return

        event.clickedBlock?.let { block ->
            if (block.isChestLink) {
                playerStorageService.inventoryStoreAtLocation(block.location)?.let { invStore ->
                    event.isCancelled = true
                    chestLinkService.openChestInventory(event.player, invStore)
                }
            }
        }
    }

    @EventHandler
    fun closeInventory(event: InventoryCloseEvent) {
        event.inventory.holder.inventoryStore?.let { invStore ->
            event.inventory.viewers.remove(event.player)
            if (event.inventory.viewers.size == 0) {
                chunkStorageService.getInventoryLocations(invStore.uuid).forEach {
                    it.containerAnimation(false)
                }
            }
        }
    }

    // ? ChestLink Add/Remove events

    @EventHandler
    fun createChestLink(event: PlayerInteractEvent) {
        if (event.player.isSneaking) {
            event.item?.let { item ->
                item.itemMeta?.let { itemMeta ->
                    event.clickedBlock?.let { block ->
                        if (item.type == Material.NAME_TAG && block.isChestLink) {
                            chestLinkService.addChestLink(event.player, block.location, itemMeta.displayName)
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun storageBreak(event: BlockBreakEvent) {
        if (event.block.isChestLink) {
            chunkStorageService.removeLocationInfo(event.block.location)
        }
    }

    @EventHandler
    fun storageLoaded(event: LocationInfoLoadEvent) {
        log.info { "Loaded location: ${event.locationInfo.location}" }
        event.locationInfo.location?.let { location ->
            if (event.locationInfo.inventoryUUID == null)
                return

            location.block.blockData.directional?.let { chest ->
                val relativeBlock = location.block.getRelative(chest.facing)

                val itemFrame = relativeBlock.location.world?.spawn(relativeBlock.location, ItemFrame::class.java)
                itemFrame?.persistentDataContainer?.set(itemFrameKey, PersistentDataType.STRING, event.locationInfo.inventoryUUID.toString())
                itemFrame?.setFacingDirection(chest.facing)
                itemFrame?.isFixed = true
                itemFrame?.isVisible = false

                event.locationInfo.itemFrame = itemFrame

                playerStorageService.playerStoreByInventoryUUID(event.locationInfo.inventoryUUID!!)?.inventoryStore(event.locationInfo.inventoryUUID!!)?.apply {
                    calculateMostCommonItem()
                    event.locationInfo.updateItemFrame(mostCommonItem)
                }

                log.info { "Spawned item frame: $itemFrame" }
            }
        }
    }

    // ? ChestLink Inventory update events

    @EventHandler(priority = EventPriority.LOWEST)
    fun onInventoryPlayerUpdate(event: InventoryClickEvent) = itemFrameUpdate(event)

    @EventHandler
    fun onInventoryPlayerUpdate(event: InventoryDragEvent) = itemFrameUpdate(event)

    private fun itemFrameUpdate(event: InventoryInteractEvent) {
        event.inventory.holder.inventoryStore?.let {inventoryStore ->
            Bukkit.getScheduler().scheduleSyncDelayedTask(ChestsPlusPlus.plugin(), {
                inventoryStore.calculateMostCommonItem()
                chunkStorageService.getInventoryLocationInfos(inventoryStore.uuid).forEach {
                    it.updateItemFrame(inventoryStore.mostCommonItem)
                }
            }, 1)
        }
    }

    // ? ChestLink ItemFrame events

    @EventHandler
    fun itemFrameBreak(event: HangingBreakByEntityEvent) {
        event.entity.itemFrame?.let { itemFrame ->
            if (event.cause == HangingBreakEvent.RemoveCause.ENTITY) {
                // Check if the item frame has the PDC key
                if (itemFrame.persistentDataContainer.has(itemFrameKey, PersistentDataType.STRING)) {
                    // Cancel the event to prevent the item frame from breaking
                    event.isCancelled = true

                    event.remover.player?.let { player ->
                        // If a Player broke the ItemFrame and wasn't sneaking we should open the chest instead.
                        if (!player.isSneaking) {
                            // Get the UUID stored in the item frame's persistent data container
                            itemFrame.persistentDataContainer[itemFrameKey, PersistentDataType.STRING]?.toUUID?.let {
                                playerStorageService.playerStoreByInventoryUUID(it)?.inventoryStore(it)
                                    ?.let { inv ->
                                        chestLinkService.openChestInventory(player, inv)
                                    }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun itemFrameInteract(event: PlayerInteractEntityEvent) {
        event.rightClicked.itemFrame?.let { itemFrame ->
            if (itemFrame.persistentDataContainer.has(itemFrameKey, PersistentDataType.STRING)) {
                itemFrame.location.block.getRelative(itemFrame.attachedFace).location.let { location ->
                    event.isCancelled = true
                    playerStorageService.inventoryStoreAtLocation(location)?.let { inventoryStore ->
                        chestLinkService.openChestInventory(event.player, inventoryStore)
                    }
                }
            }
        }
    }

}