package com.jamesdpeters.chestsplusplus.spigot.event

import com.jamesdpeters.chestsplusplus.storage.serializable.ChestLinkLocation
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class ChestLinkLocationLoadEvent(chestLinkLocation: ChestLinkLocation) : Event() {

    companion object {
        val handlers: HandlerList = HandlerList()

        @Suppress("unused")
        @JvmStatic
        fun getHandlerList(): HandlerList = handlers
    }

    var chestLinkLocation: ChestLinkLocation = chestLinkLocation
        private set

    override fun getHandlers(): HandlerList = Companion.handlers

}