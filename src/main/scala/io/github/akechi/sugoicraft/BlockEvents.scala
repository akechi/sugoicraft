package io.github.akechi.sugoicraft
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.event.{Listener,EventHandler}
import org.bukkit.{Material, GameMode}
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack

/**
 * Definition of future item generater qualification and result
 */
case class PendingItem(to: Material=Material.AIR,
  by: Set[Material]=Set(),
  dataFrom: Byte=127,
  dataTo: Byte=0) {

  /** 
   * Drops future item if hand item and block matches future qualification.
   */
  def drop(hand: Material, block: Block): Boolean = {
    if (this.dataFrom != 127 && this.dataFrom != block.getData) {
      return false
    }
    if (!this.by.isEmpty && !this.by.contains(hand)) {
      return false
    }
    block.setType(Material.AIR)
    block.setData(0)
    block.getWorld.dropItemNaturally(
      block.getLocation,
      new ItemStack(this.to, 1, 0.toShort, this.dataTo))
    true
  }
}

/**
 * Called when a block event fired.
 */
class BlockEvents extends Listener {

  /** definition for special brock break events. */
  val damageBlocks = Map(
    Material.GRASS-> PendingItem(
      to=Material.GLASS,
      by=Set(Material.DIRT)),
    Material.LEAVES-> PendingItem(
      to=Material.MELON,
      by=Set(Material.DIRT)),
    Material.SMOOTH_BRICK-> PendingItem(
      to=Material.SMOOTH_BRICK,
      by=Set(Material.STONE_PICKAXE),
      dataFrom=0,
      dataTo=2))

  /**
   * Drops special items by BlockEvents.damageBlocks
   *
   * if damageBlocks contains event.getBlock.getType:
   *
   * * drops PendingItem if matched PendingItem qualification.
   * * Else drop default item. 
   */
  @EventHandler
  def onBlockBreakEvent(evt: org.bukkit.event.block.BlockBreakEvent) {
    val block = evt.getBlock
    val material = block.getType
    if (evt.getPlayer.getGameMode == GameMode.SURVIVAL) {
      if (damageBlocks.contains(material)) {
        val future = damageBlocks(material)
        val hand = evt.getPlayer.getItemInHand.getType
        if (future.drop(hand, block)) {
            evt.setCancelled(true)
          }
      }
    }
  }
}
