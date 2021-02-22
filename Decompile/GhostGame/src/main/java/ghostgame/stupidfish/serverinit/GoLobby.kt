package ghostgame.stupidfish.serverinit

import ghostgame.stupidfish.setup
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import com.google.common.io.ByteStreams

import com.google.common.io.ByteArrayDataOutput
import ghostgame.stupidfish.utils.utils
import sun.audio.AudioPlayer
import java.lang.Exception
import sun.audio.AudioPlayer.player





class GoLobby(p: Player) {
    init {
        Bukkit.getScheduler().runTaskLater(setup.instance,{
            //p.performCommand("server ${setup.lobbyname}")
            sendToProxy(p,setup.lobbyname)
        },10L)

    }
    fun sendToProxy(player: Player, server: String?) {
        try {
            val out = ByteStreams.newDataOutput()
            out.writeUTF("Connect")
            out.writeUTF(server)
            player.sendPluginMessage(setup.instance, "BungeeCord", out.toByteArray())
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}