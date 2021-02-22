package ghostgame.stupidfish.gameitem

import ghostgame.stupidfish.particlemanager.ParticleManager
import ghostgame.stupidfish.teammanager.Team
import ghostgame.stupidfish.utils.utils
import net.minecraft.server.v1_8_R3.EnumParticle
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityShootBowEvent

object BOW_ITEM {
    fun Ghost_Bow_Shoot_Event(event: EntityShootBowEvent) {
        try {
            val a = event.projectile as Arrow
            val killer = a.shooter as Player
            if (killer.type == EntityType.PLAYER) {
                if (Team.JudgPlayerInList(killer, Team.GhostTeam)) { //如果幽灵射了 JudgPlayerInList(killer, GhostList)
                    ParticleManager.spawnParticle(killer, EnumParticle.REDSTONE, 10, 1f, 1f, 1f, 0f)
                    ParticleManager.spawnParticle(killer, EnumParticle.REDSTONE, 10, 1f, 0f, 1f, 1f)
                } else {
                    utils.SendMessageRe(killer, "这是幽灵专属的武器 你没有力量使用它")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}