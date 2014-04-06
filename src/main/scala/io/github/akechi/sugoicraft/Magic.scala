package io.github.akechi.sugoicraft
import scala.collection.JavaConversions.collectionAsScalaIterable
import org.bukkit.{Bukkit, Sound}
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

  /** player magicka value and timestamp */
  var magicka = Map[String, (Int, Long)]()

  val magicka_max = 100

  val dict: Map[Material, (Int, Class[_ <: Projectile], Class[_ <: Paranormal.Base])] =
    Map(
      Material.LAVA_BUCKET -> (20, classOf[org.bukkit.entity.Egg], classOf[Paranormal.Fire]),
      Material.TNT-> (30, classOf[org.bukkit.entity.Egg], classOf[Paranormal.Destruction]))

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
   * Get current magicka value.
   */
  def getMagicka(player: Player): Int = {
    if (!this.magicka.contains(player.getName)) {
      return this.magicka_max
    } else {
      val (old_magicka, old_timestamp) = this.magicka(player.getName)
      val new_magicka = old_magicka + (System.currentTimeMillis / 1000 - old_timestamp).toInt
      if (new_magicka > this.magicka_max) {
        return this.magicka_max
      }
      return new_magicka
    }
  }

  /**
   * decrement magicka value.
   */
  def decrementMagicka(player: Player, magicka: Int) {
    val current = System.currentTimeMillis / 1000
    val old_magicka = this.getMagicka(player)
    val new_magicka = old_magicka - magicka
    this.magicka = this.magicka.updated(player.getName, (new_magicka, current))
  }

  /**
   * Handler to call effect.
   */
  @EventHandler
  def onPlayerInteractEvent(evt: org.bukkit.event.player.PlayerInteractEvent) {
    if (!evt.hasBlock && evt.hasItem) {
      if (this.dict.contains(evt.getItem.getType)) {
        val player = evt.getPlayer
        val e = this.dict(evt.getItem.getType)
        val (cost, style, effect) = e
        val current_magicka = this.getMagicka(player)
        if (current_magicka >= cost) {
          this.effect(player, style, effect)
          this.decrementMagicka(player, cost)
        } else {
          player.playSound(player.getLocation, Sound.ENDERMAN_TELEPORT, (50.0).toFloat, (2.0).toFloat)
          player.sendMessage("low magicka [%d/%d]".format(current_magicka, this.magicka_max))
        }
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
