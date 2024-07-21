package com.jamesdpeters.chestsplusplus.menus

import com.jamesdpeters.chestsplusplus.services.data.inventory.InventoryStorageService
import com.jamesdpeters.chestsplusplus.services.logic.ChestLinkService
import com.jamesdpeters.chestsplusplus.util.ItemBuilder
import fr.minuskube.inv.ClickableItem
import fr.minuskube.inv.InventoryManager
import fr.minuskube.inv.SmartInventory
import fr.minuskube.inv.content.InventoryContents
import fr.minuskube.inv.content.InventoryProvider
import org.bukkit.Material
import org.bukkit.entity.Player
import org.springframework.stereotype.Component

@Component
class ChestLinkMenu(
    private val inventoryStorageService: InventoryStorageService,
    private val chestLinkService: ChestLinkService,
    inventoryManager: InventoryManager
) : InventoryProvider {

    val menu: SmartInventory = SmartInventory.builder()
        .id("chestLinkMenu")
        .title("Inventory Storage")
        .provider(this)
        .manager(inventoryManager)
        .size(6, 9)
        .build()

    override fun init(player: Player, contents: InventoryContents) {
        val pagination = contents.pagination()

        val clickableItems = inventoryStorageService.getPartyInventories(player)
            .map { invStore ->
                ClickableItem.from(invStore.mostCommonItem) {
                    chestLinkService.openChestInventory(player, invStore)
                }
            }.toTypedArray()

        pagination.setItems(*clickableItems)
        pagination.setItemsPerPage(28)

        val border = ItemBuilder.getInstance(Material.GRAY_STAINED_GLASS_PANE).setName(" ").get()
        contents.fillBorders(ClickableItem.empty(border))
        pagination.pageItems.forEach {
            contents.add(it)
        }

        val previous = ItemBuilder.getInstance(Material.ARROW).setName("Previous").get()
        contents.set(5, 2, ClickableItem.from(previous) {
            menu.open(player, contents.pagination().previous().page)
        })

        val next = ItemBuilder.getInstance(Material.ARROW).setName("Next").get()
        contents.set(5, 6, ClickableItem.from(next) {
            menu.open(player, contents.pagination().next().page)
        })

    }

    override fun update(p0: Player?, contents: InventoryContents?) {
    }


}