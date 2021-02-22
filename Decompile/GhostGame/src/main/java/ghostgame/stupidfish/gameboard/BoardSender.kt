package ghostgame.stupidfish.gameboard

import ghostgame.stupidfish.gameboard.ScoreBoardManager.SendBoardToOnlines
import ghostgame.stupidfish.gameboard.ScoreBoardManager.SendBoardToPlayer
import ghostgame.stupidfish.playerlist.PlayerListManager
import ghostgame.stupidfish.setup
import ghostgame.stupidfish.teammanager.Team
import ghostgame.stupidfish.threadmanager.MainThread
import ghostgame.stupidfish.utils.utils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object BoardSender {
    fun JoinGameGameBoard(p: Player?) {
        val SB = StringBuilder()
        SB.append("&6(")
        SB.append(PlayerListManager.PlayerTimerList.size)
        SB.append("/")
        SB.append(setup.maxPlayer)
        SB.append(")")
        val line: MutableList<String> = LinkedList()
        line.add("")
        line.add("&b地图:&7" + setup.mapName)
        line.add("&b等待中:&2$SB ")
        line.add("&b概率:&7" + utils.Player_Ghost_Probability[p] + "% ")
        line.add(" ")
        line.add(" ")
        line.add("&b游玩愉快!")
        line.add("&emc.kinomc.cn")
        line.reverse()
        SendBoardToOnlines(line)
    }

    fun TimerStartGameGameBoard(p: Player?, Second: Int) {
        val SB = StringBuilder()
        SB.append("&6(")
        SB.append(PlayerListManager.PlayerTimerList.size)
        SB.append("/")
        SB.append(setup.maxPlayer)
        SB.append(")")
        val line: MutableList<String> = LinkedList()
        line.add("")
        line.add("&b地图:&7" + setup.mapName)
        line.add("&b等待中:&2$SB ")
        line.add("&b还有" + Second + "秒开始 ")
        line.add("&b概率:&7" + utils.Player_Ghost_Probability[p] + "% ")
        line.add(" ")
        line.add(" ")
        line.add("&b游玩愉快!")
        line.add("&emc.kinomc.cn")
        line.reverse()
        SendBoardToOnlines(line)
    }

    fun StartedGameBoard() {
        Bukkit.getOnlinePlayers().forEach { p: Player ->
            val SB = StringBuilder()
            SB.append(" &6(")
            SB.append(PlayerListManager.PlayerTimerList.size)
            SB.append("/")
            SB.append(setup.maxPlayer)
            SB.append(")")
            val line: MutableList<String> = LinkedList()
            line.add("")
            var id = ""
            if (Team.JudgPlayerInList(p, Team.GhostTeam)) {
                id = "&a幽灵"
            } else if (Team.JudgPlayerInList(p, Team.HunterTeam)) {
                id = "&4猎人"
            }
            line.add("&b地图:&7" + setup.mapName)
            line.add("&b您的身份为:$id")
            line.add(" ")
            line.add("&b游戏结束:" + MainThread.FinalStop)
            line.add("&b幽灵:&e" + Team.GhostTeam.size)
            line.add("&b猎人:&e" + Team.HunterTeam.size)
            line.add("&b游玩愉快!")
            line.add("&emc.kinomc.cn")
            Collections.reverse(line)
            SendBoardToPlayer(p, line)
        }
    }
}