package io.github.akechi.sugoicraft
import org.bukkit.{Location, Material, World}

object Structs {

  /**
   * Base coordinate interface.
   */
  abstract class Coordinate {

    /**
     * This Struct object size.
     * xMin, xMax, yMin, yMax, zMin, zMax
     */
    val scan: (Int, Int, Int, Int, Int, Int)

    /**
     * Returns block data at givent position.
     */
    def block(x: Float, y:Float, z:Float) :Option[(Material, Byte)]
  }

  /**
   * Sample Ball strcture.
   */
  class Ball(size: Int) extends Coordinate {
    override val scan = (-size, size, -size, size, -size, size)
    override def block(x: Float, y:Float, z:Float) :Option[(Material, Byte)] = {
      val s = x*x + y*y + z*z
      val r = this.size * this.size
      if ( r-5 < s && s < r+5) {
        return Some((Material.SMOOTH_BRICK, 0))
      } else {
        return None
      }
    }
  }

  /**
   * Converts Builder.Coordinate to Int bitmap data.
   */
  def flatten(struct: Coordinate): Map[(Int, Int, Int), (Material, Byte)] = {
    var result: Map[(Int, Int, Int), (Material, Byte)] = Map()
    val (xs, xe, ys, ye, zs, ze) = struct.scan
    for (x <- Range(xs, xe)) {
      for (y <- Range(ys, ye)) {
        for (z <- Range(zs, ze)) {
          val o = struct.block(x.toFloat, y.toFloat, z.toFloat)
          o match {
            case Some((mat, data)) =>
              result = result.updated((x, y, z), (mat, data))
            case _ =>
          }
        }
      }
    }
    return result
  }

  /**
   * Generate Struct from flatten data.
   */
  def generate(world: World, loc: Location, state: Map[(Int, Int, Int), (Material, Byte)]) {
    for ((pos, blockdata) <- state) {
      val (x, y, z) = pos
      val (mat, data) = blockdata
      val replace_loc = new Location(world, loc.getX + x, loc.getY + y, loc.getZ + z)
      val block = world.getBlockAt(replace_loc)
      if (block.getType != Material.BED_BLOCK) {
        block.setType(mat)
        block.setData(data)
      }
    }
  }
}
