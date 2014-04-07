package io.github.akechi.sugoicraft
import org.bukkit.Bukkit
import org.bukkit.{Location, Sound, World}
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.event.{Listener,EventHandler}

/**
 * SuperJump with sneak
 */
class SuperJump extends Listener {
  /** name, (count, timestamp) */
  var crouchingCounter: Map[String, (Int, Long)] = Map()
  
  def getCrouchingCounter(player: Player) : Int = {
    val c = this.crouchingCounter.get(player.getName)
    c match {
      case Some((count, old_time)) =>
        val current_time = System.currentTimeMillis / 1000
        val new_count = count - (current_time - old_time).toInt
        if (new_count < 0) {
          return 0
        } else {
          return new_count
        }
      case _ =>
        return 0
    }
  }

  def incrementCrouchingCounter(player: Player) {
    val current_time = System.currentTimeMillis / 1000
    crouchingCounter = crouchingCounter.updated(
      player.getName,
      (this.getCrouchingCounter(player) + 1, current_time))

  }

  @EventHandler
  def onPlayerToggleSneakEvent(evt: org.bukkit.event.player.PlayerToggleSneakEvent) {
    val player = evt.getPlayer
    if (player.isSneaking) {
      this.incrementCrouchingCounter(player)
      if (this.getCrouchingCounter(player) == 4) {
        player.playSound(
          player.getLocation,
          Sound.BAT_TAKEOFF,
          (1.0).toFloat,
          (0.0).toFloat)
        player.setFallDistance((0.0).toFloat)
        player.setVelocity(player.getVelocity.setY(1.4))
      }
    }
  }
}
