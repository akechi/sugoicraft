package io.github.akechi.sugoicraft

import org.scalatest.FunSpec
import org.mockito.Mockito._
import org.mockito.Matchers

import org.bukkit.Material
import org.bukkit.entity.{Player, Projectile, Egg, Arrow}

class ParanormalForTest(entity: Projectile) extends Paranormal.Base(entity) {
  def hit(evt: org.bukkit.event.entity.ProjectileHitEvent) {
  }
}

class MagicForTest extends Magic {
  override val dict = 
    Map(Material.TNT -> (10, classOf[Egg], classOf[ParanormalForTest]))
}

class MagicTest extends FunSpec {
  describe("MagicTest") {
    describe("onProjectileHitEvent") {
      describe("no special events when") {
        it("no liveeffects") {
          val proj =  mock(classOf[Egg])
          val magic = new MagicForTest
          val evt = new org.bukkit.event.entity.ProjectileHitEvent(proj)
          magic.onProjectileHitEvent(evt)
        }
        it("no match liveeffects") {
          val player = mock(classOf[Player])
          val egg =  mock(classOf[Egg])
          val arrow =  mock(classOf[Arrow])
          val paranormal = mock(classOf[Paranormal.Base])
          when(paranormal.entity).thenReturn(arrow)
          val magic = new MagicForTest
          magic.liveeffects = Set(paranormal)
          val evt = new org.bukkit.event.entity.ProjectileHitEvent(egg)
          magic.onProjectileHitEvent(evt)
          verify(paranormal, times(0)).hit(evt)
        }
      }
      describe("special events when") {
        it("event Projectile and lieveeffects Projectile matched") {
          val player = mock(classOf[Player])
          val egg =  mock(classOf[Egg])
          val paranormal = mock(classOf[Paranormal.Base])
          when(paranormal.entity).thenReturn(egg)
          val magic = new MagicForTest
          magic.liveeffects = Set(paranormal)
          val evt = new org.bukkit.event.entity.ProjectileHitEvent(egg)
          magic.onProjectileHitEvent(evt)
          verify(paranormal, times(1)).hit(evt)
        }
      }
    }
  }
}
