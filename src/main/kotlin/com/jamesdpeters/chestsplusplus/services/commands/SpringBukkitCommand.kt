package com.jamesdpeters.chestsplusplus.services.commands

import me.gleeming.command.CommandHandler
import javax.annotation.PostConstruct

abstract class SpringBukkitCommand {

    @PostConstruct
    fun init() {
        if(isEnabled) {
            CommandHandler.registerCommands(this)
            onEnable()
        }
    }

    open val isEnabled: Boolean = true
    open fun onEnable() {}

}