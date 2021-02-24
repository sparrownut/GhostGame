package networkoptimizer.stupidfish.event

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class EventManager : Listener{
    @EventHandler
    fun onJoin(event:PlayerJoinEvent){
        Bukkit.getOnlinePlayers().forEach {
            player ->
            if(!event.player.name.equals(player.name)){//如果不是自己
                event.player.hidePlayer(player)//使自己不能看到别人
            }
        }
    }
}