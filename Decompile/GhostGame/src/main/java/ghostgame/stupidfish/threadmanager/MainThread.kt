package ghostgame.stupidfish.threadmanager

import ghostgame.stupidfish.GameRun
import ghostgame.stupidfish.gameboard.BoardSender
import ghostgame.stupidfish.setup
import ghostgame.stupidfish.teammanager.Team
import ghostgame.stupidfish.utils.utils
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

object MainThread {
    var FinalStop = 600
    fun StartThread() { //定时结束游戏
        if (setup.is_Game_Start_Timed) {
            object : BukkitRunnable() {
                var time = FinalStop
                override fun run() {
                    if (!setup.is_Game_Start_Timed) {
                        cancel()
                        return
                    }
                    if (Team.GhostTeam.size <= 0) {
                        GameRun.win(Team.HunterTeam, Team.GhostTeam)
                        cancel()
                        return
                    }
                    if (Team.HunterTeam.size <= 0) {
                        GameRun.win(Team.GhostTeam, Team.HunterTeam)
                        cancel()
                        return
                    }
                    FinalStop = time
                    BoardSender.StartedGameBoard()
                    if (time <= 0) {
                        SafeJudgFinalRes()
                        cancel()
                        return
                    }
                    time--
                }
            }.runTaskTimer(setup.instance, 0L, 20L)
        }
    }

    fun SafeJudgFinalRes() {
        if (Team.GhostTeam.size > Team.HunterTeam.size) { //Ghost win
            GameRun.win(Team.GhostTeam, Team.HunterTeam)
        } else if (Team.GhostTeam.size < Team.HunterTeam.size) { // Hunter win
            GameRun.win(Team.HunterTeam, Team.GhostTeam)
        } else {
            EqualsRes() //平局
        }
    }

    fun EqualsRes() {
        utils.SendAllMessage("&4时间到 &6平局!")
        Bukkit.getOnlinePlayers().forEach { player_online: Player ->
            player_online.gameMode = GameMode.SPECTATOR
            utils.clearInv(player_online)
            player_online.world.spawnEntity(player_online.location, EntityType.FIREWORK)
        }
    }
}