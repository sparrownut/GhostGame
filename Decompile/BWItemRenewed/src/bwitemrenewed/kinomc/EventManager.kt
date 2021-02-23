package bwitemrenewed.kinomc

import bedwarsgun.bedwarsgun.GunManager.DefaultGun
import bwitemrenewed.kinomc.bedwarsprotect.ProtectBuilding
import bwitemrenewed.kinomc.TrapManager.DefaultTrap
import bwitemrenewed.kinomc.gunmanager.SuperGun
import bwitemrenewed.kinomc.rushmode.RushModeCore
import io.github.bedwarsrel.events.BedwarsGameStartedEvent
import io.github.bedwarsrel.game.Game
import io.github.bedwarsrel.game.Team
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.player.*
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*
import kotlin.collections.ArrayList


class EventManager : Listener {

    companion object{
        var GunList = ArrayList<Material>()
        var DropList = ArrayList<EntityType>()
        var SneckList = ArrayList<Player>()
        lateinit var game: Game
        var PlayerCanExpendBlockMap = HashMap<Player,Boolean>()
    }
    @EventHandler
    fun onDamage(event: EntityDamageEvent){
        if(event.entity.type == EntityType.PLAYER){
            if(event.cause == EntityDamageEvent.DamageCause.FALL)
                event.isCancelled = true
        }
    }
    @EventHandler
    fun onBreak(event:BlockBreakEvent){
        val loc = event.block.location
        if(event.block.type == Material.TRIPWIRE){
            event.player.world.createExplosion(loc.x,loc.y,loc.z,2F,false,false)
        }
    }
    @EventHandler
    fun onStart(event: BedwarsGameStartedEvent){

        game = event.game
        RushModeCore.game = event.game
        val all: Iterator<Team> = game.teams.values.iterator()
        while (all.hasNext()) {
            val team: Team = all.next()
            val teamBed: Iterator<Player> = team.players.iterator()
            while (teamBed.hasNext()) {
                teamBed.next()
                ProtectBuilding().placeBedProtect(game, team, team.targetHeadBlock, team.targetFeetBlock)
            }
        }
    }
    @EventHandler
    fun onMove(event: PlayerMoveEvent){//移动事件 机枪和陷阱需要
        SuperGun().superGunModelEvent(event,Material.STONE_HOE)//机枪
        DefaultTrap.DefaultTrapMove(event)//陷阱
    }
    @EventHandler
    fun onItemMove(event:InventoryMoveItemEvent){
        if(event.item.type in EventManager.GunList){
            event.isCancelled = true//如果移动枪械 取消事件
        }
    }
    @EventHandler
    fun onPut(event:BlockPlaceEvent){
        DefaultTrap.DefaultTrapPut(event)
        PlayerCanExpendBlockMap[event.player] = true//防止方块卡人
        Bukkit.getScheduler().runTaskLater(setup.instance,{ PlayerCanExpendBlockMap[event.player] = false},2L)//防止方块卡人
    }
    @EventHandler
    fun onInteraceEvent(event: PlayerInteractEvent) {//点击事件
        Bukkit.getScheduler().runTaskLater(setup.instance,{
            RushModeCore().Core(event)//快速搭路模式
        },1L)

        val d = DefaultGun()
        d.defaultGunModel(event, Material.DIAMOND_HOE, 20F, 1, 48, 40F, 1F)//步枪
        d.defaultGunModel(event, Material.WOOD_HOE, 2F, 3, 12, 50F, 4F)//木制枪
        d.defaultGunModel(event, Material.GOLD_HOE, 0.2F, 3, 2, 120F, 0.1F)//狙击枪
        val s = SuperGun()
        s.superGunmodel(event,Material.STONE_HOE,CoolDownSpeed = 40F,SingleLoadTime = 1,MaxLoad = 96,SPEED = 70F,SPREAD = 20F)//机枪
    }
    @EventHandler
    fun onSneak(event:PlayerToggleSneakEvent){
        if(!event.isSneaking){
            Bukkit.getScheduler().runTaskLater(setup.instance, Runnable {
                event.player.removePotionEffect(PotionEffectType.SLOW)//视角变化
            }, 1)
        }
        if(event.player.inventory.itemInHand.type in GunList) {
            if (event.isSneaking) {
                event.player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 255555, 255, false, false), false)//视角变化
                if(!SneckList.contains(event.player)){//只加入蹲下列表一次
                    SneckList.add(event.player)
                }

            } else {
                Bukkit.getScheduler().runTaskLater(setup.instance, Runnable {
                    event.player.removePotionEffect(PotionEffectType.SLOW)//视角变化
                }, 1)
                if(SneckList.contains(event.player)){//如果蹲下列表有 就删除一次
                    SneckList.remove(event.player)
                }
            }
        }
    }
    @EventHandler
    fun onPickArrow(event: PlayerPickupItemEvent){
        if(event.item.type in DropList){
            event.isCancelled = true
        }
    }//不能捡起箭
    @EventHandler
    fun onItemDrop(event: PlayerDropItemEvent){

        if(event.itemDrop.itemStack.type in GunList){
            event.isCancelled = true
        }
    }
}