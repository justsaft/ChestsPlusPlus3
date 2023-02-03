package com.jamesdpeters.chestsplusplus.serialize.serializers

import com.googlecode.cqengine.ConcurrentIndexedCollection
import com.googlecode.cqengine.persistence.wrapping.WrappingPersistence
import com.jamesdpeters.chestsplusplus.serialize.CppSerializer
import org.springframework.stereotype.Component

@Component
class ConcurrentIndexedCollectionSerializer : CppSerializer<ConcurrentIndexedCollection<*>?, Set<*>?> {

    override fun serialize(from: ConcurrentIndexedCollection<*>?): Set<*>? {
        return from
    }

    override fun deserialize(from: Set<*>?): ConcurrentIndexedCollection<*> {
        return ConcurrentIndexedCollection(
            WrappingPersistence.aroundCollection(from ?: setOf())
        )
    }
}