import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.WorldMock
import com.jamesdpeters.chestsplusplus.ChestsPlusPlus
import com.jamesdpeters.chestsplusplus.services.data.chunk.ChunkStorageService
import com.jamesdpeters.chestsplusplus.spigot.event.ChestLinkLocationLoadEvent
import com.jamesdpeters.chestsplusplus.spigot.event.HopperFilterLocationLoadEvent
import org.bukkit.Location
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ChunkStorageServiceTest : BaseTest() {

    private var world: WorldMock? = null
    private lateinit var storageService: ChunkStorageService

    @BeforeEach
    fun init() {
        storageService = ChestsPlusPlus.getBean(ChunkStorageService::class.java)!!
        storageService.clear()
        server.removeWorld(world)
        world = server.addSimpleWorld("world")
    }

    @Test
    fun `ChestLink event`() {
        val location = Location(world, 0.0, 0.0, 0.0)
        val uuid = UUID.randomUUID()
        storageService.createChestLinkLocation(location, uuid)
        server.pluginManager.assertEventFired(ChestLinkLocationLoadEvent::class.java)
    }

    @Test
    fun `HopperFilter event`() {
        val location = Location(world, 0.0, 0.0, 0.0)
        storageService.getHopperFilterLocation(location)
        server.pluginManager.assertEventFired(HopperFilterLocationLoadEvent::class.java)
    }

    @Test
    fun `Test ChestLink persistence`() {
        val location = Location(world, 0.0, 0.0, 0.0)
        val uuid = UUID.randomUUID()

        // Create and reload
        storageService.createChestLinkLocation(location, uuid)
        storageService.persist()
        storageService.reload()

        assertEquals(uuid, storageService.getChestLinkLocation(location)?.inventoryUUID)

        storageService.removeChestLinkLocation(location)
        storageService.persist()
        storageService.reload()

        assertNull(storageService.getChestLinkLocation(location))
    }

}