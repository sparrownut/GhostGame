package ghostgame.stupidfish.gamesound

import org.bukkit.Sound
import org.bukkit.entity.Player

object GameSoundPlayer {
    fun PlayTimerSound(p: Player) {
        val loc = p.location
        p.world.playSound(loc, Sound.DIG_STONE, 1f, 1f)
    }

    fun PlayStartSound(p: Player) {
        val loc = p.location
        p.world.playSound(loc, Sound.LEVEL_UP, 1f, 1f)
    }

    fun PlayKillSound(p: Player) {
        val loc = p.location
        p.world.playSound(loc, Sound.ENDERDRAGON_HIT, 1f, 1f)
    }

    fun PlayTauntSound(p: Player) {
        val loc = p.location
        p.world.playSound(loc, Sound.CAT_MEOW, 1f, 0.5f)
    }
}