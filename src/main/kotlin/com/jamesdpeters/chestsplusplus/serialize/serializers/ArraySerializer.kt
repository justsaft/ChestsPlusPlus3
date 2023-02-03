package com.jamesdpeters.chestsplusplus.serialize.serializers

import com.jamesdpeters.chestsplusplus.serialize.CppSerializer
import java.util.*
import kotlin.collections.ArrayList

class ArraySerializer<T>(clazz: Class<Array<T>?>) : CppSerializer<Array<T>?, ArrayList<T>?> {
    private val arrayInstance: Array<T>

    init {
        @Suppress("UNCHECKED_CAST")
        arrayInstance = java.lang.reflect.Array.newInstance(clazz.componentType(), 0) as Array<T>
    }

    override fun serialize(from: Array<T>?): ArrayList<T>? {
        return from?.toList()?.let { ArrayList(it) }
    }

    override fun deserialize(from: ArrayList<T>?): Array<T>? {
        @Suppress("UNCHECKED_CAST")
        return from?.toArray(arrayInstance)
    }
}