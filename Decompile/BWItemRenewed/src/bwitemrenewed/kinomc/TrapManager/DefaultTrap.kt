package bwitemrenewed.kinomc.TrapManager

import bedwarsgun.bedwarsgun.utils.utils
import bwitemrenewed.kinomc.setup
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerMoveEvent

class DefaultTrap {
    companion object{
        lateinit var TrapMap:Map<Location,Player>

        fun DefaultTrapMove(event:PlayerMoveEvent){

            val loc = Location(event.player.world,event.player.location.x,event.player.location.y+0.1,event.player.location.z)
            if(event.player.world.getBlockAt(loc).type  == Material.CARPET && event.player.gameMode != GameMode.SPECTATOR) {/* 如果脚下是陷阱 */
                TrapActiveSound(event.player)
                event.player.world.getBlockAt(loc).type = Material.AIR
                utils().MessageSendRe("&4您已触发地雷 还有0.5秒爆炸 快跑！",event.player)
                Bukkit.getScheduler().runTaskLater(setup.instance, {
                    event.player.world.createExplosion(loc.x,loc.y,loc.z,2F,false,false)
                }, 10L)
            }
        }
        fun DefaultTrapPut(event:BlockPlaceEvent){
            if(event.player.itemInHand.type == Material.CARPET) {
                utils().MessageSendRe("&c您已布置陷阱",event.player)
                event.player.world.playSound(event.player.location, Sound.GLASS, 5F, 1F)
            }
        }
        fun TrapActiveSound(p: Player){
            p.world.playSound(p.location, Sound.FALL_BIG,1F,1F)
        }
    }
}