package ghostgame.stupidfish.teammanager

import org.bukkit.entity.Player
import java.util.*

object Team {
    var GhostTeam: MutableList<Player> = LinkedList()
    var HunterTeam: MutableList<Player> = LinkedList()


    fun JudgPlayerInList(player: Player, list: List<Player?>?): Boolean {
        for (p in list!!) {
            if (p!!.name == player.name) {
                return true
            }
        }
        return false
    }

    fun getTeamSize(team: List<Player?>?): Int {
        return team!!.size
    }
}