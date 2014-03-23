package io.github.akechi.sugoicraft
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.event.{Listener,EventHandler}
import org.bukkit.Material

class SugoiPlugin extends JavaPlugin with Listener {
  private val log = this.getLogger()

  override def onEnable() {
    log.info("onEnable")
    Bukkit.getPluginManager().registerEvents(this, this)
    Bukkit.getPluginManager().registerEvents(new BlockEvents(), this)
  }

  val slowBlocks = Set(Material.SAND, Material.GRAVEL)

  @EventHandler
  def onPlayerToggleSprint(evt: org.bukkit.event.player.PlayerToggleSprintEvent) {
    val player = evt.getPlayer
    val loc = player.getLocation.clone.add(0, -1, 0)
    if (evt.isSprinting && slowBlocks.contains(loc.getBlock.getType)) {
      player.setWalkSpeed(0.4F)
    } else {
      player.setWalkSpeed(0.2F)
    }
  }
}
