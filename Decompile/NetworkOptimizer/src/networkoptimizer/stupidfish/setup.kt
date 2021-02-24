package networkoptimizer.stupidfish

import networkoptimizer.stupidfish.event.EventManager
import networkoptimizer.stupidfish.utils.BasicUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.plugin.java.JavaPlugin

class setup : JavaPlugin() {
    companion object {
        lateinit var instance: setup
    }
    override fun onEnable() {
        // Plugin startup logic
        instance = this
        BasicUtils().BukkitSendMessageRe("&c&lNetworkOptimizer 网络优化器已经启动")
        BasicUtils().BukkitSendMessageRe("&a 作者-_stupidfish")
        Bukkit.getPluginManager().registerEvents(EventManager(),this)
        BasicUtils().BukkitSendMessageRe("&a&l 事件监听器注册完成")
    }
}