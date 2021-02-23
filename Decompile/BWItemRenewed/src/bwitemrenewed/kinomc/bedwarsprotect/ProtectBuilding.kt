package bwitemrenewed.kinomc.bedwarsprotect

import java.util.ArrayList


import io.github.bedwarsrel.game.Game
import io.github.bedwarsrel.game.Team
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import java.util.function.Consumer
import io.github.bedwarsrel.game.TeamColor

class ProtectBuilding {
    fun placeBedProtect(game: Game, team: Team, loc1: Location, loc2: Location): Boolean {
        val world: World = loc1.world
        val bedProtectFirst = ArrayList<Location>()
        listOf(loc1, loc2).forEach(Consumer { loc: Location ->
            bedProtectFirst.add(Location(world, loc.x, loc.y, loc.z - 1.0))
            bedProtectFirst.add(Location(world, loc.x, loc.y, loc.z + 1.0))//z轴两个防护
            bedProtectFirst.add(Location(world, loc.x, loc.y + 1.0, loc.z))//纵轴一个
            bedProtectFirst.add(Location(world, loc.x - 1.0, loc.y, loc.z))
            bedProtectFirst.add(Location(world, loc.x + 1.0, loc.y, loc.z))//x轴两个防护
        })//添加第1层的每个方块位置
        val bedProtectSecond = ArrayList<Location>()
        bedProtectFirst.forEach(Consumer { loc: Location ->
            bedProtectSecond.add(Location(world, loc.x, loc.y, loc.z - 1.0))
            bedProtectSecond.add(Location(world, loc.x, loc.y, loc.z + 1.0))
            bedProtectSecond.add(Location(world, loc.x, loc.y + 1.0, loc.z))
            bedProtectSecond.add(Location(world, loc.x - 1.0, loc.y, loc.z))
            bedProtectSecond.add(Location(world, loc.x + 1.0, loc.y, loc.z))

        })//添加第2层的每个方块位置
        bedProtectFirst.forEach(Consumer { block: Location ->//第一层木头
            if(block.block.type == Material.AIR) {
                block.block.type = Material.WOOD
                game.region.addPlacedBlock(block.block, null)//设置可破坏
            }
        })
        bedProtectSecond.forEach(Consumer { block: Location ->//第二层羊毛
            if(block.block.type == Material.AIR) {
                block.block.type = Material.WOOL
                block.block.setData(returnTeamColorID(team.color))//设置颜色成队伍
                game.region.addPlacedBlock(block.block, null)
            }
        })
        return true
    }
    private fun returnTeamColorID(teamcolor: TeamColor): Byte {
        return when(teamcolor){
            TeamColor.GREEN -> 5
            TeamColor.YELLOW -> 4
            TeamColor.BLUE -> 3
            TeamColor.RED -> 14
            TeamColor.AQUA -> 9
            TeamColor.BLACK -> 15
            TeamColor.GRAY -> 7
            else -> 100
        }

    }
}