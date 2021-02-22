package ghostgame.stupidfish.gameitem

import ghostgame.stupidfish.coin.CoinManager
import ghostgame.stupidfish.gamesound.GameSoundPlayer
import ghostgame.stupidfish.imageparticle.ShowImageInParticle
import ghostgame.stupidfish.setup
import ghostgame.stupidfish.teammanager.Team
import ghostgame.stupidfish.utils.utils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.awt.image.BufferedImage
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.imageio.ImageIO

object SOUL_SAND_ITEM {
    var CoolDown: MutableMap<Player, Int> = HashMap()
    fun Ghost_Soul_Sand_Click(event: PlayerInteractEvent) {
        val p = event.player
        if (!CoolDown.containsKey(p)) {
            Run(p, event)
        } else {
            utils.SendMessageRe(p, "&c道具正在冷却 请等待" + CoolDown[p] + "秒")
        }
    }

    fun Run(p: Player, event: PlayerInteractEvent) {
        CoolDown[p] = 15
        object : BukkitRunnable() {
            var timer = 15
            override fun run() {
                CoolDown[p] = timer
                if (timer <= 0) {
                    CoolDown.remove(p)
                    cancel()
                    return
                }
                timer--
            }
        }.runTaskTimer(setup.instance, 0L, 20L) //冷却
        CoinManager.TauntReward(p)
        if (Team.JudgPlayerInList(p, Team.GhostTeam) && setup.is_Game_Start_Timed) {
            GameSoundPlayer.PlayTauntSound(p)
            var BI: BufferedImage? = null
            p.world.spawnEntity(p.location, EntityType.FIREWORK)
            try {
                BI = ImageIO.read(Files.newInputStream(Paths.get(setup.TauntImagePath)))
                ShowImageInParticle.spawnParticleDirect(p, 10, 8f, BI, 64, 0, 0) //放图片
            } catch (e: IOException) {
                Bukkit.getLogger().warning("&c读取嘲讽图片错误")
                utils.SendAllMessage("&c读取嘲讽图片错误 &4很可能是sb蛋喵没设置对路径")
                e.printStackTrace()
            } //粒子
            val count = event.item.amount - 1
            if (count <= 0) {
                p.inventory.itemInHand = ItemStack(Material.AIR)
            } else {
                p.inventory.itemInHand = ItemStack(Material.SOUL_SAND, count)
            } //item 损耗
            p.world.spawnEntity(p.location, EntityType.FIREWORK)
        }
    }
}