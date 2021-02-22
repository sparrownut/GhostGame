package ghostgame.stupidfish.particlemanager

import ghostgame.stupidfish.setup
import net.minecraft.server.v1_8_R3.EnumParticle
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

object ParticleManager {
    fun spawnParticle(
        player: Player,
        Particle: EnumParticle?,
        ParticleNum: Int,
        delay: Float,
        r: Float,
        g: Float,
        b: Float
    ) {
        object : BukkitRunnable() {
            var times = ParticleNum
            override fun run() {
                times--
                val loc = player.location
                val packet = PacketPlayOutWorldParticles(
                    Particle, true, loc.x
                        .toFloat(), loc.y.toFloat(), loc.z.toFloat(), r - 1, g, b, 1F, 0
                )
                for (online in Bukkit.getOnlinePlayers()) {
                    (online as CraftPlayer).handle.playerConnection.sendPacket(packet)
                }
                if (times <= 0) {
                    cancel()
                    return
                }
            }
        }.runTaskTimerAsynchronously(setup.instance, 0, delay.toLong())
    }

    fun spawnArrowParticle(
        loc: Location,
        Particle: EnumParticle?,
        ParticleNum: Int,
        delay: Float,
        r: Float,
        g: Float,
        b: Float
    ) {
        object : BukkitRunnable() {
            var times = ParticleNum
            override fun run() {
                times--
                var z = 0
                while (z < 255) {
                    val packet = PacketPlayOutWorldParticles(
                        Particle, true, loc.x
                            .toFloat(), loc.y.toFloat() + z, loc.z.toFloat(), r - 1, g, b, 1F, 0
                    )
                    for (online in Bukkit.getOnlinePlayers()) {
                        (online as CraftPlayer).handle.playerConnection.sendPacket(packet)
                    }
                    z += 1
                }
                if (times <= 0) {
                    cancel()
                    return
                }
            }
        }.runTaskTimerAsynchronously(setup.instance, 0, delay.toLong())
    }
}