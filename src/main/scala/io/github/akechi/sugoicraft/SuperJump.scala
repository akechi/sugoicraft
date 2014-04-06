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
  var crouching_counter: Map[String, (Int, Long)] = Map()
  
  /** crouching_counter reset interval */
  val interval =  2l

  def getCrouchingCounter(player: Player) : Int = {
    if (!this.crouching_counter.contains(player.getName)) {
      return 0
    } else {
      val current_time = System.currentTimeMillis / 1000
      val (count, old_time) = this.crouching_counter(player.getName)
      if (current_time - old_time > this.interval) {
        return 0
      } else {
        return count
      }
    }
  }

  def incrementCrouchingCounter(player: Player) {
    val current_time = System.currentTimeMillis / 1000
    crouching_counter = crouching_counter.updated(
      player.getName,
      (this.getCrouchingCounter(player) + 1, current_time))

  }

  @EventHandler
  def onPlayerToggleSneakEvent(evt: org.bukkit.event.player.PlayerToggleSneakEvent) {
    val player = evt.getPlayer
    if (player.isSneaking) {
      this.incrementCrouchingCounter(player)
      if (this.getCrouchingCounter(player) > 5) {
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


