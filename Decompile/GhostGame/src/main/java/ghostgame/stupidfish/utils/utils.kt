package ghostgame.stupidfish.utils

import ghostgame.stupidfish.playerlist.PlayerListManager
import ghostgame.stupidfish.teammanager.Team
import net.minecraft.server.v1_8_R3.IChatBaseComponent
import net.minecraft.server.v1_8_R3.PacketPlayOutChat
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

object utils {
    var Player_Ghost_Probability: MutableMap<Player?, Float?> = HashMap() //成为幽灵的概率map
    fun FlushPGhost() { //刷新成为幽灵的几率
        Bukkit.getOnlinePlayers().forEach { onlinePlayer: Player ->
            var PGhost = 70f
            PGhost = 70f
            if (Bukkit.getOnlinePlayers().size == 1) PGhost = 100f //只有一个人概率100%
            else if (Team.JudgPlayerInList(onlinePlayer, PlayerListManager.SwitchGhostBySelf)) PGhost = 80f //选择了的话变80%
            if (Player_Ghost_Probability[onlinePlayer] != null) {
                Player_Ghost_Probability.remove(onlinePlayer)
                Player_Ghost_Probability[onlinePlayer] = PGhost
            } else {
                Player_Ghost_Probability[onlinePlayer] = PGhost
            }
            sendActionBar(onlinePlayer, "&a您为幽灵的概率当前为:$PGhost%")
        }
    }

    fun SendMessageRe(player: Player?, s: String?) {
        player!!.sendMessage("§b§lKino§6Mc §a>> §r" + StringReplace(s))
    }

    fun isNumeric(str: String): Boolean { //judg is numeric
        var i = str.length
        while (--i >= 0) {
            if (!Character.isDigit(str[i])) {
                return false
            }
        }
        return true
    } //判断字符串是不是数字

    fun SendAllMessage(s: String?) {
        Bukkit.getOnlinePlayers().forEach { player: Player? -> SendMessageRe(player, s) }
    } //发送全体消息

    fun StringReplace(s: String?): String {
        val string: String
        string = try {
            s!!.replace('&', '§')
        } catch (e: NullPointerException) {
            return ""
        }
        return string
    } //转换String



    fun LocDistanceCalc(loc1: Location, loc2: Location): Double {
        val x1 = loc1.x
        val y1 = loc1.y
        val z1 = loc1.z
        val x2 = loc2.x
        val y2 = loc2.y
        val z2 = loc2.z
        return Math.sqrt(Math.pow(x1 - x2, 2.0) + Math.pow(y1 - y2, 2.0) + Math.pow(z1 - z2, 2.0))
    }

    fun sendActionBar(p: Player, message: String) {
        val packet = PacketPlayOutChat(
            IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message.replace("&", "§") + "\"}"),
            2.toByte()
        )
        (p as CraftPlayer).handle.playerConnection.sendPacket(packet)
    }

    fun clearInv(p: Player?) {
        for (i in 0..35) {
            p!!.inventory.setItem(i, ItemStack(Material.AIR)) //清理背包
        }
        p!!.inventory.leggings = ItemStack(Material.AIR)
        p.inventory.chestplate = ItemStack(Material.AIR)
        p.inventory.boots = ItemStack(Material.AIR)
        p.inventory.helmet = ItemStack(Material.AIR)
    }

    fun JudgItemInBag(p: Player, `is`: ItemStack): Int {
        for (i in 0..35) {
            val item = p.inventory.getItem(i)
            if (item.type == `is`.type) return i
        }
        p.inventory.chestplate = ItemStack(Material.AIR)
        p.inventory.boots = ItemStack(Material.AIR)
        p.inventory.helmet = ItemStack(Material.AIR)
        p.inventory.leggings = ItemStack(Material.AIR)
        return -1
    }
    fun getDeviateValue(p1: Player, p2: Player?): Double {//获取偏离值的算法模块
        val Loc2 = p2!!.location
        val Loc = p1.location
        val x = Loc.x
        val z = Loc.z
        val cosVectorYaw = (Loc2.z - z) / Math.sqrt(
            Math.pow(Loc2.x - x, 2.0) + Math.pow(
                Loc2.z - z,
                2.0
            )
        )
        val NowcosVectorYaw = Math.cos(Math.toRadians(Loc.yaw.toDouble()))
        return Math.abs(NowcosVectorYaw - cosVectorYaw)
    } //获得p
    fun DeathRandomMessage(deather: Player, killer: Player) {
        //GameRun.JudgPlayerInList(deather, GameRun.GhostList) && GameRun.JudgPlayerInList(killer, GameRun.HunterList)
        if (Team.JudgPlayerInList(deather, Team.GhostTeam) && Team.JudgPlayerInList(
                killer,
                Team.HunterTeam
            )
        ) { //如果猎人杀了幽灵
            if (Math.random() < 0.5) {
                SendAllMessage("&a" + deather.name + "&c被&a" + killer.name + "&c气死了")
            } else {
                SendAllMessage("&a" + deather.name + "&c被&a" + killer.name + "&c吓死了")
            } //死亡信息
            //GameRun.JudgPlayerInList(killer, GameRun.GhostList) && GameRun.JudgPlayerInList(deather, GameRun.HunterList)
        } else if (Team.JudgPlayerInList(deather, Team.HunterTeam) && Team.JudgPlayerInList(
                killer,
                Team.GhostTeam
            )
        ) { //如果幽灵杀了猎人
            SendAllMessage("&b&l" + killer.name + "作为幽灵反杀了" + deather.name)
        }
    }
}