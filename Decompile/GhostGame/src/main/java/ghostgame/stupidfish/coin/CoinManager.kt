package ghostgame.stupidfish.coin

import ghostgame.stupidfish.setup
import ghostgame.stupidfish.utils.utils
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object CoinManager {
    fun KillReward(p: Player) {
        if (setup.is_Game_Start_Timed) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), setup.kill_Reward!!.replace("%player%", p.name))
            utils.SendMessageRe(p, utils.StringReplace(setup.kill_tip))
        }
    }

    fun TauntReward(p: Player) {
        if (setup.is_Game_Start_Timed) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), setup.taunt_Reward!!.replace("%player%", p.name))
            utils.SendMessageRe(p, utils.StringReplace(setup.taunt_tip))
        }
    }

    fun WinReward(p: Player?) {
        if (setup.is_Game_Start_Timed) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), setup.win_Reward!!.replace("%player%", p!!.name))
            utils.SendMessageRe(p, utils.StringReplace(setup.win_tip))
        }
    }
}