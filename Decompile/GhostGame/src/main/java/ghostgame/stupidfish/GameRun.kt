package ghostgame.stupidfish

import ghostgame.stupidfish.coin.CoinManager
import ghostgame.stupidfish.gameboard.BoardSender
import ghostgame.stupidfish.gameitem.BLAZE_ROD_ITEM
import ghostgame.stupidfish.gamesound.GameSoundPlayer
import ghostgame.stupidfish.gametreeevent.LastGaspGoalEvent
import ghostgame.stupidfish.playerlist.PlayerListManager
import ghostgame.stupidfish.prefix.PrefixManager
import ghostgame.stupidfish.serverinit.Restart
import ghostgame.stupidfish.teammanager.Team
import ghostgame.stupidfish.threadmanager.MainThread
import ghostgame.stupidfish.utils.utils
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import java.util.function.Consumer

object GameRun : Listener {
    var is_DeathMode = false
    fun JudgGameRes(player: Player) {
        if (setup.is_Game_Start_Timed) {
            //JudgPlayerInList(player, GhostList)
            if (Team.JudgPlayerInList(player, Team.GhostTeam)) {
                Team.GhostTeam.remove(player)
            }
            if (Team.getTeamSize(Team.GhostTeam) == 0) {
                setup.is_Game_Start_Timed = false
                utils.SendAllMessage("&c猎人赢了!")
                win(Team.HunterTeam, Team.GhostTeam)
            } else if (Team.HunterTeam.size == 0) {
                setup.is_Game_Start_Timed = false
                utils.SendAllMessage("&c幽灵赢了!")
                win(Team.GhostTeam, Team.HunterTeam)
            }
        }
    }

    fun run() { //主线程
        setup.is_Game_Start_Timed = true //设置开始游戏
        PlayerListManager.PlayerTimerList.clear()
        Bukkit.getOnlinePlayers().forEach { p:Player -> GameSoundPlayer.PlayStartSound(p) }
        object : BukkitRunnable() {
            //死亡竞赛
            var timec = 300
            override fun run() { //死亡竞赛 run
                if (!setup.is_Game_Start_Timed) {
                    cancel()
                    return
                }
                Bukkit.getOnlinePlayers().forEach { player: Player -> utils.sendActionBar(player, "&c$timec") }
                if (timec < 60) {
                    if (timec <= 10) {
                        if (timec <= 0) {
                            utils.SendAllMessage("&c已经开启&c死亡竞赛")
                            utils.SendAllMessage("&4小心!此模式下,所有人近战将一击必杀")
                            LastGaspGoalEvent.DeathKillMode()
                            is_DeathMode = true
                            cancel()
                            return
                        } else {
                            utils.SendAllMessage("&4还有" + timec + "秒 即将开启死亡竞赛")
                            Bukkit.getOnlinePlayers()
                                .forEach {p: Player -> GameSoundPlayer.PlayTimerSound(p) }
                        }
                    }
                } else if (timec == 60) {
                    utils.SendAllMessage("&c还有60秒 即将开启死亡竞赛")
                }
                timec--
            }
        }.runTaskTimer(setup.instance, 0L, 20L) //死亡竞赛
        utils.SendAllMessage("&c游玩快乐!")
        utils.SendAllMessage("&4下一环节:死亡竞赛 -> 5Min")
        Bukkit.getOnlinePlayers().forEach {p: Player -> GameSoundPlayer.PlayStartSound(p) }
        Bukkit.getOnlinePlayers().forEach { player: Player ->  //选择身份 选择器
            var P_Ghost = 0.5f
            if (Team.JudgPlayerInList(player, PlayerListManager.SwitchGhostBySelf)) {
                P_Ghost = 0.3f
            }
            if (Math.random() > 0.3 && Team.HunterTeam.size != 0) { //random选择身份
                Team.GhostTeam.add(player)
                utils.SendMessageRe(player, "&a您为幽灵")
            } else {
                if (Team.GhostTeam.size == 0) {
                    Team.GhostTeam.add(player)
                    utils.SendMessageRe(player, "&a您为幽灵")
                } else {
                    Team.HunterTeam.add(player)
                    utils.SendMessageRe(player, "&4您为猎人")
                }
            }
        } //tp
        Bukkit.getScheduler().runTask(setup.instance) {
            //这是一开始计时15s出动的线程
            Team.GhostTeam.forEach(Consumer { ghost: Player? ->
                ghost!!.teleport(setup.GameLoc) //先把ghost传送到位置
                GiveGhostItem(ghost)
            })
            utils.SendAllMessage("&c&l猎人将会在15秒后出动")
            Team.HunterTeam.forEach(Consumer { hunter: Player? ->
                for (i in 0..35) hunter!!.inventory.setItem(
                    i, ItemStack(
                        Material.AIR
                    )
                )
            })
            object : BukkitRunnable() {
                //15s
                var timer = 15
                override fun run() {
                    if (!setup.is_Game_Start_Timed) {
                        cancel()
                        return
                    }
                    timer--
                    Team.HunterTeam.forEach(Consumer { player: Player? ->
                        if (timer <= 0) {
                            player!!.level = 0
                            BoardSender.StartedGameBoard()
                        }
                        player!!.level = timer
                    })
                    if (timer <= 5) {
                        if (timer <= 0) {
                            //hunter do
                            MainThread.StartThread()
                            GameStart()
                            cancel()
                            return
                        }
                        utils.SendAllMessage("&c猎人还有" + timer + "秒即将出动")
                    }
                }
            }.runTaskTimerAsynchronously(setup.instance, 0L, 20L)
        } //
    }

