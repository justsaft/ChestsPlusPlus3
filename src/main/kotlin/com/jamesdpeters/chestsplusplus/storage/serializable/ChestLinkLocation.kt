package com.jamesdpeters.chestsplusplus.storage.serializable

import com.jamesdpeters.chestsplusplus.serialize.ConfigSerialize
import com.jamesdpeters.chestsplusplus.serialize.serializers.ItemFrameSerializer
import com.jamesdpeters.chestsplusplus.services.config.ConfigOptions
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ItemFrame
import org.bukkit.inventory.ItemStack
import java.util.*

class ChestLinkLocation : SerializableObject {

    @ConfigSerialize
    var location: Location? = null
        private set

    @ConfigSerialize
    var inventoryUUID: UUID? = null
        private set

    @ConfigSerialize(ItemFrameSerializer::class)
    var itemFrame: ItemFrame? = null

    @Suppress("unused")
    constructor(map: MutableMap<String?, Any?>) : super(map)

    constructor(location: Location, inventoryUUID: UUID? = null, itemFrame: ItemFrame? = null) {
        this.location = location
        this.inventoryUUID = inventoryUUID
        this.itemFrame = itemFrame
    }

    fun updateItemFrame(itemStack: ItemStack?) {
        if (itemFrame != null) {
            itemFrame!!.setItem(itemStack, false)
            itemFrame?.isVisible = (itemStack == null || itemStack.type == Material.AIR) && ConfigOptions().useInvisibleItemFramesChestLink()
        }
    }

    fun remove() {
        inventoryUUID = null
        itemFrame?.remove()
    }

}