package ghostgame.stupidfish.gametreeevent

import ghostgame.stupidfish.setup
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

object LastGaspGoalEvent {
    fun DeathKillMode() {
        Bukkit.getOnlinePlayers().forEach { player: Player ->
            player.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 999999, 255))
            object : BukkitRunnable() {
                var i = 1000
                override fun run() {
                    player.world.time = i.toLong()
                    if (i >= 13000) {
                        cancel()
                        return
                    }
                    i += 1000
                }
            }.runTaskTimer(setup.instance, 0L, 10L)
        }
    }
}