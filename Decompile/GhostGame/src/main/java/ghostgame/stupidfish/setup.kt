package ghostgame.stupidfish

import ghostgame.stupidfish.gamerule.BasicGameRuleManager
import ghostgame.stupidfish.playerlist.PlayerListManager
import ghostgame.stupidfish.utils.utils
import ghostgame.stupidfish.weathermanager.weatherOrDay
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class setup : JavaPlugin() {
    override fun onEnable() {
        instance = this
        object : BukkitRunnable() {
            override fun run() {
                if (is_Game_Start_Timed) {
                    cancel()
                    return
                }
            }
        }.runTaskTimer(this, 0L, 5L)
        object : BukkitRunnable() {
            //从开始就创建一个刚进程用来显示加入人数什么的
            override fun run() {
                if (PlayerListManager.WaitPlayer_Shower()) {
                    cancel()
                    return
                }
            }
        }.runTaskTimerAsynchronously(this, 0L, 20L)
        saveDefaultConfig()
        Reload()
        Bukkit.getPluginManager().registerEvents(PlayerListManager(), this)
        Bukkit.getPluginManager().registerEvents(weatherOrDay(), this)
        Bukkit.getPluginManager().registerEvents(GameEventManager(), this)
        Bukkit.getPluginManager().registerEvents(BasicGameRuleManager(), this)
        // Plugin startup logic
    }

    override fun onCommand(
        sender: CommandSender,
        cmd: Command,
        commandLabel: String,
        args: Array<String>
    ): Boolean { //命令
        return if (sender is Player) { //判断是不是玩家
            if (cmd.name.equals("ghostgame", ignoreCase = true)) { //如果输入的指令是/ghostgame
                if (args.size < 1) {
                    utils.SendMessageRe(sender, "&c请输入正确的参数 &aEG:/ghostgame settingSpawnLoc/settingGameLoc")
                } else {
                    val loc = sender.location
                    val config = config
                    if (sender.hasPermission("ghostgame.admin")) {
                        when (args[0].toLowerCase(Locale.ROOT)) {
                            "settingspawnloc" -> {
                                config["SpawnLoc"] = sender.location
                                saveConfig()
                                utils.SendMessageRe(sender, "&a保存位置成功")
                                Reload()
                                utils.SendMessageRe(sender, "&a重载成功")
                                return true
                            }
                            "settinggameloc" -> {
                                config["GameLoc"] = sender.location
                                saveConfig()
                                utils.SendMessageRe(sender, "&a保存Game位置成功")
                                Reload()
                                utils.SendMessageRe(sender, "&a重载成功")
                                return true
                            }
                        }
                    } else {
                        utils.SendMessageRe(sender, "&c不允许执行此指令！")
                    }
                }
            }
            false
        } else {
            false
        }
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    fun Reload() {
        try {
            val config = config
            minPlayerStart = config.getInt("minPlayer") //获取进服的最小玩家数
            SpawnLoc = config["SpawnLoc"] as Location
            maxPlayer = config.getInt("maxPlayer")
            GameLoc = config["GameLoc"] as Location
            TauntImagePath = config.getString("TauntImagePath")
            kill_Reward = config.getString("kill_Reward")
            win_Reward = config.getString("win_Reward")
            taunt_Reward = config.getString("taunt_Reward")
            kill_tip = config.getString("kill_tip")
            win_tip = config.getString("win_tip")
            mapName = config.getString("mapName")
            lobbyname = config.getString("lobby")
            taunt_tip = config.getString("taunt_tip")
            Bukkit.getLogger().info(utils.StringReplace("win_tip$win_tip"))
            Bukkit.getLogger().info(utils.StringReplace("win_Reward$win_Reward"))
            Bukkit.getLogger().info("Reload config done.")
            this.server.messenger.registerOutgoingPluginChannel(this, "BungeeCord");//注册信道
        } catch (e: Exception) {
            //e.printStackTrace();
        }
    }

    companion object {
        @JvmField
        var instance: setup? = null

        @JvmField
        var is_Game_Start_Timed = false

        @JvmField
        var minPlayerStart = 8
        var pitch = 0.0
        var yaw = 0.0
        var x = 0.0
        var y = 0.0
        var z = 0.0

        @JvmField
        var SpawnLoc: Location? = null

        @JvmField
        var GameLoc: Location? = null

        @JvmField
        var maxPlayer = 16

        @JvmField
        var TauntImagePath = ""

        @JvmField
        var kill_Reward: String? = null

        @JvmField
        var win_Reward: String? = null

        @JvmField
        var kill_tip: String? = null

        @JvmField
        var win_tip: String? = null

        @JvmField
        var taunt_Reward: String? = null

        @JvmField
        var taunt_tip: String? = null

        @JvmField
        var mapName: String? = null

        @JvmField
        var lobbyname = "ghostlobby"
        var helptext = """
               &b游戏规则:
               XXXXXXXX
               XXXXXXXX
               XXXXXXXX
               游玩快乐
               
               """.trimIndent()
    }
}