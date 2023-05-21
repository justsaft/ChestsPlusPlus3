package com.jamesdpeters.chestsplusplus

import com.jamesdpeters.chestsplusplus.services.config.ConfigOptions

object Log {

    fun info(string: () -> String) {
        println("[ChestsPlusPlus] ${string()}")
    }

    fun debug(string: () -> String) {
        if (ConfigOptions.instance.isDebug())
            println("[ChestsPlusPlus] [DEBUG] ${string()}")
    }

}