package com.jamesdpeters.chestsplusplus.services.commands.processors

import com.jamesdpeters.chestsplusplus.player
import com.jamesdpeters.chestsplusplus.services.data.inventory.InventoryStorageService
import com.jamesdpeters.chestsplusplus.storage.serializable.InventoryStore
import me.gleeming.command.paramter.Processor
import org.bukkit.command.CommandSender
import org.springframework.stereotype.Service

@Service
class InventoryStoreCommandProcessor(
    private val inventoryStorageService: InventoryStorageService
) : Processor<InventoryStore>() {

    override fun process(sender: CommandSender?, supplied: String?): InventoryStore? {
        sender.player?.let {
            if (supplied != null) {
                return inventoryStorageService.getInventoryStoreOrCreate(it, supplied)
            }
        }
        return null
    }

    override fun tabComplete(sender: CommandSender?, supplied: String?): MutableList<String> {
        sender.player?.let { player ->
            if (supplied != null) {
                return inventoryStorageService.inventories(player)
                    .map { it.name }
                    .filter { s -> s.startsWith(supplied, true)}
                    .toMutableList()
            }
        }
        return super.tabComplete(sender, supplied)
    }

}