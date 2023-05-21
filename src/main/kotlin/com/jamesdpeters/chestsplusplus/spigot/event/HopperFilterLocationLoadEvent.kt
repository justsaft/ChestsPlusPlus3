package com.jamesdpeters.chestsplusplus.spigot.event

import com.jamesdpeters.chestsplusplus.storage.serializable.HopperFilterLocation
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class HopperFilterLocationLoadEvent(hopperFilterLocation: HopperFilterLocation) : Event() {

    companion object {
        val handlers: HandlerList = HandlerList()

        @Suppress("unused")
        @JvmStatic
        fun getHandlerList(): HandlerList = handlers
    }

    var hopperFilterLocation: HopperFilterLocation = hopperFilterLocation
        private set

    override fun getHandlers(): HandlerList = Companion.handlers

}