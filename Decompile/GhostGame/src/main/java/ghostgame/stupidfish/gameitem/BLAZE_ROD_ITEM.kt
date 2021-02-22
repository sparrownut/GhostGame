package ghostgame.stupidfish.gameitem

import ghostgame.stupidfish.particlemanager.ParticleManager
import ghostgame.stupidfish.setup
import ghostgame.stupidfish.teammanager.Team
import ghostgame.stupidfish.threadmanager.ThreadManager.runtasklater
import ghostgame.stupidfish.utils.utils
import net.minecraft.server.v1_8_R3.EnumParticle
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

object BLAZE_ROD_ITEM {
    //public static boolean BLAZE_ROD_CODING = false;
    var BLAZE_ROD_CODING: MutableMap<Player?, Boolean?> = HashMap()
    fun Hunter_Blaze_Rod_Click(event: PlayerInteractEvent) {
        val p = event.player
        if (setup.is_Game_Start_Timed && Team.JudgPlayerInList(p, Team.HunterTeam)) { //如果游戏已经开始 且玩家队伍正确
            val b = p.getTargetBlock(null as HashSet<Byte?>?, 200) //获取猎人目光所在的200米以内的方块
            ParticleManager.spawnArrowParticle(b.location, EnumParticle.LAVA, 1, 0f, 1f, 0f, 0f) //Particle
            //生成通天的粒子束
            val count = event.item.amount - 1
            val itemloc = utils.JudgItemInBag(p, event.item)
            if (count > 0) {
                p.itemInHand = ItemStack(event.item.type, count)
            }
            p.itemInHand = ItemStack(Material.AIR, count)
            //烈焰棒的损耗
            runtasklater(20L) {
                utils.SendAllMessage("&6" + event.player.name + "&c发动了箭雨！") //提示信息
                val x = b.location.x
                val z = b.location.z
                for (i in 0..511) {
                    val drx = Math.random() * 20 - 10
                    val drz = Math.random() * 20 - 10
                    val a = p.world.spawnEntity(
                        Location(p.world, x + drx, b.y + 64 + Math.random() * 100, z + drz),
                        EntityType.ARROW
                    ) //生成箭的部分
                    Bukkit.getScheduler().runTaskLater(setup.instance, { a.remove() }, 20L * 5) //5秒后清除
                } //生成512支箭
            } //箭雨的功能实现
            object : BukkitRunnable() {
                var time = 60 //60s
                override fun run() {
                    if (!setup.is_Game_Start_Timed) {
                        cancel()
                        return
                    } //如果循环中 发现没开始就结束事件  防止错误的获得道具
                    if (setup.is_Game_Start_Timed) {
                        BLAZE_ROD_CODING[p] = true //正在冷却
                        time--
                        if (p.itemInHand != null) {
                            //防止null
                            if (p.itemInHand.type != Material.DIAMOND_SWORD) {
                                p.level = time
                            }
                        }
                        //恢复
                        if (time <= 0) {
                            BLAZE_ROD_CODING[p] = false
                            val lore: MutableList<String?> = LinkedList()
                            val blRod = ItemStack(Material.BLAZE_ROD)
                            val blRodMeta = blRod.itemMeta
                            blRodMeta.displayName = utils.StringReplace("&c道具:箭雨")
                            lore.clear()
                            lore.add(utils.StringReplace("&a冷却时间:&c60s"))
                            blRodMeta.lore = lore
                            blRod.itemMeta = blRodMeta
                            p.inventory.setItem(1, blRod)
                            cancel()
                            return
                        }
                    }
                }
            }.runTaskTimer(setup.instance, 0L, 20L) //定时恢复道具栏
        } else {
            utils.SendMessageRe(event.player, "&c你从哪里弄到的这个?如果不是开挂 请上报给&6管理员 &c将有奖励哦")
        }
    }
}