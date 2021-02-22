package ghostgame.stupidfish.itemutils

import ghostgame.stupidfish.utils.utils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ItemUtils {
    companion object {
        fun givePlayerItemNamed(p: Player, displayName: String, location: Int, material: Material, amount: Int) {
            //以上都是初始化
            val itemstack = ItemStack(material)
            itemstack.amount = amount//设置数量
            val im = itemstack.itemMeta
            im.displayName = utils.StringReplace(displayName)
            itemstack.itemMeta = im
            p.inventory.setItem(location, itemstack)
        }
    }
}