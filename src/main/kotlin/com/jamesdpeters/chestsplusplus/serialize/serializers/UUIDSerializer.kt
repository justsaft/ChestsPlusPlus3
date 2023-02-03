package com.jamesdpeters.chestsplusplus.serialize.serializers

import com.jamesdpeters.chestsplusplus.serialize.CppSerializer
import org.springframework.stereotype.Component
import java.util.*

@Component
class UUIDSerializer : CppSerializer<UUID?, String?> {

    override fun serialize(from: UUID?): String? {
        return from?.toString()
    }

    override fun deserialize(from: String?): UUID? {
        return UUID.fromString(from)
    }
}