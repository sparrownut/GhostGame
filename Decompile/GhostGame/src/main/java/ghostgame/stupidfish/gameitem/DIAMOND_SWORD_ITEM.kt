package ghostgame.stupidfish.gameitem

import ghostgame.stupidfish.setup
import ghostgame.stupidfish.teammanager.Team
import ghostgame.stupidfish.utils.utils
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import java.util.*
import java.util.function.Consumer

object DIAMOND_SWORD_ITEM {
    fun Hunter_Diamond_Sword_Click(event: PlayerInteractEvent) {
        val p = event.player
        if (Team.JudgPlayerInList(p, Team.HunterTeam) && setup.is_Game_Start_Timed) {
            try {
                val ghostSingle: Player? = null
                val LF: MutableList<Float> = ArrayList()
                Team.GhostTeam.forEach(Consumer { ghost: Player? ->
                    val DiviateValue = utils.getDeviateValue(p, ghost)
                    LF.add(DiviateValue.toFloat())
                })
                LF.sortWith(java.util.Comparator { o1, o2 ->

                    //排序 找出最小距离
                    if (o1 < o2) -1 //-1代表交换两个数的位置
                    else 1
                })
                val diviateValue = LF[0].toDouble()
                p.playSound(p.location, Sound.CAT_MEOW, 1f, ((2 - diviateValue) * 1).toFloat())
            } catch (ignored: NullPointerException) {
            }
        }
    }
}