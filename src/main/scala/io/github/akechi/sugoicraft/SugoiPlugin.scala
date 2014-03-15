package io.github.akechi.sugoicraft
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.event.{Listener,EventHandler}

class SugoiPlugin extends JavaPlugin with Listener {
  override def onEnable() {
    println('cool)
    Bukkit.getPluginManager().registerEvents(this, this)
  }

  @EventHandler
  def onPlayerToggleSprint(evt: org.bukkit.event.player.PlayerToggleSprintEvent) {
    val player = evt.getPlayer
    if (evt.isSprinting) {
      player.setWalkSpeed(0.4)
    } else {
      player.setWalkSpeed(0.2)
    }
  }
}
