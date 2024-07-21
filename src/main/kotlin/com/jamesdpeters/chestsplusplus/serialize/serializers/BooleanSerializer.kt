package com.jamesdpeters.chestsplusplus.serialize.serializers

import com.jamesdpeters.chestsplusplus.serialize.CppSerializer
import org.springframework.stereotype.Component

@Component
class BooleanSerializer : CppSerializer<Boolean?, String> {

    override fun serialize(from: Boolean?): String {
        return from.toString()
    }

    override fun deserialize(from: String): Boolean {
        return from.toBoolean()
    }
}