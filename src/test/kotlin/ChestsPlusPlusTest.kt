import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import be.seeseemelk.mockbukkit.WorldMock
import be.seeseemelk.mockbukkit.entity.PlayerMock
import com.jamesdpeters.chestsplusplus.ChestsPlusPlus
import com.jamesdpeters.chestsplusplus.services.data.inventory.InventoryStorageService
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class ChestsPlusPlusTest {

    companion object {
        private lateinit var chestsPlusPlus: ChestsPlusPlus
        private lateinit var server: ServerMock
        private lateinit var mainWorld: WorldMock
        private lateinit var player: PlayerMock
        private lateinit var origin: Location

        private lateinit var inventoryStorageService: InventoryStorageService

        @JvmStatic
        @BeforeAll
        fun load() {
            server = MockBukkit.mock()
            mainWorld = server.addSimpleWorld("Main World")
            chestsPlusPlus = MockBukkit.load(ChestsPlusPlus::class.java)
            player = server.addPlayer()
            origin = Location(mainWorld, 0.0, 0.0, 0.0)

            inventoryStorageService = ChestsPlusPlus.getBean(InventoryStorageService::class.java)!!
        }

        @JvmStatic
        @AfterAll
        fun unload() {
            MockBukkit.unmock()
        }
    }

    private fun getChestsPlusPlusItemTag() : ItemStack {
        val item = ItemStack(Material.NAME_TAG)

        item.itemMeta?.let {
            it.displayName(Component.text("Chests Plus Plus"))
            item.itemMeta = it
        }

        return item
    }

    @Test
    fun `Test ChestLink can be added via NameTag and then broken`() {
        player.simulateBlockPlace(Material.CHEST, origin)
        player.inventory.setItemInMainHand(getChestsPlusPlusItemTag())
        player.simulateUseItemOn(origin, BlockFace.UP, EquipmentSlot.HAND)

        val inventoryStore = inventoryStorageService.inventoryStoreAtLocation(origin)
        assertEquals(inventoryStore?.owner, player)
        assertEquals(inventoryStore?.name, "Chests Plus Plus")

        player.simulateBlockBreak(origin.block)
        val shouldBeNullInventoryStore = inventoryStorageService.inventoryStoreAtLocation(origin)
        assertNull(shouldBeNullInventoryStore)
    }

}