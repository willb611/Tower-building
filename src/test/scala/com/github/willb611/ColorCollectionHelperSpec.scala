package com.github.willb611

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.github.willb611.ColorCollectionHelper.CountOfColors
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class ColorCollectionHelperSpec(_system: ActorSystem) extends TestKit(_system)
  with Matchers
  with WordSpecLike
  with BeforeAndAfterAll
  with MockFactory {
  def this() = this(ActorSystem("ColorCollectionHelperSpec"))
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Empty CountOfColors" should {
    "Not throw exception" in {
      val colors = CountOfColors.EmptyCount
      assert(colors.highestPercentColor().isEmpty)
      assert(colors.map != null)
      assert(colors.sum() == 0)
    }
    "Return an empty collection when updating with empty collection" in {
      val colors = CountOfColors.EmptyCount
      assert(colors.updateWith(CountOfColors.EmptyCount) == CountOfColors.EmptyCount)
    }
  }

  "colorCountFromList" should {
    "Not throw exception when given empty list" in {
      val result = ColorCollectionHelper.colorCountFromList(List())
      assert(result == CountOfColors.EmptyCount)
    }
  }
}
