package com.jamesdpeters.chestsplusplus.services.data.inventory

import com.google.common.collect.BiMap
import com.google.common.collect.HashBasedTable
import com.google.common.collect.HashBiMap
import com.google.common.collect.Table
import com.jamesdpeters.chestsplusplus.storage.serializable.InventoryStore
import org.springframework.util.MultiValueMap
import java.util.UUID

typealias OwnerUUID = UUID
typealias InventoryUUID = UUID

class InventoryStorageMap : HashMap<InventoryUUID, InventoryStore>() {

    private val ownerTable = HashBasedTable.create<OwnerUUID, String, InventoryUUID>()

    private fun addToOtherMap(value: InventoryStore) {
        val ownerUUID = value.owner.uniqueId
        ownerTable.put(ownerUUID, value.name, value.uuid)
    }

    override fun put(key: InventoryUUID, value: InventoryStore): InventoryStore? {
        addToOtherMap(value)
        return super.put(key, value)
    }

    override fun remove(key: InventoryUUID): InventoryStore? {
        return super.remove(key)?.also {
            ownerTable.remove(it.owner.uniqueId, it.name)
        }
    }

    override fun remove(key: InventoryUUID, value: InventoryStore): Boolean {
        return super.remove(key, value).also {
            if (it) {
                ownerTable.remove(value.owner.uniqueId, value.name)
            }
        }
    }

    fun add(inv: InventoryStore) {
        put(inv.uuid, inv)
    }

    fun addAll(inventories: List<InventoryStore>) {
        inventories.forEach {
            add(it)
        }
    }

    fun getAllByOwner(uuid: OwnerUUID) : List<InventoryStore> {
        return ownerTable.row(uuid).values
            .mapNotNull { get(it) }
    }

    fun getByOwnerAndName(uuid: OwnerUUID, name: String) : InventoryStore? {
        val inventoryUUID = ownerTable.get(uuid, name)
        return get(inventoryUUID)
    }

    override fun clear() {
        ownerTable.clear()
        super.clear()
    }

}