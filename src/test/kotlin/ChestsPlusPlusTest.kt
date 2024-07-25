import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import be.seeseemelk.mockbukkit.WorldMock
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
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class ChestsPlusPlusTest {

    companion object {
        private lateinit var chestsPlusPlus: ChestsPlusPlus
        private lateinit var server: ServerMock
        private lateinit var mainWorld: WorldMock

        @JvmStatic
        @BeforeAll
        fun load() {
            server = MockBukkit.mock()
            mainWorld = server.addSimpleWorld("Main World")
            chestsPlusPlus = MockBukkit.load(ChestsPlusPlus::class.java)
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
    fun `Test ChestLink can be added via NameTag`() {
        val inventoryStorageService = ChestsPlusPlus.getBean(InventoryStorageService::class.java)!!
        assertNotNull(inventoryStorageService)

        val player = server.addPlayer()
        val location = Location(mainWorld, 0.0, 0.0, 0.0)
        player.simulateBlockPlace(Material.CHEST, location)

        player.inventory.setItemInMainHand(getChestsPlusPlusItemTag())
        player.simulateUseItemOn(location, BlockFace.UP, EquipmentSlot.HAND)

        val inventoryStore = inventoryStorageService.inventoryStoreAtLocation(location)
        assertNotNull(inventoryStore)
        assertEquals(inventoryStore?.owner, player)
        assertEquals(inventoryStore?.name, "Chests Plus Plus")
    }

}