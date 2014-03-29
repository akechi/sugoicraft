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
    Bukkit.getPluginManager().registerEvents(new Magic(), this)
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

  @EventHandler
  def onAsyncPlayerChatEvent(evt: org.bukkit.event.player.AsyncPlayerChatEvent) {
    val player = evt.getPlayer()
    val msg = evt.getMessage()

    // TODO
    {
      // TODO evt.getFormat() caused runtime error
      log.info("format: evt.getFormat()")
      val text = "%s: %s".format(player.getName, msg)
      println('text, text)
      // curl -d 'room=mcujm&bot=sugoicraft&text=ujm%20logged%20out.&bot_verifier=bb5060f31bc6e89018c55ac72d39d5ca6aca75c9' "http://lingr.com/api/room/say"
      // http://www.bigbeeconsultants.co.uk/content/bee-client/simple-examples
      // import uk.co.bigbeeconsultants.http._ // omg
      import uk.co.bigbeeconsultants.http.HttpClient
      import uk.co.bigbeeconsultants.http.request.RequestBody
      val resp: String =
        (new HttpClient).
          post(
            new java.net.URL("http://lingr.com/api/room/say"),
            Some(RequestBody(Map(
              "room" -> "mcujm",
              "bot" -> "sugoicraft",
              "text" -> "ujm%20logged%20out.",
              "bot_verifier" -> "bb5060f31bc6e89018c55ac72d39d5ca6aca75c9")))).
          body.asString
      println('resp, resp)
    }
  }
}
