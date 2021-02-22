package ghostgame.stupidfish.gamerule

import ghostgame.stupidfish.particlemanager.ParticleManager
import ghostgame.stupidfish.setup
import ghostgame.stupidfish.teammanager.Team
import net.minecraft.server.v1_8_R3.EnumParticle
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.*
import org.bukkit.event.player.PlayerItemDamageEvent

class BasicGameRuleManager : Listener {
    @EventHandler
    fun onItemDamage(event: PlayerItemDamageEvent) {
        event.isCancelled = true
    } //防止物品损坏

    @EventHandler
    fun onChangeItemLoc(e: InventoryDragEvent) {
        e.isCancelled = true
    }

    @EventHandler
    fun onChangeItemLocMove(e: InventoryCreativeEvent) {
        if (setup.is_Game_Start_Timed) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onChangeItemLocMoveT(e: InventoryMoveItemEvent) {
        e.isCancelled = true
    }

    @EventHandler
    fun onChangeItemLocT(e: InventoryPickupItemEvent) {
        e.isCancelled = true
    } //物品交换方位

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        try {
            if (event.entity.type == EntityType.PLAYER) {
                if (setup.is_Game_Start_Timed) {
                    if (event.cause == EntityDamageEvent.DamageCause.FALL || event.cause == EntityDamageEvent.DamageCause.DROWNING) {
                        event.isCancelled = true
                    }
                    if (Team.JudgPlayerInList(
                            event.entity as Player,
                            Team.GhostTeam
                        ) && Team.JudgPlayerInList((event.entity as Player).killer as Player, Team.GhostTeam)
                    ) {
                        event.isCancelled = true
                    }
                } else {
                    if (event.entity.type == EntityType.PLAYER) {
                        val p = event.entity as Player
                        ParticleManager.spawnParticle(p, EnumParticle.REDSTONE, 3, 1f, 0f, 1f, 1f)
                        event.isCancelled = true
                    }
                }
            }
        } catch (e: NullPointerException) {
        }
    } //伤害过滤器

    @EventHandler
    fun onChestDo(event: InventoryOpenEvent) {
        event.isCancelled = true
    }
}