import com.inc.pmu.models.Player
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
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
        val bytes: ByteArray = player.toBytes()
        val strPlayer: String = bytes.toString(Charsets.UTF_8);
        println(strPlayer)
        val json: JSONObject = JSONObject(strPlayer)
        Assert.assertEquals(json.get("puuid"), uuid)
        Assert.assertEquals(json.get("name"), "Bob")
    }
}
