package bwitemrenewed.kinomc.gunmanager

import bedwarsgun.bedwarsgun.utils.utils
import bwitemrenewed.kinomc.EventManager
import bwitemrenewed.kinomc.particlemanager.FireParticle
import bwitemrenewed.kinomc.setup
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import java.util.HashSet

class DefaultRocket {
    companion object {
        var map = hashMapOf<Player, Float>()
    }
    fun superGunModelEvent(event: PlayerMoveEvent, material: Material){
        if(event.player.itemInHand.type == material){
            event.player.addPotionEffect(
                PotionEffect(PotionEffectType.SLOW, 99999, 3, false, false),
                false
            )//视角变化
        }else{
            if(event.player.itemInHand.type !in EventManager.GunList) {
                event.player.removePotionEffect(PotionEffectType.SLOW)
            }
        }
    }
    fun superGunmodel(
        event: PlayerInteractEvent,
        GunMaterial: Material,
        CoolDownSpeed: Float,
        SingleLoadTime: Int,
        MaxLoad: Int,
        SPEED: Float,
        SPREAD: Float
    ) {
        val player = event.player
        val item = event.item
        val cdValue = 20//冷却结束的等级
        if (item != null) {
            if (item.type == GunMaterial) {//如果是这个材料的
                if (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK) {//如果是右键 发射
                    if (map.get(player) == null) {
                        gunShoot(event, CoolDownSpeed, cdValue, SPEED, SPREAD)//射事件 会射而且冷却程度归0
                    } else if (map.get(player)!! >= cdValue) {
                        gunShoot(event, CoolDownSpeed, cdValue, SPEED, SPREAD)//射事件 会射而且冷却程度归0
                    }
                } else if (event.action == Action.LEFT_CLICK_AIR || event.action == Action.LEFT_CLICK_BLOCK) {//如果时左键 装弹
                    gunLoad(player, event.item, MaxLoad, SingleLoadTime)//装填
                }
            }
        }
    }

    private fun gunShoot(
        event: PlayerInteractEvent,
        CoolDownSpeed: Float,
        cdValue: Int,
        SPEED: Float,
        SPREAD: Float
    ) {//射击事件
        if (event.item.amount >= 2) {//如果还有弹药
            val player = event.player
            itemAmountLoss(event.item)//弹药减少
            object : BukkitRunnable() {
                override fun run() {
                    map[player]?.plus(CoolDownSpeed)?.let { map.put(player, it) }
                    if (map[player]!! >= cdValue) {
                        cancel()
                        return
                    }
                }

            }.runTaskTimer(setup.instance, 0L, 1L)//回复
            val blockSet = HashSet<Material>()
            blockSet.add(Material.AIR)
            val PlayerSeeLocation = player.getTargetBlock(blockSet, 1).location
            Location(
                player.world,
                PlayerSeeLocation.x,
                PlayerSeeLocation.y,
                PlayerSeeLocation.z,
                player.eyeLocation.yaw,
                player.eyeLocation.pitch
            )//这是箭的发射位置
            //val arrow = player.launchPro...
            val TNT = player.launchProjectile(Arrow::class.java,player.location.direction)
            TNT.velocity = player.location.direction.multiply(SPEED)//速度
            val originalVector = player.location.direction
            FireParticle.SuperFireParticle(player)//生成开火的粒子
            //player.velocity = Vector(0 - originalVector.x, (0 - originalVector.y), 0 - originalVector.z).multiply(SPEED/ 400)// rpg无后座力
            if(SPEED > 60) {
                player.addPotionEffect(
                    PotionEffect(PotionEffectType.BLINDNESS, 3, 255, false, false),
                    false
                )//视角变化
                player.world.playSound(player.location, Sound.FIRE, 10F, 0.5F)
            }

            //arrow.fireTicks = 5
            TNT.knockbackStrength = 0//设置箭击退0
            TNT.shooter = player
            player.noDamageTicks = 2
            map[player] = 0F//设置冷却进度为0
            Bukkit.getScheduler().runTaskTimer(setup.instance, Runnable { TNT.remove() }, 200L, 200L)//10s后清理
        } else {
            utils().MessageSendRe("&c您没有弹药了 左键装填", event.player)
        }
    }//射击

    private fun itemAmountLoss(item: ItemStack) {
        val itemamount = item.amount
        if (itemamount >= 2) {//确保数量>0
            item.amount--//减少一个item数量
        }
    }//损耗

    private fun gunLoad(player: Player, item: ItemStack, MaxLoad: Int, SingleLoadTime: Int) {
        object : BukkitRunnable() {
            override fun run() {
                //装填事件
                if (item.amount >= MaxLoad) {
                    cancel()
                    return
                }//装填完毕
                item.amount++
                player.playSound(player.location, Sound.DIG_SNOW, 1F, 4F)
            }

        }.runTaskTimer(setup.instance, 0L, SingleLoadTime.toLong())//单次装填
    }//装弹
}