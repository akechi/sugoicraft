package io.github.akechi.sugoicraft

import org.scalatest.FunSpec
import org.mockito.Mockito._
import org.mockito.Matchers
import org.mockito.ArgumentCaptor

import org.bukkit.entity.Player
import org.bukkit.{Material, GameMode, World, Location}
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack

class BlockEventsTest extends FunSpec {
  describe("BlockEventsTest") {
    describe("onBlockBreakEvent") {
      describe("event does not cancelled") {
        it ("if target block type does not matched.") {
          val block = mock(classOf[Block])
          val player = mock(classOf[Player])
          val evt = new org.bukkit.event.block.BlockBreakEvent(block, player)
          when(block.getType).thenReturn(Material.SAND)
          when(player.getGameMode).thenReturn(GameMode.SURVIVAL)
          val blockEvents = new BlockEvents()
          blockEvents.onBlockBreakEvent(evt)
          assert(false === evt.isCancelled)
        }
      }
      describe("drop items and event cancelled") {
        it ("if target block type, data, and hand material matched") {
          val block = mock(classOf[Block])
          val blockLocation = mock(classOf[Location])
          val player = mock(classOf[Player])
          val evt = new org.bukkit.event.block.BlockBreakEvent(block, player)
          val playerHandStack = mock(classOf[ItemStack])
          val world = mock(classOf[World])
          when(player.getGameMode).thenReturn(GameMode.SURVIVAL)
          when(block.getType).thenReturn(Material.SMOOTH_BRICK)
          when(player.getItemInHand).thenReturn(playerHandStack)
          when(playerHandStack.getType).thenReturn(Material.STONE_PICKAXE)
          when(block.getWorld).thenReturn(world)
          when(block.getLocation).thenReturn(blockLocation)
          val blockEvents = new BlockEvents()
          blockEvents.onBlockBreakEvent(evt)
          val itemStackCaptor = ArgumentCaptor.forClass(classOf[ItemStack])
          verify(world, times(1)).dropItemNaturally(
            Matchers.eq(blockLocation),
            itemStackCaptor.capture)
          //assert(new ItemStack(Material.SMOOTH_BRICK, 1, 0.toShort, 2.toByte).isSimilar(itemStackCaptor.getValue))
          assert(Material.SMOOTH_BRICK === itemStackCaptor.getValue.getType)
          assert(true === evt.isCancelled)
        }
      }
    }
  }
}
