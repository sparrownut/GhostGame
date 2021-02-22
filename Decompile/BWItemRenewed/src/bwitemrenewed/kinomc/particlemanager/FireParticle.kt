package bwitemrenewed.kinomc.particlemanager

import bwitemrenewed.kinomc.setup
import net.minecraft.server.v1_8_R3.EnumParticle
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles
import org.bukkit.Bukkit
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class FireParticle {
    companion object {
        fun SwitchedModeParticle(p: Player) {
            object : BukkitRunnable() {
                override fun run() {
                    val loc = p.location
                    val packet = PacketPlayOutWorldParticles(
                        EnumParticle.REDSTONE, true,
                        loc.x.toFloat(), p.eyeLocation.y.toFloat(), loc.z.toFloat(), 1F, 0F, 0F, 1F, 1
                    )//设置开火的粒子包
                    for(i in 0..10) {
                        for (online in Bukkit.getOnlinePlayers()) {
                            (online as CraftPlayer).handle.playerConnection.sendPacket(packet)
                        }//send
                    }
                    cancel()
                    return
                }
            }.runTaskTimerAsynchronously(setup.instance, 0, 0)

        }
        fun FireParticle(p: Player) {
            object : BukkitRunnable() {
                override fun run() {
                    val loc = p.location
                    val packet = PacketPlayOutWorldParticles(
                        EnumParticle.REDSTONE, true,
                        loc.x.toFloat(), p.eyeLocation.y.toFloat(), loc.z.toFloat(), 1F, 0F, 0F, 1F, 1
                    )//设置开火的粒子包
                    for(i in 0..10) {
                        for (online in Bukkit.getOnlinePlayers()) {
                            (online as CraftPlayer).handle.playerConnection.sendPacket(packet)
                        }//send
                    }
                    cancel()
                    return
                }
            }.runTaskTimerAsynchronously(setup.instance, 0, 0)

        }
        fun BlockPlaceParticle(b: Block) {
            object : BukkitRunnable() {
                override fun run() {
                    val loc = b.location
                    val packet = PacketPlayOutWorldParticles(
                        EnumParticle.REDSTONE, true,
                        loc.x.toFloat(), b.y.toFloat(), loc.z.toFloat(), 1F, 0F, 0F, 1F, 2
                    )//设置开火的粒子包
                    for(i in 0..3) {
                        for (online in Bukkit.getOnlinePlayers()) {
                            (online as CraftPlayer).handle.playerConnection.sendPacket(packet)
                        }//send
                    }
                    cancel()
                    return
                }
            }.runTaskTimerAsynchronously(setup.instance, 0, 0)

        }
    }

}