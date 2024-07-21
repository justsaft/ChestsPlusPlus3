package com.jamesdpeters.chestsplusplus.services.commands

import com.jamesdpeters.chestsplusplus.Log
import com.jamesdpeters.chestsplusplus.isChestLink
import com.jamesdpeters.chestsplusplus.menus.ChestLinkMenu
import com.jamesdpeters.chestsplusplus.services.logic.ChestLinkService
import com.jamesdpeters.chestsplusplus.storage.serializable.InventoryStore
import me.gleeming.command.Command
import me.gleeming.command.paramter.Param
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.springframework.stereotype.Service

@Service
class ChestLinkCommands(
    private val chestLinkService: ChestLinkService,
    private val chestLinkMenu: ChestLinkMenu
) : SpringBukkitCommand() {

    override fun onEnable() {
        super.onEnable()
        Log.debug { "Chest Link Commands enabled" }
    }

    @Command(names = ["chestlink add", "cl add"], permission = "chestlink.add", playerOnly = true)
    fun addChestLink(player: Player, @Param(name = "name") inventoryStore: InventoryStore?) {
        if (inventoryStore == null) {
            player.sendMessage("${ChatColor.DARK_RED}Could not create ChestLink")
            return
        }
        player.getTargetBlockExact(10)?.let {
            if (it.isChestLink) {
                chestLinkService.addChestLink(player, it.location, inventoryStore)
            }
        }
    }

    @Command(names = ["chestlink menu", "cl menu"], permission = "chestlink.menu", playerOnly = true)
    fun openInventoryMenu(player: Player) {
        chestLinkMenu.menu.open(player)
    }

    @Command(names = ["chestlink open", "cl open"], permission = "chestlink.open", playerOnly = true)
    fun openChestLink(player: Player, @Param(name = "name") inventoryStore: InventoryStore?) {
        if (inventoryStore != null) {
            chestLinkService.openChestInventory(player, inventoryStore)
        }
    }

    @Command(names = ["chestlink setpublic", "cl setpublic"], permission = "chestlink.setpublic", playerOnly = true)
    fun setChestLinkPublic(player: Player, @Param(name = "name") inventoryStore: InventoryStore?, ) {
        inventoryStore?.isPublic = (!(inventoryStore?.isPublic ?: false))
    }

}