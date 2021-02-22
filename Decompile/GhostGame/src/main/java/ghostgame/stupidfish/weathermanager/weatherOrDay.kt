package ghostgame.stupidfish.weathermanager

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.weather.WeatherChangeEvent

class weatherOrDay : Listener {
    @EventHandler
    fun onWeatherChange(event: WeatherChangeEvent) {
        event.isCancelled = true
    }
}