package com.jamesdpeters.chestsplusplus.spigot.event

import com.jamesdpeters.chestsplusplus.storage.serializable.LocationInfo
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class LocationInfoLoadEvent : Event {

    companion object {
        val handlers: HandlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = handlers
    }

    var locationInfo: LocationInfo
        private set

    constructor(locationInfo: LocationInfo) {
        this.locationInfo = locationInfo
    }

    override fun getHandlers(): HandlerList = Companion.handlers

}