package com.jamesdpeters.chestsplusplus.storage.serializable

import com.jamesdpeters.chestsplusplus.findSimilarTag
import com.jamesdpeters.chestsplusplus.isSimilarTag
import com.jamesdpeters.chestsplusplus.serialize.ConfigSerialize
import com.jamesdpeters.chestsplusplus.serialize.serializers.ItemFrameSerializer
import org.bukkit.Location
import org.bukkit.Rotation
import org.bukkit.entity.ItemFrame
import org.bukkit.inventory.ItemStack

class HopperFilterLocation : SerializableObject {

    @ConfigSerialize
    var location: Location? = null
        private set

    @ConfigSerialize
    var filters: MutableList<HopperFilter>? = null
        private set

    @Suppress("unused")
    constructor(map: MutableMap<String?, Any?>) : super(map) {
        init()
    }

    constructor(location: Location) {
        this.location = location
        init()
    }

    private fun init() {
        if (filters == null) filters = mutableListOf()
        filters?.removeIf { it.itemFrame == null || it.itemFrame!!.isDead }
    }

    fun isInFilter(itemStack: ItemStack): Boolean {
        if (filters == null) return true
        if (filters!!.size == 0) return true

        val (accept, reject) = filters!!.partition { it.isAcceptFilter() }

        val hasAcceptFilter = accept.any { it.getPredicate(itemStack) == HopperFilter.Type.ACCEPT }
        val isRejected = reject.any { it.getPredicate(itemStack) == HopperFilter.Type.REJECT }

        return if (accept.isNotEmpty()) hasAcceptFilter && !isRejected
        else !isRejected
    }

}

class HopperFilter : SerializableObject {

    enum class Type {
        ACCEPT,
        REJECT,
        NONE
    }

    @ConfigSerialize(ItemFrameSerializer::class)
    var itemFrame: ItemFrame? = null
        private set


    @Suppress("unused")
    constructor(map: MutableMap<String?, Any?>) : super(map) {

    }

    constructor(itemFrame: ItemFrame) {
        this.itemFrame = itemFrame
    }

    fun filterByItemMeta() : Boolean {
        return itemFrame != null && (itemFrame!!.rotation == Rotation.FLIPPED || itemFrame!!.rotation == Rotation.COUNTER_CLOCKWISE)
    }

    fun dontAllowThisItem() : Boolean {
        return itemFrame != null && (itemFrame!!.rotation == Rotation.CLOCKWISE || itemFrame!!.rotation == Rotation.COUNTER_CLOCKWISE)
    }

    fun ignoreFilter() : Boolean {
        return itemFrame != null && (
                        itemFrame!!.rotation == Rotation.COUNTER_CLOCKWISE_45
                        || itemFrame!!.rotation == Rotation.CLOCKWISE_135
                        || itemFrame!!.rotation == Rotation.CLOCKWISE_45
                        || itemFrame!!.rotation == Rotation.FLIPPED_45
                )
    }

    fun isAcceptFilter(): Boolean = !dontAllowThisItem()

    fun getPredicate(itemStack: ItemStack): Type {
        if (itemFrame!!.item.type.isAir || ignoreFilter())
            return Type.ACCEPT

        val dontAllowThisItem = dontAllowThisItem()
        val filterByItemMeta = filterByItemMeta()

        if (dontAllowThisItem && !filterByItemMeta) {
            return if (itemFrame!!.item.isSimilar(itemStack)) Type.REJECT
            else Type.ACCEPT
        }
        else if (dontAllowThisItem) {
            return if (isFilteredByMeta(itemStack)) Type.REJECT
            else Type.ACCEPT
        }

        if (filterByItemMeta) {
            if (isFilteredByMeta(itemStack)) return Type.ACCEPT
        }
        else {
            if (itemFrame!!.item.isSimilar(itemStack)) return Type.ACCEPT
        }
        return Type.NONE
    }

    private fun isFilteredByMeta(itemStack: ItemStack): Boolean {
        if (itemFrame!!.item.isSimilar(itemStack)) return true
        if (filterByItemMeta()) {
            return if (itemFrame!!.item.type == itemStack.type) true
            else {
                val similarTag = itemFrame!!.item.findSimilarTag(itemStack)
                return similarTag != null
            }
        }
        return false
    }

//    fun getPredicate(itemStack: ItemStack): Type {
//        if (itemFrame!!.item.isSimilar(cachedItemStack) && cachedRotation == itemFrame!!.rotation) {
//            val cache = cachePredicates[itemStack.hashCode()]
//            return if (cache != null)
//                cache
//            else {
//                val predicate = getType(itemStack)
//                cachePredicates[itemStack.hashCode()] = predicate
//                predicate
//            }
//        } else {
//            cachePredicates.clear()
//            cachedItemStack = itemFrame!!.item.clone()
//            cachedRotation = itemFrame!!.rotation
//            val predicate = getType(itemStack)
//            cachePredicates[itemStack.hashCode()] = predicate
//            return predicate
//        }
//    }


}