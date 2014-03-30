package io.github.akechi.sugoicraft
import scala.collection.JavaConversions.collectionAsScalaIterable
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.event.{Listener,EventHandler}
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.entity.{Player, Projectile}


object Paranormal {
  abstract class Base(val entity: Projectile) {
    def hit(evt: org.bukkit.event.entity.ProjectileHitEvent)
  }

  class Fire(entity: Projectile) extends Base(entity) {
    override def hit(evt: org.bukkit.event.entity.ProjectileHitEvent) {
      // TODO meiraka: all entities including items? - ujihisa
      val near = this.entity.getNearbyEntities(6.0, 6.0, 6.0)
      for (i <- near) {
        println(i)
        i.setFireTicks(120)
      }
    }
  }

  class Destruction(entity: Projectile) extends Base(entity) {
    override def hit(evt: org.bukkit.event.entity.ProjectileHitEvent) {
      val loc = this.entity.getLocation
      this.entity.getWorld.createExplosion(loc.getX, loc.getY, loc.getZ, 2, false, false)
    }
  }
}

/**
 * Add Magic method to player.
 */
class Magic extends Listener {

  /** effect in world. */
  var liveeffects = Set[Paranormal.Base]()

  val dict: Map[Material, (Class[_ <: Projectile], Class[_ <: Paranormal.Base])] =
    Map(
      Material.LAVA_BUCKET -> (classOf[org.bukkit.entity.Egg], classOf[Paranormal.Fire]),
      Material.TNT-> (classOf[org.bukkit.entity.Egg], classOf[Paranormal.Destruction]))

  /**
   * create new effect by player.
   */
  def effect(player: Player, style: Class[_ <: Projectile], effect: Class[_ <: Paranormal.Base]) {
    val proj = player.launchProjectile(
      style,
      player.getEyeLocation.getDirection.multiply(2))
    liveeffects += effect.getConstructor(classOf[Projectile]).newInstance(proj)
  }

  /**
   * Handler to call effect.
   */
  @EventHandler
  def onPlayerInteractEvent(evt: org.bukkit.event.player.PlayerInteractEvent) {
    if (!evt.hasBlock && evt.hasItem) {
      if (this.dict.contains(evt.getItem.getType)) {
        val e = this.dict(evt.getItem.getType)
        val (style, effect) = e
        this.effect(
          evt.getPlayer, style, effect)
        evt.setCancelled(true)
      }
    }
  }

  /**
   * Handler to call Effects.Effect#hit(evt) when effects hit any entity.
   */
  @EventHandler
  def onProjectileHitEvent(evt: org.bukkit.event.entity.ProjectileHitEvent) {
    for (liveeffect <- this.liveeffects) {
      if (liveeffect.entity == evt.getEntity) {
        liveeffect.hit(evt)
        this.liveeffects -= liveeffect
        return
      }
    }
  }
}