    fun GiveHunterItem(p: Player) {
        utils.clearInv(p) //给猎人物品之前清理
        object : BukkitRunnable() {
            override fun run() {
                for (i in 0..4) {
                    p.teleport(setup.GameLoc) //游戏开始 传送猎人到位置
                    p.maxHealth = 40.0
                    p.health = 40.0
                }
                cancel()
                return
            }
        }.runTaskLater(setup.instance, 10L)
        val PE = PotionEffect(PotionEffectType.SPEED, 999999, 1)
        p.addPotionEffect(PE, true) //Caused by: java.lang.IllegalStateException: Asynchronous effect add!
        utils.SendMessageRe(p, "&4追杀幽灵并杀的一个不剩")
        val diamond_sword = ItemStack(Material.DIAMOND_SWORD)
        diamond_sword.addEnchantment(Enchantment.DAMAGE_ALL, 1)
        diamond_sword.addEnchantment(Enchantment.FIRE_ASPECT, 1)
        val itemMeta_diamond_Sword = diamond_sword.itemMeta
        itemMeta_diamond_Sword.displayName = utils.StringReplace("&6&l猎人刀")
        val lore: MutableList<String?> = LinkedList()
        lore.add(utils.StringReplace("&a这把刀手持时 经验条会随着幽灵的气息&6增加 "))
        lore.add(utils.StringReplace("&4幽灵越近经验条越高"))
        lore.add(utils.StringReplace("&4挥刀时 音调代表着你面前的方位是否有幽灵"))
        itemMeta_diamond_Sword.lore = lore
        diamond_sword.itemMeta = itemMeta_diamond_Sword
        p.inventory.setItem(0, diamond_sword) //猎人刀
        val Diamond_che = ItemStack(Material.DIAMOND_CHESTPLATE)
        val Diamond_cheIM = Diamond_che.itemMeta
        Diamond_cheIM.displayName = utils.StringReplace("&c猎人盔甲")
        val Diamond_Chestplate_Lore: MutableList<String?> = ArrayList()
        Diamond_Chestplate_Lore.add(utils.StringReplace("&c猎人专属盔甲"))
        Diamond_cheIM.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
        Diamond_che.itemMeta = Diamond_cheIM
        p.inventory.chestplate = Diamond_che //盔甲
        try {
            if (!BLAZE_ROD_ITEM.BLAZE_ROD_CODING.containsKey(p)) { //如果rod不在冷却(冷却列表没有这个人)
                val blRod = ItemStack(Material.BLAZE_ROD)
                val blRodMeta = blRod.itemMeta
                val lore_blROd: MutableList<String?> = ArrayList()
                blRodMeta.displayName = utils.StringReplace("&c道具:箭雨")
                lore_blROd.add(utils.StringReplace("&a冷却时间:&c60s"))
                blRodMeta.lore = lore
                blRod.itemMeta = blRodMeta
                p.inventory.setItem(1, blRod)
            } else { //冷却列表有这个人
                if (!BLAZE_ROD_ITEM.BLAZE_ROD_CODING[p]!!) { //获取到的是不在冷却
                    val blRod = ItemStack(Material.BLAZE_ROD)
                    val blRodMeta = blRod.itemMeta
                    val lore_blROd: MutableList<String?> = ArrayList()
                    blRodMeta.displayName = utils.StringReplace("&c道具:箭雨")
                    lore_blROd.add(utils.StringReplace("&a冷却时间:&c60s"))
                    blRodMeta.lore = lore_blROd
                    blRod.itemMeta = blRodMeta
                    p.inventory.setItem(1, blRod)
                }
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    fun GiveGhostItem(p: Player?) {
        p!!.level = 0
        p.maxHealth = 16.0 //设置血
        p.health = 16.0
        utils.clearInv(p)
        utils.SendMessageRe(p, "&c猎人已出动 你隐身了")
        p.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 999999, 1), false) //9999ticks的隐身1
        p.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 999999, 3), false)
        p.isCustomNameVisible = false //名字隐身
        utils.SendMessageRe(p, "&4逃离猎人的追杀并充分利用道具反击")
        val diamond_bow = ItemStack(Material.BOW)
        diamond_bow.addEnchantment(Enchantment.ARROW_INFINITE, 1)
        diamond_bow.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 4) //击退jian
        val itemMeta = diamond_bow.itemMeta
        val arrow = ItemStack(Material.ARROW, 3)
        itemMeta.displayName = utils.StringReplace("&6&l弓")
        val lore: MutableList<String?> = LinkedList()
        lore.add(utils.StringReplace("&a每射出一次 将会暴露自己的位置"))
        lore.add(utils.StringReplace("&4但是击退极高"))
        itemMeta.lore = lore
        diamond_bow.itemMeta = itemMeta
        val taunt = ItemStack(Material.SOUL_SAND, 5)
        val tauntim = taunt.itemMeta
        tauntim.displayName = utils.StringReplace("&c嘲讽")
        taunt.itemMeta = tauntim
        val Magic_Head = ItemStack(Material.SKULL_ITEM)
        val Magic_Head_IM = Magic_Head.itemMeta
        Magic_Head_IM.displayName = utils.StringReplace("&4技能:召唤")
        Magic_Head.itemMeta = Magic_Head_IM
        p.inventory.setItem(2, Magic_Head)
        p.inventory.setItem(0, diamond_bow)
        p.inventory.setItem(8, arrow)
        p.inventory.setItem(1, taunt)
    }

    fun GameStart() { //Really START
        PrefixManager.prefixAdd()
        Bukkit.getScheduler().runTask(setup.instance) {
            Team.HunterTeam.forEach { p: Player -> GiveHunterItem(p) } //give sword\
            Team.GhostTeam.forEach { p: Player -> GiveGhostItem(p) }
        }
    }

    fun win(WinList: List<Player?>?, LoseList: List<Player?>?) {
        WinList!!.forEach(Consumer { winer: Player? ->
            CoinManager.WinReward(winer)
            winer!!.world.spawnEntity(winer.location, EntityType.FIREWORK)
            for (i in 0..8) winer.inventory.setItem(i, ItemStack(Material.AIR)) //清理背包
            winer.level = 0
            winer.activePotionEffects.forEach(Consumer { effecttype: PotionEffect -> winer.removePotionEffect(effecttype.type) }) //去除药水效果
            winer.gameMode = GameMode.ADVENTURE
        })
        object : BukkitRunnable() {
            var i = 10
            override fun run() {
                i--
                LoseList!!.forEach(Consumer { loser: Player? -> loser!!.gameMode = GameMode.SPECTATOR })
                if (i <= 0) {
                    Restart.restart()
                    cancel()
                    return
                }
            }
        }.runTaskTimer(setup.instance, 0L, 20L)
    }
}