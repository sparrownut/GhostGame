package ghostgame.stupidfish.prefix

import ghostgame.stupidfish.teammanager.Team
import ghostgame.stupidfish.utils.utils
import org.bukkit.entity.Player
import java.util.function.Consumer

object PrefixManager {
    fun prefixRemove(p: Player) {
        p.isCustomNameVisible = false
        p.displayName = p.name
        p.playerListName = p.name
        p.customName = p.name
    }

    fun prefixAdd() {
        Team.HunterTeam.forEach(Consumer { hunter: Player? ->
            hunter!!.isCustomNameVisible = true
            hunter.displayName = utils.StringReplace("&c[猎人]&a") + hunter.name
            hunter.playerListName = utils.StringReplace("&c[猎人]&a") + hunter.name
            hunter.customName = utils.StringReplace("&c[猎人]&a") + hunter.name
        })
        Team.GhostTeam.forEach(Consumer { ghost: Player? ->
            ghost!!.isCustomNameVisible = true
            ghost.displayName = utils.StringReplace("&b[幽灵]&a") + ghost.name
            ghost.playerListName = utils.StringReplace("&b[幽灵]&a") + ghost.name
            ghost.customName = utils.StringReplace("&b[幽灵]&a") + ghost.name
        })
    }
}