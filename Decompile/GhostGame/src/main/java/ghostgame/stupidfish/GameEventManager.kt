package ghostgame.stupidfish

import ghostgame.stupidfish.coin.CoinManager
import ghostgame.stupidfish.gameboard.BoardSender
import ghostgame.stupidfish.gameitem.*
import ghostgame.stupidfish.gamesound.GameSoundPlayer
import ghostgame.stupidfish.particlemanager.ParticleManager
import ghostgame.stupidfish.playerlist.PlayerListManager
import ghostgame.stupidfish.playerlist.PlayerListManager.Companion.PlayerLeaveListFunc
import ghostgame.stupidfish.prefix.PrefixManager
import ghostgame.stupidfish.teammanager.Team
import ghostgame.stupidfish.threadmanager.ThreadManager.runtasklater
import ghostgame.stupidfish.utils.utils
import net.minecraft.server.v1_8_R3.EnumParticle
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import java.util.function.Consumer

class GameEventManager : Listener {
    @EventHandler
    fun onInteract(event: PlayerInteractEvent) { //物品特效
        try {
            FEATHER_ITEM.All_Feather_Click(event)
            if (event.item.type == Material.SOUL_SAND) {
                SOUL_SAND_ITEM.Ghost_Soul_Sand_Click(event)
            } else if (event.item.type == Material.DIAMOND_SWORD) { //用剑击打空气
                DIAMOND_SWORD_ITEM.Hunter_Diamond_Sword_Click(event)
            } else if (event.item.type == Material.BLAZE_ROD) { //猎人的jianyu
                BLAZE_ROD_ITEM.Hunter_Blaze_Rod_Click(event)
            } else if (event.item.type == Material.SKULL_ITEM) {
                MAGIC_HEAD.Companion.Ghost_Magic_Head_Click(event)
            } else if (event.item.type == Material.BED) {
                val BI = BED_ITEM()
                BI.All_Bed_Click(event)
            }
        } catch (e: NullPointerException) {
            Bukkit.getLogger().info(utils.StringReplace("&cGhostGame出现null错误 不影响游玩 只是记录"))
        }
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        event.quitMessage = ""
        val p = event.player
        val player = event.player
        if (!setup.is_Game_Start_Timed) {
            PlayerLeaveListFunc(player)
            utils.SendAllMessage("&6" + p.name + "&a退出了游戏")
        } else {
            if (Team.JudgPlayerInList(player, Team.HunterTeam) || Team.JudgPlayerInList(player, Team.GhostTeam)) {
                utils.SendAllMessage("&6" + p.name + "&a退出了游戏")
            }
            if (Team.JudgPlayerInList(player, Team.GhostTeam)) { //退出就从列表中去除
                Team.GhostTeam.remove(player) //退出
            } else if (Team.JudgPlayerInList(player, Team.HunterTeam)) { //退出就从列表中去除
                Team.HunterTeam.remove(player)
            }
            GameRun.JudgGameRes(player) //判断游戏结果
        }
        if (Team.JudgPlayerInList(player, PlayerListManager.SwitchGhostBySelf)) //如果在幽灵列表里就去掉他
            PlayerListManager.SwitchGhostBySelf.remove(player)
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        BoardSender.StartedGameBoard()
        event.deathMessage = ""
        val deather = event.entity
        val killer = event.entity.killer
        utils.clearInv(deather) //死亡清除背包
        GameSoundPlayer.PlayKillSound(killer) //Sound
        if (setup.is_Game_Start_Timed) { //如果开始
            if (GameRun.is_DeathMode) {
                //彻底死亡
                deather.gameMode = GameMode.SPECTATOR
                utils.SendMessageRe(deather, "&4您已死亡")
                try {
                    Team.GhostTeam.remove(deather)
                } catch (e: Exception) {
                }
                try {
                    Team.HunterTeam.remove(deather)
                } catch (e: Exception) {
                }
            } else { //不在死亡竞赛模式
                if (killer.type == EntityType.PLAYER && deather.type == EntityType.PLAYER) { //Check killer is !null
                    CoinManager.KillReward(killer) //硬币奖励
                    utils.DeathRandomMessage(deather, killer) //死亡信息
                    if (Team.JudgPlayerInList(deather, Team.GhostTeam) && Team.JudgPlayerInList(
                            killer,
                            Team.HunterTeam
                        )
                    ) { //如果猎人捕杀幽灵
                        utils.SendMessageRe(deather, "&4您被杀死了 您将变成猎人")
                        //幽灵死了
                        Team.GhostTeam.remove(deather) //猎人捕杀幽灵
                        Team.HunterTeam.add(deather)
                    } else if (Team.JudgPlayerInList(deather, Team.HunterTeam) && Team.JudgPlayerInList(
                            killer,
                            Team.GhostTeam
                        )
                    ) { //如果幽灵捕杀猎人
                        //猎人死了
                        utils.SendMessageRe(deather, "&4您被杀死了 您将变成幽灵")
                        try {
                            runtasklater(5L) {
                                Team.HunterTeam.remove(deather)
                                Team.GhostTeam.add(deather)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else { //意外死亡 nope
                        if (event.entity.killer.type == EntityType.ZOMBIE) {
                            utils.SendAllMessage("&a" + deather.name + "被幽灵的盟友杀死了") //意外死亡信息
                            runtasklater(1L) {
                                Team.GhostTeam.add(deather)
                                Team.HunterTeam.remove(deather)
                            }
                        } else {
                            if (Team.JudgPlayerInList(deather, Team.GhostTeam)) {
                                Team.GhostTeam.remove(deather)
                                Team.HunterTeam.add(deather)
                            } else if (Team.JudgPlayerInList(deather, Team.HunterTeam)) {
                                Bukkit.getScheduler().runTaskLater(setup.instance, {
                                    Team.HunterTeam.remove(deather)
                                    Team.GhostTeam.add(deather)
                                }, 10L)
                            }
                            utils.SendAllMessage("&a" + deather.name + "莫名其妙死了") //意外死亡信息
                        }
                    }
                }
                PrefixManager.prefixAdd()
            }
        }
        GameRun.JudgGameRes(deather)
    }

    @EventHandler
    fun onDamageByEntity(event: EntityDamageByEntityEvent) {
        if (setup.is_Game_Start_Timed) {
            if (event.entity.type == EntityType.PLAYER) {
                if (event.damager.type == EntityType.ZOMBIE && Team.JudgPlayerInList(
                        event.entity as Player,
                        Team.GhostTeam
                    )
                ) {
                    event.isCancelled = true
                }
            }
        }
        if (setup.is_Game_Start_Timed && event.damager.type == EntityType.PLAYER && event.entityType == EntityType.PLAYER) {
            val killer = event.damager as Player
            val damager = event.entity as Player
            if (Team.JudgPlayerInList(killer, Team.HunterTeam) && Team.JudgPlayerInList(damager, Team.HunterTeam)) {
                event.isCancelled = true
            }
            if (Team.JudgPlayerInList(killer, Team.GhostTeam) && Team.JudgPlayerInList(damager, Team.GhostTeam)) {
                event.isCancelled = true
            }
        }
        if (setup.is_Game_Start_Timed) { //判断是不是箭的伤害
            if (Team.JudgPlayerInList(event.entity as Player, Team.GhostTeam)) {
                if (Team.JudgPlayerInList(event.damager as Player, Team.GhostTeam)) {
                    event.isCancelled = true
                }
            }
        }
    } //玩家间伤害过滤器

    @EventHandler
    fun onBowSHot(event: EntityShootBowEvent) { //射击监测
        BOW_ITEM.Ghost_Bow_Shoot_Event(event)
    } //射箭

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (setup.is_Game_Start_Timed) {
            val p = event.player
            val iteminhand = event.player.itemInHand
            val LInt: MutableList<Int> = LinkedList()
            if (Team.GhostTeam.size != 0) {
                Team.GhostTeam.forEach(Consumer { ghost: Player? ->
                    LInt.add(
                        utils.LocDistanceCalc(ghost!!.location, p.location).toInt()
                    )
                })
                if (Team.JudgPlayerInList(
                        p,
                        Team.HunterTeam
                    ) && iteminhand.type == Material.DIAMOND_SWORD
                ) { //如果猎人手里拿着剑
                    LInt.sortWith(java.util.Comparator { o1, o2 ->

                        //排序 找出最小距离
                        if (o1 < o2) -1 //-1代表交换两个数的位置
                        else 1
                    })
                    if (LInt.size != 0) {
                        try {
                            p.level = LInt[0] //等级设置为距离
                        } catch (IOOBE: IndexOutOfBoundsException) {
                            IOOBE.printStackTrace()
                        }
                    }
                    ParticleManager.spawnParticle(
                        p, EnumParticle.REDSTONE, 1, 1f, Math.random().toFloat(), Math.random()
                            .toFloat(), Math.random().toFloat()
                    ) //Particle
                }
            } else {
                p.level = 0
            }
        }
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        utils.clearInv(event.player) //重生清除背包
        BoardSender.StartedGameBoard()
        if (setup.is_Game_Start_Timed) {
            BoardSender.StartedGameBoard()
            player.noDamageTicks = 20
            utils.SendMessageRe(player, "&a您将有1秒的无敌时间")
            object : BukkitRunnable() {
                override fun run() {
                    if (!setup.is_Game_Start_Timed) {
                        cancel()
                        return
                    }
                    ParticleManager.spawnParticle(player, EnumParticle.FLAME, 2, 1f, 1f, 0f, 0f)
                    utils.SendMessageRe(player, "&a无敌时间已过")
                    cancel()
                    return
                }
            }.runTaskTimer(setup.instance, 20L, 0L)
            if (setup.is_Game_Start_Timed) {
                PrefixManager.prefixAdd()
                Bukkit.getScheduler().runTaskLater(setup.instance, {
                    if (Team.JudgPlayerInList(player, Team.HunterTeam)) {
                        utils.SendMessageRe(player, "&a重生:&c猎人")
                        GameRun.GiveHunterItem(player) //是猎人 重生给武
                        player.teleport(setup.GameLoc)
                    } else if (!Team.JudgPlayerInList(player, Team.HunterTeam)) {
                        utils.SendMessageRe(player, "&a重生:&b幽灵")
                        GameRun.GiveGhostItem(player) //是幽灵 重生给武器
                        player.teleport(setup.GameLoc)
                    } else {
                        utils.SendAllMessage(player.name + "身份不明 应该是_stupidfish写bug了")
                        player.teleport(setup.GameLoc)
                    }
                }, 20L)
                player.teleport(setup.GameLoc)
            } else {
                player.teleport(setup.SpawnLoc)
            }
        }
    }
}