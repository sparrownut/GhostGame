package ghostgame.stupidfish.gameboard

import ghostgame.stupidfish.threadmanager.ThreadManager.runtasklater
import ghostgame.stupidfish.utils.utils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import java.util.function.Consumer

object ScoreBoardManager {
    @JvmStatic
    fun SendBoardToOnlines(SL: MutableList<String>) {
        runtasklater(1L, Runnable {
            Bukkit.getOnlinePlayers().forEach { players: Player ->
                val scoreboard = Bukkit.getScoreboardManager().newScoreboard
                scoreboard.getObjective(DisplaySlot.SIDEBAR)
                val obj = scoreboard.registerNewObjective("main", "dummy")
                obj.displaySlot = DisplaySlot.SIDEBAR
                obj.displayName = utils.StringReplace("&b&l幽灵战争")
                SL.forEach(Consumer { line: String ->
                    val s = obj.getScore(utils.StringReplace(line))
                    s.score = SL.lastIndexOf(line)
                })
                players.scoreboard = scoreboard
            }
        })
    }

    @JvmStatic
    fun SendBoardToPlayer(player: Player, SL: MutableList<String>) {
        runtasklater(1L, Runnable {
            val scoreboard = Bukkit.getScoreboardManager().newScoreboard
            scoreboard.getObjective(DisplaySlot.SIDEBAR)
            val obj = scoreboard.registerNewObjective("main", "dummy")
            obj.displaySlot = DisplaySlot.SIDEBAR
            obj.displayName = utils.StringReplace("&b&l幽灵战争")
            SL.forEach(Consumer { line: String ->
                val s = obj.getScore(utils.StringReplace(line))
                s.score = SL.lastIndexOf(line)
            })
            player.scoreboard = scoreboard
        })
    }
}