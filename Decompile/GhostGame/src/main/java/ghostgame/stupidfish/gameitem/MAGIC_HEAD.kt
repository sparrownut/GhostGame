package ghostgame.stupidfish.gameitem

import ghostgame.stupidfish.setup
import ghostgame.stupidfish.teammanager.Team
import ghostgame.stupidfish.threadmanager.ThreadManager.runtasklater
import ghostgame.stupidfish.utils.utils
import net.minecraft.server.v1_8_R3.NBTTagCompound
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import java.util.function.Consumer

class MAGIC_HEAD {
    fun disableAI(entity: Entity) {
        val nmsEnt = (entity as CraftEntity).handle as Entity
        var tag = (nmsEnt as net.minecraft.server.v1_8_R3.Entity).nbtTag
        if (tag == null) {
            tag = NBTTagCompound()
        }
        (nmsEnt as net.minecraft.server.v1_8_R3.Entity).c(tag)
        tag.setInt("NoAI", 1)
        (nmsEnt as net.minecraft.server.v1_8_R3.Entity).f(tag)
    }

    companion object {
        var MAGIC_HEAD_CoolDown: MutableMap<Player, Int> = HashMap()
        fun Ghost_Magic_Head_Click(event: PlayerInteractEvent) {
            val p = event.player
            if (!MAGIC_HEAD_CoolDown.containsKey(p)) {
                run(event, p)
            } else {
                utils.SendMessageRe(p, "&c道具正在冷却 请等待" + MAGIC_HEAD_CoolDown[p] + "秒")
            }
        }

        fun run(event: PlayerInteractEvent, p: Player) {
            if (Team.JudgPlayerInList(p, Team.GhostTeam)) {
                MAGIC_HEAD_CoolDown[p] = 250
                object : BukkitRunnable() {
                    var timer = 250
                    override fun run() {
                        MAGIC_HEAD_CoolDown[p] = timer
                        if (!setup.is_Game_Start_Timed) {
                            cancel()
                            return
                        }
                        if (timer <= 0) {
                            MAGIC_HEAD_CoolDown.remove(p)
                            cancel()
                            return
                        }
                        timer--
                    }
                }.runTaskTimer(setup.instance, 0L, 20L) //冷却
                utils.SendAllMessage("&6" + event.player.name + "&c发动了僵尸群!")
                for (i in 0..9) { //生成10个小僵尸
                    val zombie = p.world.spawnEntity(p.location, EntityType.ZOMBIE)
                    val mob = zombie as Monster
                    mob.removeWhenFarAway = true
                    mob.canPickupItems = false
                    mob.maxHealth = 100.0
                    mob.health = 100.0
                    mob.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 999999, 5), false)
                    mob.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 4), false)
                    mob.customName = utils.StringReplace("&c幽灵的盟友")
                    if (Team.HunterTeam.size != 0) {
                        Team.HunterTeam.forEach(Consumer { hunter: Player? -> mob.target = hunter })
                    }
                    runtasklater(20L * 10) { zombie.remove() }
                }
            }
        }
    }
}