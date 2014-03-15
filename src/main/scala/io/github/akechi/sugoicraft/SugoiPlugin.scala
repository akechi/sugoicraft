package io.github.akechi.sugoicraft
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.event.{Listener,EventHandler}

class SugoiPlugin extends JavaPlugin with Listener {
  override def onEnable() {
    println('cool)
    getServer().getPluginManager().registerEvents(this, this)
  }

  @EventHandler
  def onPlayerToggleSprint(evt: org.bukkit.event.player.PlayerToggleSprintEvent) {
    val player = evt.getPlayer
    println(player, evt.isSprinting)
  }
}
