package bedwarsgun.bedwarsgun.utils

import net.minecraft.server.v1_8_R3.IChatBaseComponent
import net.minecraft.server.v1_8_R3.PacketPlayOutChat
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player

class utils {
    var prefix: String = MessageReplace("&b&lKino&6Mc")
    fun MessageReplace(string: String): String {
        return string.replace("&", "§")
    }

    fun MessageSendRe(message: String, player: Player) {
        player.sendMessage(MessageReplace(message))
    }
    fun sendActionBar(p: Player, message: String) {
        val packet = PacketPlayOutChat(
            IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message.replace("&", "§") + "\"}"),
            2.toByte()
        )
        (p as CraftPlayer).handle.playerConnection.sendPacket(packet)
    }
    fun BukkitSendMessageRe(message: String){
        Bukkit.getLogger().info(message.replace("&","§"))
    }
}