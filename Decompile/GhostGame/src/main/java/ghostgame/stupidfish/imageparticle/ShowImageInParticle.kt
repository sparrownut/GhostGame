package ghostgame.stupidfish.imageparticle

import ghostgame.stupidfish.setup
import net.minecraft.server.v1_8_R3.EnumParticle
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.awt.image.BufferedImage

object ShowImageInParticle {
    fun spawnParticleDirect(
        player: Player,
        time: Int,
        height: Float,
        image: BufferedImage?,
        smooth_value: Int,
        PX: Int,
        PY: Int
    ) {
        object : BukkitRunnable() {
            var times = time
            var l = player.location
            override fun run() {
                var y = 0
                while (y < image!!.height) {
                    //像素迭代
                    var x = 0
                    while (x < image.width) {
                        val pixel = image.getRGB(x, y)
                        val R = pixel and 0xff0000 shr 16
                        val G = pixel and 0xff00 shr 8
                        val B = pixel and 0xff //translte to RGB
                        val X = x.toFloat() / (image.width / 12.toFloat())
                        val Y = y.toFloat() / (image.width / 12.toFloat())
                        try {
                            if (!(R == 255 && G == 255 && B == 255)) { //去除白色
                                val packet = PacketPlayOutWorldParticles(
                                    EnumParticle.REDSTONE,
                                    true,
                                    l.x
                                        .toFloat(),
                                    l.y.toFloat() + height - Y,
                                    l.z.toFloat() + X,
                                    (R / 255).toFloat() - 1,
                                    G.toFloat() / 255,
                                    B.toFloat() / 255,
                                    1.toFloat(),
                                    0
                                )
                                for (online in Bukkit.getOnlinePlayers()) {
                                    (online as CraftPlayer).handle.playerConnection.sendPacket(packet)
                                }
                            }
                        } catch (n: NullPointerException) {
                        }
                        if (times <= 0) {
                            cancel()
                            return
                        }
                        x += smooth_value
                    }
                    y += smooth_value
                }
                times--
            }
        }.runTaskTimerAsynchronously(setup.instance, 0L, 2L)
    }
}