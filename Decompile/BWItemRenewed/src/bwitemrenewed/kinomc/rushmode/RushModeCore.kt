package bwitemrenewed.kinomc.rushmode

import bwitemrenewed.kinomc.setup
import bedwarsgun.bedwarsgun.utils.utils
import bwitemrenewed.kinomc.particlemanager.FireParticle
import io.github.bedwarsrel.game.Game
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.scheduler.BukkitRunnable
import java.lang.Exception
import java.util.HashMap

class RushModeCore {
    companion object{
        var PlayerIsEnableExpendMode = HashMap<Player,Boolean>()
        lateinit var game: Game
    }
    fun Core(event:PlayerInteractEvent){
        val player = event.player
        if(event.player.itemInHand.type == Material.WOOL){//如果手上拿的是羊毛
            if(event.action == Action.LEFT_CLICK_BLOCK || event.action == Action.LEFT_CLICK_AIR){//如果是左键 切换
                FireParticle.SwitchedModeParticle(player)//切换的粒子
                if(PlayerIsEnableExpendMode[player] == null){//如果名单里没有玩家 -> 代表玩家现在是正常模式
                    PlayerIsEnableExpendMode[player] = true//加入
                    utils().sendActionBar(player,"&a&l您已经切换为搭路模式")
                }else{
                    PlayerIsEnableExpendMode.remove(player)//如果他是急速模式 就设置为不是
                    utils().sendActionBar(player,"&c&l您已经切换为非搭路模式")
                }

            }
            if(event.action == Action.RIGHT_CLICK_BLOCK){//如果是右键 搭
                if(PlayerIsEnableExpendMode[player] != null){//如果也在搭路名单
                    if(event.clickedBlock != null) {
                        ExpendToDirection(event, event.blockFace.modX, event.blockFace.modY, event.blockFace.modZ)
                    }
                }
            }


        }
    }
    fun ExpendToDirection(event: PlayerInteractEvent, x:Int, y:Int, z:Int){
        var i = setup.ExpendBlock
        var step = 0//跳过最开始放置的方块
        val blockOrigialLocation  = event.clickedBlock.location
        val blockItemStack = event.player.itemInHand
        object : BukkitRunnable(){
            override fun run() {

                blockOrigialLocation.x += x
                blockOrigialLocation.y += y
                blockOrigialLocation.z += z
                val locnew = Location(event.player.world,blockOrigialLocation.x,blockOrigialLocation.y,blockOrigialLocation.z)
                if((event.player.world.getBlockAt(locnew).type != Material.AIR || JudgPlayerInThisLocation(blockOrigialLocation))&&step >= 1){//跳过最开始放置的方块 如果有阻拦
                    cancel()
                    return
                }else{
                    step++
                    val b:Block = event.player.world.getBlockAt(locnew)
                    b.type = blockItemStack.type
                    b.data = blockItemStack.data.data
                    event.player.world.getBlockAt(locnew).setTypeIdAndData(b.type.id,b.data,true)//放带颜色的羊毛
                    FireParticle.BlockPlaceParticle(b)
                    try {
                        game.region.addPlacedBlock(b,null)//设置玩家可破坏此方块
                    }catch (e: Exception){
                        e.printStackTrace()
                        cancel()
                        return
                    }

                }
                i--
                if(i <= 0){
                    cancel()
                    return
                }
            }
        }.runTaskTimer(setup.instance,0L,3L)
    }
    fun JudgPlayerInThisLocation(loc: Location): Boolean {
        Bukkit.getOnlinePlayers().forEach { p ->
            if(p.location.blockX == loc.blockX){
                if(p.location.blockY == loc.blockY || p.location.blockY + 1 == loc.blockY){
                    if(p.location.blockZ == loc.blockZ){
                        if(p.gameMode != GameMode.SPECTATOR)//如果不是观察者 才会起作用
                            return true
                    }
                }
            }
        }
        return false
    }
}