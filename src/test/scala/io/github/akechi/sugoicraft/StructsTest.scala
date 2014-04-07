package io.github.akechi.sugoicraft

import org.scalatest.FunSpec
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.mockito.ArgumentCaptor

import org.bukkit.Material
import org.bukkit.{Location, Material, World}
import org.bukkit.block.Block
import org.bukkit.entity.{Player, Projectile, Egg, Arrow}

class Cube extends Structs.Coordinate {
  val scan = (-1, 1, -1, 1, -1, 1)

  def block(x: Float, y:Float, z:Float) :Option[(Material, Byte)] = {
    return Some((Material.SMOOTH_BRICK, 0))
  }
}

class StructsTest extends FunSpec {
  describe("Structs") {
    describe("flatten") {
      it("Returns cube") {
        val q = Structs.flatten(new Cube)
        for (x <- Range(-1, 1)) {
          for (y <- Range(-1, 1)) {
            for (z <- Range(-1, 1)) {
              assert(q.contains(x, y, z))
            }
          }
        }
      }
    }
    describe("generate") {
      describe("block") {
        it("replaces specified pos") {
          val world = mock(classOf[World])
          val loc = mock(classOf[Location])
          val block = mock(classOf[Block])
          when(loc.getX).thenReturn((1).toFloat)
          when(loc.getY).thenReturn((1).toFloat)
          when(loc.getZ).thenReturn((1).toFloat)
          when(block.getType).thenReturn(Material.AIR)
          when(world.getBlockAt(any[Location])).thenReturn(block)
          val d = Map(
            (1, 0, 0) -> (Material.SMOOTH_BRICK, 0.toByte))
          Structs.generate(world, loc, d)
          val repace_loc = ArgumentCaptor.forClass(classOf[Location])
          verify(world, times(1)).getBlockAt(repace_loc.capture)
          assert((1+1).toFloat == repace_loc.getValue.getX)
          assert((1).toFloat == repace_loc.getValue.getY)
          assert((1).toFloat == repace_loc.getValue.getZ)
          verify(block, times(1)).setType(Material.SMOOTH_BRICK)
          verify(block, times(1)).setData(0.toByte)
        }
        it("does not replace if target block is BED_BLOCK") {
          val world = mock(classOf[World])
          val loc = mock(classOf[Location])
          val block = mock(classOf[Block])
          when(loc.getX).thenReturn((1).toFloat)
          when(loc.getY).thenReturn((1).toFloat)
          when(loc.getZ).thenReturn((1).toFloat)
          when(block.getType).thenReturn(Material.BED_BLOCK)
          when(world.getBlockAt(any[Location])).thenReturn(block)
          val d = Map(
            (1, 0, 0) -> (Material.SMOOTH_BRICK, 0.toByte))
          Structs.generate(world, loc, d)
          val repace_loc = ArgumentCaptor.forClass(classOf[Location])
          verify(world, times(1)).getBlockAt(repace_loc.capture)
          assert((1+1).toFloat == repace_loc.getValue.getX)
          assert((1).toFloat == repace_loc.getValue.getY)
          assert((1).toFloat == repace_loc.getValue.getZ)
          verify(block, times(0)).setType(Material.SMOOTH_BRICK)
          verify(block, times(0)).setData(0.toByte)
        }
      }
    }
  }
}
