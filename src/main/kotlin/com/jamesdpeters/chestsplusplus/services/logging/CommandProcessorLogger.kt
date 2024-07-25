package com.jamesdpeters.chestsplusplus.services.logging

import com.jamesdpeters.chestsplusplus.Log
import me.gleeming.command.paramter.Processor
import org.springframework.stereotype.Service

@Service
class CommandProcessorLogger(
    processors: List<Processor<out Any>>?
) {

    init {
        Log.debug { "Loaded Command Processors: " }
        processors?.forEach {
            Log.debug { it.type.toGenericString() }
        }
    }

}