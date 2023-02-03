package com.jamesdpeters.chestsplusplus.serialize.serializers

import com.jamesdpeters.chestsplusplus.serialize.CppSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.ItemFrame
import org.springframework.stereotype.Component
import java.util.*

@Component
class ItemFrameSerializer : CppSerializer<ItemFrame?, String?> {

    override fun serialize(from: ItemFrame?): String? {
        return from?.uniqueId?.toString()
    }

    override fun deserialize(from: String?): ItemFrame? {
        if (from != null) {
            val uuid = UUID.fromString(from)
            val entity = Bukkit.getEntity(uuid)
            return entity as? ItemFrame
        }
        return null
    }
}