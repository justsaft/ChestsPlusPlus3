package com.jamesdpeters.chestsplusplus.serialize.serializers

import com.jamesdpeters.chestsplusplus.serialize.CppSerializer
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.springframework.stereotype.Component
import java.util.*

@Component
class OfflinePlayerUUIDSerializer : CppSerializer<OfflinePlayer?, String?> {

    override fun serialize(from: OfflinePlayer?): String {
        return from?.uniqueId.toString()
    }

    override fun deserialize(from: String?): OfflinePlayer? {
        return from?.let { Bukkit.getOfflinePlayer(UUID.fromString(it)) }
    }
}