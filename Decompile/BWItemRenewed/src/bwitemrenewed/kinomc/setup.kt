package bwitemrenewed.kinomc

import bedwarsgun.bedwarsgun.utils.utils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.plugin.java.JavaPlugin

class setup : JavaPlugin() {
    companion object {
        lateinit var instance: setup
        var ExpendBlock = 16
        var BackStrengthSmallLevel:Int = 50
    }

    var util = utils()

    override fun onEnable() {
        // Plugin startup logic
        instance = this
        saveDefaultConfig()

        BackStrengthSmallLevel = config.getInt("BackStrengthSmallLevel")
        InteractEvent.GunList.add(Material.WOOD_HOE)
        InteractEvent.GunList.add(Material.GOLD_HOE)
        InteractEvent.GunList.add(Material.DIAMOND_HOE)
        InteractEvent.DropList.add(EntityType.ARROW)
        InteractEvent.DropList.add(EntityType.ARMOR_STAND)
        Bukkit.getPluginManager().registerEvents(InteractEvent(), this)
        Bukkit.getLogger().info(util.MessageReplace("&cBedwarsGun已经加载"))

    }

    override fun onDisable() {
        Bukkit.getLogger().info(util.MessageReplace("&cBedwarsGun已经卸载"))
        // Plugin shutdown logic
    }
}