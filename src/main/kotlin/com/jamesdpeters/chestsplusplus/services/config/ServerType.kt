package com.jamesdpeters.chestsplusplus.services.config

import org.springframework.stereotype.Component

@Component
class ServerType {

    var type: Type
        private set

    var hasSpigotConfig: Boolean = false
        private set

    enum class Type {
        BUKKIT,
        SPIGOT,
        PAPER
    }

    init {
        type = Type.BUKKIT

        try {
            Class.forName("org.spigotmc.SpigotConfig")
            type = Type.SPIGOT
            hasSpigotConfig = true
        } catch (ignored: Exception) {}

        try {
            Class.forName("com.destroystokyo.paper.VersionHistoryManager\$VersionData")
            // If reached here class exists
            type = Type.PAPER
        } catch (ignored: Exception) {}
    }

}