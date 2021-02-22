package ghostgame.stupidfish.threadmanager

import ghostgame.stupidfish.setup
import org.bukkit.Bukkit

object ThreadManager {
    @JvmStatic
    fun runtasklater(latertime: Long, R: Runnable?) {
        Bukkit.getScheduler().runTaskLater(setup.instance, R, latertime)
    }
}