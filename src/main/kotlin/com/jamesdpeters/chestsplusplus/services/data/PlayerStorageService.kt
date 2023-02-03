package com.jamesdpeters.chestsplusplus.services.data

import com.googlecode.cqengine.ConcurrentIndexedCollection
import com.googlecode.cqengine.index.unique.UniqueIndex
import com.googlecode.cqengine.query.QueryFactory.equal
import com.jamesdpeters.chestsplusplus.ChestsPlusPlus
import com.jamesdpeters.chestsplusplus.storage.indexes.PlayerStoreAttributes
import com.jamesdpeters.chestsplusplus.storage.serializable.InventoryStore
import com.jamesdpeters.chestsplusplus.storage.serializable.PlayerStore
import com.jamesdpeters.chestsplusplus.storage.yaml.YamlFileStorage
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.springframework.stereotype.Service
import java.util.*

@Service
class PlayerStorageService(
    private val chunkStorageService: ChunkStorageService
) : PersistableService, YamlFileStorage<PlayerStore>() {

    private val playerStores = ConcurrentIndexedCollection<PlayerStore>()

    private val log = ChestsPlusPlus.logger()

    init {
        playerStores.addIndex(UniqueIndex.onAttribute(PlayerStoreAttributes.player))
    }

    /** Overridden functions */

    override fun persist() {
        playerStores.forEach {
            save(it)
        }
    }

    override fun reload() {
        playerStores.clear()
        Bukkit.getOnlinePlayers().forEach {
            load(it)
        }
    }

    override val directory: String
        get() = "players"

    override fun filename(obj: PlayerStore): String {
        return filename(obj.player.uniqueId)
    }

    /** Private functions */

    private fun filename(playerUUID: UUID): String {
        return "$playerUUID.yml"
    }

    private fun load(player: OfflinePlayer): PlayerStore? {
        return load(player.uniqueId)
    }

    private fun load(playerUUID: UUID): PlayerStore? {
        return load<PlayerStore>(filename(playerUUID))?.also {
            playerStores.add(it)
        }
    }

    /** Public functions */

    fun playerStore(player: OfflinePlayer): PlayerStore {
        playerStores.retrieve(equal(PlayerStoreAttributes.player, player.uniqueId)).use { query ->
            return if (query.isNotEmpty) query.uniqueResult()
            else {
                return load(player) ?: PlayerStore(player).also { playerStores.add(it) }
            }
        }
    }

    fun savePlayer(player: OfflinePlayer) {
        save(playerStore(player))
        log.info { "Saved player store: $player" }
    }

    fun playerStoreByInventoryUUID(uuid: UUID): PlayerStore? {
        return playerStores.retrieve(equal(PlayerStoreAttributes.inventoryUUID, uuid)).let {
            if (it.isNotEmpty) it.uniqueResult() else null
        }
    }

    fun inventoryStoreAtLocation(location: Location): InventoryStore? {
        return chunkStorageService.getInventoryUUID(location)?.let {
            playerStoreByInventoryUUID(it)?.inventoryStore(it)
        }
    }

}