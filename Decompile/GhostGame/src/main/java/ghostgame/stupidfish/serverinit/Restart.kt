package ghostgame.stupidfish.serverinit

import ghostgame.stupidfish.GameRun
import ghostgame.stupidfish.playerlist.PlayerListManager
import ghostgame.stupidfish.setup
import ghostgame.stupidfish.teammanager.Team
import ghostgame.stupidfish.threadmanager.MainThread
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object Restart {
    fun restart() { //游戏结束时重置服务器
        setup.is_Game_Start_Timed = false //设置游戏结束
        Bukkit.getOnlinePlayers().forEach { p: Player? ->
            p?.let { GoLobby(it) }
        }
        GameRun.is_DeathMode = false
        Team.HunterTeam.clear()
        Team.GhostTeam.clear() //清理两队人员
        PlayerListManager.SwitchGhostBySelf.clear() //自主选择的幽灵的list清理
        PlayerListManager.PlayerTimerList.clear()
        MainThread.FinalStop = 600
    }
}