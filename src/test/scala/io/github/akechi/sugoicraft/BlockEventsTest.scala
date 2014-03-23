package io.github.akechi.sugoicraft

import org.scalatest.FunSpec
import org.mockito.Mockito._
import org.mockito.Matchers
import org.mockito.ArgumentCaptor

import org.bukkit.entity.Player
import org.bukkit.{Material, GameMode, World, Location}
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack

class BlockEventsForTest extends BlockEvents {
  /** definition for special brock break events. */
  override val damageBlocks = Map(
    Material.GRASS -> PendingItem(
      Material.GLASS),
    Material.LEAVES -> PendingItem(
      Material.MELON,
      by = Set(Material.DIRT)),
    Material.SMOOTH_BRICK -> PendingItem(
      Material.SMOOTH_BRICK,
      by = Set(Material.STONE_PICKAXE),
      dataFrom = 1,
      dataTo = 2))
}

class BlockEventsTest extends FunSpec {
  describe("BlockEventsTest") {
    describe("onBlockBreakEvent") {
      describe("event did not cancelled") {
        it ("if player is not in survival.") {
          val block = mock(classOf[Block])
          val player = mock(classOf[Player])
          val evt = new org.bukkit.event.block.BlockBreakEvent(block, player)
          when(block.getType).thenReturn(Material.GRASS)
          when(player.getGameMode).thenReturn(GameMode.CREATIVE)
          val blockEvents = new BlockEventsForTest()
          blockEvents.onBlockBreakEvent(evt)
          assert(!evt.isCancelled)
        }
        it ("if target block type does not matched.") {
          val block = mock(classOf[Block])
          val player = mock(classOf[Player])
          val evt = new org.bukkit.event.block.BlockBreakEvent(block, player)
          when(block.getType).thenReturn(Material.SAND)
          when(player.getGameMode).thenReturn(GameMode.SURVIVAL)
          val blockEvents = new BlockEventsForTest()
          blockEvents.onBlockBreakEvent(evt)
          assert(!evt.isCancelled)
        }
        it ("if hand item type does not matched.") {
          val targetMaterial = Material.LEAVES
          val handMaterial = Material.STONE_PICKAXE
          val block = mock(classOf[Block])
          val player = mock(classOf[Player])
          val evt = new org.bukkit.event.block.BlockBreakEvent(block, player)
          val playerHandStack = mock(classOf[ItemStack])
          when(block.getType).thenReturn(targetMaterial)
          when(player.getGameMode).thenReturn(GameMode.SURVIVAL)
          when(player.getItemInHand).thenReturn(playerHandStack)
          when(playerHandStack.getType).thenReturn(handMaterial)
          val blockEvents = new BlockEventsForTest()
          blockEvents.onBlockBreakEvent(evt)
          assert(!evt.isCancelled)
        }
        it ("if target block data does not matched.") {
          val targetMaterial = Material.SMOOTH_BRICK
          val targetData = 0.toByte
          val handMaterial = Material.STONE_PICKAXE
          val block = mock(classOf[Block])
          val player = mock(classOf[Player])
          val evt = new org.bukkit.event.block.BlockBreakEvent(block, player)
          val playerHandStack = mock(classOf[ItemStack])
          when(block.getType).thenReturn(targetMaterial)
          when(block.getData).thenReturn(targetData)
          when(player.getGameMode).thenReturn(GameMode.SURVIVAL)
          when(player.getItemInHand).thenReturn(playerHandStack)
          when(playerHandStack.getType).thenReturn(handMaterial)
          val blockEvents = new BlockEventsForTest()
          blockEvents.onBlockBreakEvent(evt)
          assert(!evt.isCancelled)
        }
      }
      describe("dropped items and event cancelled") {
        it ("if target block type matched") {
          val targetMaterial = Material.GRASS
          val handMaterial = Material.STONE_PICKAXE
          val block = mock(classOf[Block])
          val blockLocation = mock(classOf[Location])
          val player = mock(classOf[Player])
          val evt = new org.bukkit.event.block.BlockBreakEvent(block, player)
          val playerHandStack = mock(classOf[ItemStack])
          val world = mock(classOf[World])
          when(player.getGameMode).thenReturn(GameMode.SURVIVAL)
          when(block.getType).thenReturn(targetMaterial)
          when(player.getItemInHand).thenReturn(playerHandStack)
          when(playerHandStack.getType).thenReturn(handMaterial)
          when(block.getWorld).thenReturn(world)
          when(block.getLocation).thenReturn(blockLocation)
          val blockEvents = new BlockEventsForTest()
          blockEvents.onBlockBreakEvent(evt)
          val itemStackCaptor = ArgumentCaptor.forClass(classOf[ItemStack])
          verify(world, times(1)).dropItemNaturally(
            Matchers.eq(blockLocation),
            itemStackCaptor.capture)
          assert(Material.GLASS === itemStackCaptor.getValue.getType)
          assert(evt.isCancelled)
        }
        it ("if target block type, data, and hand material matched") {
          val targetMaterial = Material.SMOOTH_BRICK
          val targetData = 1.toByte
          val handMaterial = Material.STONE_PICKAXE
          val block = mock(classOf[Block])
          val blockLocation = mock(classOf[Location])
          val player = mock(classOf[Player])
          val evt = new org.bukkit.event.block.BlockBreakEvent(block, player)
          val playerHandStack = mock(classOf[ItemStack])
          val world = mock(classOf[World])
          when(player.getGameMode).thenReturn(GameMode.SURVIVAL)
          when(block.getType).thenReturn(targetMaterial)
          when(block.getData).thenReturn(targetData)
          when(player.getItemInHand).thenReturn(playerHandStack)
          when(playerHandStack.getType).thenReturn(handMaterial)
          when(block.getWorld).thenReturn(world)
          when(block.getLocation).thenReturn(blockLocation)
          val blockEvents = new BlockEventsForTest()
          blockEvents.onBlockBreakEvent(evt)
          val itemStackCaptor = ArgumentCaptor.forClass(classOf[ItemStack])
          verify(world, times(1)).dropItemNaturally(
            Matchers.eq(blockLocation),
            itemStackCaptor.capture)
          //assert(new ItemStack(Material.SMOOTH_BRICK, 1, 0.toShort, 2.toByte).isSimilar(itemStackCaptor.getValue))
          assert(Material.SMOOTH_BRICK === itemStackCaptor.getValue.getType)
          assert(evt.isCancelled)
        }
      }
    }
  }
}
