package ghostgame.stupidfish.gameitem

import ghostgame.stupidfish.playerlist.PlayerListManager.Companion.switchGhost
import ghostgame.stupidfish.setup
import ghostgame.stupidfish.utils.utils
import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

object FEATHER_ITEM {
    fun All_Feather_Click(event: PlayerInteractEvent) {
        val p = event.player
        if (event.item != null && !setup.is_Game_Start_Timed) {
            if (event.item.type == Material.FEATHER) {
                switchGhost(p)
                event.player.itemInHand = ItemStack(Material.AIR, 1)
                utils.FlushPGhost()
            }
        }
    }
}