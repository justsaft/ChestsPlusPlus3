package com.jamesdpeters.chestsplusplus.serialize

interface CppSerializer<A, B> {
    fun serialize(from: A): B
    fun deserialize(from: B): A
}