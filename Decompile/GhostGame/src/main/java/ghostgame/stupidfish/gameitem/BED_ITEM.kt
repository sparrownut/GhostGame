package ghostgame.stupidfish.gameitem

import ghostgame.stupidfish.serverinit.GoLobby
import ghostgame.stupidfish.setup
import ghostgame.stupidfish.utils.utils
import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent

class BED_ITEM {
    fun All_Bed_Click(event: PlayerInteractEvent) {
        val p = event.player
        val items = event.item
        items.type = Material.AIR//消耗
        if (!setup.is_Game_Start_Timed)
            GoLobby(p)
        else
            utils.SendMessageRe(p, "&4您已开始游戏 理应没有此按钮 请反馈bug")
    }

}