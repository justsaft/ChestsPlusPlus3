package com.jamesdpeters.chestsplusplus.storage.serializable

import com.jamesdpeters.chestsplusplus.serialize.ConfigSerialize
import com.jamesdpeters.chestsplusplus.serialize.serializers.ItemFrameSerializer
import org.bukkit.Location
import org.bukkit.entity.ItemFrame
import org.bukkit.inventory.ItemStack
import java.util.UUID

class LocationInfo : SerializableObject {

    @ConfigSerialize
    var location: Location? = null
        private set

    @ConfigSerialize
    var inventoryUUID: UUID? = null

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
        itemFrame?.setItem(itemStack)
    }

    fun reset() {
        inventoryUUID = null
        itemFrame?.remove()
    }

}