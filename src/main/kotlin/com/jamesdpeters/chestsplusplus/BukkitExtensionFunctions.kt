package com.jamesdpeters.chestsplusplus

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.*
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Directional
import org.bukkit.block.data.type.Hopper
import org.bukkit.entity.Entity
import org.bukkit.entity.Hanging
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

val Location.isChunkLoaded: Boolean
    get() {
        val chunkX: Int = blockX shr 4
        val chunkZ: Int = blockZ shr 4
        return world?.isChunkLoaded(chunkX, chunkZ) == true
    }

val Block.isChestLink: Boolean
    get() = state is Chest || state is Barrel

fun Location.containerAnimation(open: Boolean) {
    if (isChunkLoaded) {
        val container = block.state
        if (container is Lidded) {
            if (open) container.open()
            else Bukkit.getScheduler().scheduleSyncDelayedTask(ChestsPlusPlus.plugin(), container::close, 1)
        }
    }
}

val Array<ItemStack?>.itemAmounts: Map<ItemStack, Int>
    get() {
        val itemAmounts = mutableMapOf<ItemStack, Int>()
        for (itemStack in this) {
            if (itemStack == null)
                continue

            val match = itemAmounts.keys.find { it.isSimilar(itemStack) }

            match?.let {
                val amount = itemAmounts[match]?.plus(itemStack.amount) ?: 0
                itemAmounts[match] = amount
            } ?: run {
                itemAmounts[itemStack] = itemStack.amount
            }
        }
        return itemAmounts
    }

val Array<ItemStack?>.mostCommonItem: ItemStack?
    get() {
        return itemAmounts.entries.maxByOrNull { it.value }?.key?.clone()
    }

val Entity?.itemFrame: ItemFrame?
    get() = if (this is ItemFrame) this else null

val Entity?.player: Player?
    get() = if (this is Player) this else null

val String.toUUID: UUID
    get() = UUID.fromString(this)

val BlockData?.directional: Directional?
    get() = if (this is Directional) this else null

val BlockData?.hopper: Hopper?
    get() = if (this is Hopper) this else null

val Hanging.attachedFaceLocation: Location
    get() {
        return location.block.getRelative(attachedFace).location
    }

val Block?.container: Container?
    get() {
        val container = this?.state
        return if (container is Container) container else null
    }

fun Location.addInventoryOrDrop(invToAdd: Inventory) {
    this.block.container?.inventory
        ?.add(invToAdd)
        ?.drop(this)
    ?: run {
        // ? No container was here so drop items in the world directly
        invToAdd.dropAll(this)
    }
}

fun Location.removeInventoryOrDrop(invToRemoveInto: Inventory) {
    this.block.container?.inventory?.let {
        invToRemoveInto
            .add(it) // ? Add into new inventory
            .drop(this) // ? Drop overflow items
    }
}

fun Inventory.add(inventory: Inventory): Map<Int, ItemStack> {
    val items = inventory.contents.mapNotNull { it }.toTypedArray()
    inventory.clear()
    return addItem(*items)
}

fun Inventory.dropAll(location: Location) {
    forEach {
        it?.let { itemStack ->
            location.world?.dropItem(location, itemStack)
        }
    }
}

fun Map<Int, ItemStack>.drop(location: Location) {
    forEach {
        if (it.value.amount > 0 && it.value.type != Material.AIR)
            location.world?.dropItem(location, it.value)
    }
}

fun Tag<Material>.areItemsTagged(vararg itemStack: ItemStack): Boolean {
    return itemStack.all {
        this.isTagged(it.type)
    }
}

fun ItemStack.isSimilarTag(itemStack: ItemStack): Boolean {
    return ChestsPlusPlus.blockTags!!.any { it.areItemsTagged(itemStack, this) }
            || ChestsPlusPlus.itemTags!!.any { it.areItemsTagged(itemStack, this) }
}