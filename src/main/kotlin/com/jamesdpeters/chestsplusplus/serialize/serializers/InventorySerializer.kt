package com.jamesdpeters.chestsplusplus.serialize.serializers

import com.jamesdpeters.chestsplusplus.serialize.CppSerializer
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.springframework.stereotype.Component

@Component
class InventorySerializer : CppSerializer<Inventory?, Array<ItemStack?>?> {

    override fun serialize(from: Inventory?): Array<ItemStack?>? {
        return from?.contents
    }

    override fun deserialize(from: Array<ItemStack?>?): Inventory? {
        if (from != null) {
            val inventory = Bukkit.createInventory(null, from.size)
            inventory.contents = from
            return inventory
        }
        return null
    }
}