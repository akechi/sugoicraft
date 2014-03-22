package io.github.akechi.sugoicraft
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.event.{Listener,EventHandler}
import org.bukkit.Material
import org.bukkit.GameMode
import org.bukkit.inventory.ItemStack

class BlockEvents(plugin:JavaPlugin) extends Listener {
  Bukkit.getPluginManager().registerEvents(this, plugin)

  val damageBlocks = Map(Material.GRASS->Material.GLASS,
                         Material.LEAVES->Material.MELON)

  @EventHandler
  def onBlockBreakEvent(evt: org.bukkit.event.block.BlockBreakEvent) {
    val block = evt.getBlock
    val material = block.getType
    if (evt.getPlayer.getGameMode == GameMode.SURVIVAL) {
      if (damageBlocks.contains(material)) {
        val itemstack = new ItemStack(damageBlocks.apply(material))
        block.getWorld.dropItemNaturally(block.getLocation, itemstack)
        block.setType(Material.AIR)
        evt.setCancelled(true)
      }
    }
  }
}
