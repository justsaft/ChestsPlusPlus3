package com.jamesdpeters.chestsplusplus.storage.serializable

import com.jamesdpeters.chestsplusplus.mostCommonItem
import com.jamesdpeters.chestsplusplus.serialize.ConfigSerialize
import com.jamesdpeters.chestsplusplus.serialize.serializers.ArraySerializer
import com.jamesdpeters.chestsplusplus.serialize.serializers.UUIDSerializer
import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import java.util.*

class InventoryStore : SerializableObject, InventoryHolder {

    companion object {
        val InventoryHolder?.inventoryStore: InventoryStore?
            get() = if (this is InventoryStore) this else null
    }

    private val invSize = 54

    @ConfigSerialize(UUIDSerializer::class)
    lateinit var uuid: UUID
        private set

    @ConfigSerialize
    lateinit var name: String
        private set

    @ConfigSerialize(ArraySerializer::class)
    private var inventoryItems: Array<ItemStack?>? = null

    private lateinit var inventory: Inventory

    var mostCommonItem: ItemStack? = null
        private set

    /** Constructors */

    @Suppress("unused")
    constructor(map: MutableMap<String?, Any?>) : super(map) {
        init()
    }

    constructor(name: String) {
        this.uuid = UUID.randomUUID()
        this.name = name
        init()
    }

    private fun init() {
        if (inventoryItems == null) inventoryItems = arrayOfNulls(invSize)
        inventory = Bukkit.createInventory(this, invSize, name)
        inventoryItems?.let {
            inventory.contents = it
        }
        calculateMostCommonItem()
    }

    /** Overridden functions */

    override fun preSerialize() { inventoryItems = inventory.contents }
    override fun getInventory(): Inventory = inventory

    /** public functions */

    fun calculateMostCommonItem() {
        this.mostCommonItem = inventory.contents.mostCommonItem?.apply {
            itemMeta = itemMeta?.apply {
                setDisplayName(name)
            }
        }
    }

}