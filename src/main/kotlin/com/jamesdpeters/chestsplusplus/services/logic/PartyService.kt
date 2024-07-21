package com.jamesdpeters.chestsplusplus.services.logic

import com.alessiodp.parties.api.Parties
import com.alessiodp.parties.api.interfaces.PartiesAPI
import com.jamesdpeters.chestsplusplus.ChestsPlusPlus
import com.jamesdpeters.chestsplusplus.util.annotation.PartiesAPIAvailable
import org.springframework.context.annotation.Conditional
import org.springframework.stereotype.Service

@Service
@Conditional(PartiesAPIAvailable::class)
class PartyService(
    chestsPlusPlus: ChestsPlusPlus
) {

    var partiesAPI: PartiesAPI? = null
        private set

    init {
        chestsPlusPlus.server.pluginManager.getPlugin("Parties")?.let {
            partiesAPI = Parties.getApi()
        }
    }

}