package ghostgame.stupidfish.playerlist

import ghostgame.stupidfish.GameRun
import ghostgame.stupidfish.gameboard.BoardSender
import ghostgame.stupidfish.gamesound.GameSoundPlayer
import ghostgame.stupidfish.itemutils.ItemUtils
import ghostgame.stupidfish.prefix.PrefixManager
import ghostgame.stupidfish.setup
import ghostgame.stupidfish.utils.utils
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import java.util.function.Consumer

class PlayerListManager : Listener {
    @EventHandler
    fun onPickupItem(event: PlayerPickupItemEvent) {
        event.isCancelled = true
    } //物品保护

    @EventHandler
    fun onDropItem(event: PlayerDropItemEvent) {
        event.isCancelled = true
    } //物品保护

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val p = event.player
        PrefixManager.prefixRemove(p) //移除前缀
        event.joinMessage = ""
        Bukkit.getOnlinePlayers().forEach { player: Player ->
            player.showPlayer(p)
            p.showPlayer(player)
        }
        utils.clearInv(event.player) //初始化
        utils.SendMessageRe(p, "&4&l游戏规则 使用道具和身份的优势 杀掉对方队伍!")
        utils.SendMessageRe(p, "&4&l幽灵可以隐身 猎人可以通过剑挥动时的声音和等级所显示的位置判断幽灵的位置")
        if (!setup.is_Game_Start_Timed) { //如果没开始 就运行玩家加入/倒计时系统
            Bukkit.getScheduler().runTaskLater(setup.instance, {
                try {
                    PlayerJoinListFunc(event.player) //玩家等候列表加入
                    if (setup.minPlayerStart - PlayerTimerList.size > 0) { //如果小于最小启动认识就显示距离游戏启动还剩...
                        utils.SendAllMessage("&a距离启动游戏还剩&6" + (setup.minPlayerStart - PlayerTimerList.size) + "&a人")
                    }
                    val SB = "&6" +
                            event.player.name +
                            "&a进入了游戏(&c" +
                            PlayerTimerList.size +
                            "&a/&c" +
                            setup.maxPlayer +
                            "&a)"
                    utils.SendAllMessage(SB) //欢迎语
                    BoardSender.JoinGameGameBoard(p) //Board
                } catch (e: NullPointerException) {
                    utils.SendAllMessage("&4貌似管理员还没设置大厅的存档点 &aEG:/ghostgame settingSpawnLoc/settingGameLoc")
                }
            }, 2L)
            event.player.displayName = event.player.name
        } else { //如果开始了
            p.kickPlayer(utils.StringReplace("&c游戏正在进行 您不能进入"))
            p.gameMode = GameMode.SPECTATOR //保险
            utils.SendMessageRe(p, "&4您是后进入的 作为观察者")
            Bukkit.getOnlinePlayers().forEach { player: Player ->
                p.isCustomNameVisible = false
                player.canPickupItems = false
                player.hidePlayer(p)
            }
        }
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val p = event.player
        val Loc = p.location
        if (!setup.is_Game_Start_Timed) {
            if (setup.SpawnLoc?.let { utils.LocDistanceCalc(Loc, it) }!! >= 50) {
                p.teleport(setup.SpawnLoc)
                utils.SendMessageRe(p, "&c您超出了边界!")
            }
        }
        event.player.foodLevel = 20
    }

    @EventHandler
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        val e: Entity = event.entity
        if (e.type == EntityType.PLAYER) {
            (e as Player).player.foodLevel = 20
        }
    }

    fun MaxPlayer_Doing() {
        object : BukkitRunnable() {
            var second = 3
            override fun run() {
                if (PlayerTimerList.size <= setup.minPlayerStart) {
                    cancel()
                    return
                } else {
                    utils.SendAllMessage("&c还有" + second + "秒启动")
                    if (second <= 0) {
                        GameRun.run()
                    }
                }
                second--
            }
        }.runTaskTimer(setup.instance, 0L, 20L)
    }

    @Throws(NullPointerException::class)
    fun PlayerJoinListFunc(p: Player) { //*玩家进入服的操作
        JoinServerInit(p)
        //欢迎语和初始化
        utils.FlushPGhost()
        if (PlayerTimerList.size == setup.minPlayerStart && !setup.is_Game_Start_Timed) { //如果等于进入人数 START TIMER
            i = 30
            object : BukkitRunnable() {
                //计时器
                override fun run() { //开始计时
                    if (PlayerTimerList.size >= setup.minPlayerStart) {
                        i-- //i--
                        BoardSender.TimerStartGameGameBoard(p, i)
                        if (PlayerTimerList.size == setup.maxPlayer) { //如果满员
                            MaxPlayer_Doing()
                            cancel()
                            return
                        }
                        Bukkit.getOnlinePlayers().forEach { player: Player -> player.level = i } //秒设置为等级
                        if (i <= 5) { //5秒内
                            if (i <= 0) {
                                //Run game
                                Bukkit.getScheduler().runTaskAsynchronously(setup.instance) { GameRun.run() }
                                cancel()
                                return
                            }
                            utils.SendAllMessage("&c还有&6" + i + "&c秒开始游戏")
                            Bukkit.getOnlinePlayers()
                                .forEach { p: Player? -> p?.let { GameSoundPlayer.PlayTimerSound(it) } }
                        }
                    } else {
                        utils.SendAllMessage("&c人数不足,启动游戏取消")
                        Bukkit.getOnlinePlayers().forEach { player: Player ->
                            BoardSender.JoinGameGameBoard(player)
                            player.level = 0
                        }
                        cancel()
                        return
                    }
                }
            }.runTaskTimerAsynchronously(setup.instance, 0L, 20L) //计时器结束
        }
    } //玩家进入服的操作

    companion object {
        @JvmField
        var PlayerTimerList: MutableList<Player> = LinkedList()

        @JvmField
        var SwitchGhostBySelf: MutableList<Player> = LinkedList()
        var i = 0
        fun WaitPlayer_Shower(): Boolean {
            i++
            if (i % 10 == 0) { //10秒重复一次提示
                if (setup.minPlayerStart > PlayerTimerList.size) { //如果不足以启动游戏
                    utils.SendAllMessage("&a距离启动游戏还剩&6" + (setup.minPlayerStart - PlayerTimerList.size) + "&a人")
                } else {
                    return true //true返回让bukkitrunnable停止
                }
            }
            return false
        }

        @JvmStatic
        fun PlayerLeaveListFunc(p: Player) {
            PlayerTimerList.remove(p)
        }

        @JvmStatic
        fun switchGhost(p: Player) {
            utils.SendMessageRe(p, "&6选择成功 您成为幽灵的可能性将会提高")
            GameSoundPlayer.PlayStartSound(p)
            SwitchGhostBySelf.add(p)
        }

        fun JoinServerInit(p: Player) {
            p.world.time = 1000
            utils.clearInv(p) //进服清理背包
            p.level = 0
            p.maxHealth = 20.0
            p.health = 20.0
            PlayerTimerList.add(p)
            p.activePotionEffects.forEach(Consumer { effecttype: PotionEffect -> p.removePotionEffect(effecttype.type) }) //去除药水效果
            p.gameMode = GameMode.ADVENTURE
            p.inventory.chestplate = ItemStack(Material.AIR)
            //以上都是初始化

            ItemUtils.givePlayerItemNamed(
                p = p,
                displayName = "&b&l点击代表您有意愿作为幽灵",
                location = 4,
                material = Material.FEATHER,
                amount = 1
            )
            ItemUtils.givePlayerItemNamed(
                p = p,
                displayName = "&b&l返回主大厅",
                location = 8,
                material = Material.BED,
                amount = 1
            )

            p.teleport(setup.SpawnLoc)
            utils.SendMessageRe(p, "&a欢迎加入幽灵战争")
        }
    }
}