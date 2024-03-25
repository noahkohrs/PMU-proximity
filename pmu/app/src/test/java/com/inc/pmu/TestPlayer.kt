import com.inc.pmu.models.Player
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [24], manifest = Config.NONE)
class TestPlayer {
    @Test
    fun playerDataIsCorrectlyBridged() {
        val uuid: String = UUID.randomUUID().toString()
        val player: Player = Player(uuid, "Bob")
        val json: JSONObject = player.toJson()
        val playerRemade: Player = Player.fromJson(json)

        assertEquals(player, playerRemade) // Only check for the PUUID, so not sufficient
        assertEquals(player.playerName, playerRemade.playerName)
        assertEquals(player.bet, playerRemade.bet)
    }
}
