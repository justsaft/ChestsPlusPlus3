package com.jamesdpeters.chestsplusplus.serialize.serializers.abstracts

import com.jamesdpeters.chestsplusplus.serialize.CppSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.cast

abstract class EntityListSerializer<T : Entity>(private val clazz: KClass<T>) : CppSerializer<MutableList<T?>?, MutableList<String?>?> {

    override fun serialize(from: MutableList<T?>?): MutableList<String?>? {
        return from?.map { it?.uniqueId.toString() }?.toMutableList()
    }

    override fun deserialize(from: MutableList<String?>?): MutableList<T?>? {
        return from?.mapNotNull {
            if (it == null)
                return@mapNotNull null

            val uuid = UUID.fromString(it)
            val entity = Bukkit.getEntity(uuid)
            if (clazz.isInstance(entity))
                clazz.cast(entity)
            else null
        }?.toMutableList()
    }
}

