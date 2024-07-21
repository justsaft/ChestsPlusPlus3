package com.jamesdpeters.chestsplusplus.util

import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta


@Suppress("unused")
class ItemBuilder private constructor() {

    var lore: MutableList<String>? = null
    var material: Material? = null
    var enchanted = false
    var name: String? = null
    var skullOwner: OfflinePlayer? = null
    var itemStack: ItemStack? = null

    fun setName(name: String?): ItemBuilder {
        this.name = name
        return this
    }

    fun setMaterial(material: Material?): ItemBuilder {
        this.material = material
        return this
    }

    fun addLore(lore: String): ItemBuilder {
        if (this.lore == null) this.lore = ArrayList()
        this.lore!!.add(lore)
        return this
    }

    fun setEnchanted(enchanted: Boolean): ItemBuilder {
        this.enchanted = enchanted
        return this
    }

    fun get(): ItemStack? {
        if (itemStack == null) itemStack = ItemStack(material!!)
        val meta = itemStack!!.itemMeta
        if (meta != null) {
            if (name != null) meta.setDisplayName(name)
            if (lore != null) meta.lore = lore
            if (enchanted) {
                itemStack!!.addUnsafeEnchantment(Enchantment.LURE, 1)
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            }
            if (skullOwner != null && meta is SkullMeta) {
                meta.setOwningPlayer(skullOwner)
            }
            itemStack!!.setItemMeta(meta)
        }
        return itemStack
    }

    companion object {
        // Material must be provided for every item.
        fun getInstance(material: Material?): ItemBuilder {
            val builder = ItemBuilder()
            builder.setMaterial(material)
            return builder
        }

        fun fromInstance(itemStack: ItemStack?): ItemBuilder {
            val builder = ItemBuilder()
            builder.itemStack = itemStack
            return builder
        }

        fun getPlayerHead(skullOwner: OfflinePlayer?): ItemBuilder {
            val builder = getInstance(Material.PLAYER_HEAD)
            builder.skullOwner = skullOwner
            return builder
        }
    }
}

