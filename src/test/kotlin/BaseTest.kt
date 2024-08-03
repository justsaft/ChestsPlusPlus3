import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import be.seeseemelk.mockbukkit.WorldMock
import be.seeseemelk.mockbukkit.entity.PlayerMock
import com.jamesdpeters.chestsplusplus.ChestsPlusPlus
import com.jamesdpeters.chestsplusplus.services.data.inventory.InventoryStorageService
import org.bukkit.Location
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll

open class BaseTest {

    companion object {
        private lateinit var chestsPlusPlus: ChestsPlusPlus
        lateinit var server: ServerMock

        @JvmStatic
        @BeforeAll
        fun load() {
            server = MockBukkit.mock()
            chestsPlusPlus = MockBukkit.load(ChestsPlusPlus::class.java)
        }

        @JvmStatic
        @AfterAll
        fun unload() {
            MockBukkit.unmock()
        }
    }

}