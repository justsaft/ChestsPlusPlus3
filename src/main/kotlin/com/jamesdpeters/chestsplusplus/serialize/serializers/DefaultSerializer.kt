package com.jamesdpeters.chestsplusplus.serialize.serializers

import com.jamesdpeters.chestsplusplus.serialize.CppSerializer

class DefaultSerializer : CppSerializer<Any?, Any?> {
    override fun serialize(from: Any?): Any? = from
    override fun deserialize(from: Any?): Any? = from
}