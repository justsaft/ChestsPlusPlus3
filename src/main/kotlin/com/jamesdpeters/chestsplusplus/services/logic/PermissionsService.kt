package com.jamesdpeters.chestsplusplus.services.logic

import org.bukkit.Server
import org.bukkit.entity.Player
import org.springframework.stereotype.Service

@Service
class PermissionsService(
    val server: Server
) {
    private val openPermission = server.pluginManager.getPermission("chestlink.open")
    private val createPermission = server.pluginManager.getPermission("chestlink.create")

    fun hasChestLinkCreatePermission(player: Player): Boolean {
        return createPermission?.let { player.hasPermission(it) } ?: true
    }

    fun hasChestLinkOpenPermission(player: Player): Boolean {
        return openPermission?.let { player.hasPermission(it) } ?: true
    }

}