package com.github.willb611.objects

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.github.willb611.Color.{GREEN, RED}
import com.github.willb611.builders.Builder
import com.github.willb611.builders.Builder.{DoWork, TowerToBuild}
import com.github.willb611.objects.Environment.{ActorJoinEnvironmentAdvisory, ApplyEffectCommand}
import com.github.willb611.objects.Tower._
import com.github.willb611.ColorCollectionHelper
import com.github.willb611.Color
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration.{FiniteDuration, _}
import scala.language.postfixOps

class TowerSpec(_system: ActorSystem) extends TestKit(_system)
  with Matchers
  with WordSpecLike
  with BeforeAndAfterAll
  with ImplicitSender {
  def this() = this(ActorSystem("TowerSpec"))
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }
  val waitTime: FiniteDuration = 50 milliseconds

  "A tower given a block" should {
    "increase in height after receiving processPendingBlocks message" in {
      val tower = system.actorOf(Props[Tower])
      addBlockToTower(Color.randomColor(), tower)
      tower ! ProcessPendingBlocksCommand
      tower ! HeightQuery
      expectMsg(waitTime, 1)
    }
    "Save latest color" in {
      val tower = system.actorOf(Props[Tower])
      addBlockToTower(GREEN, tower)
      tower ! LastColorQuery
      expectMsg(waitTime, Option(GREEN))

      addBlockToTower(RED, tower)
      tower ! LastColorQuery
      expectMsg(waitTime, Option(RED))
    }
    "Lose height when affected by lightning" in {
      val tower = system.actorOf(Props[Tower])
      addBlockToTower(Color.randomColor(), tower)
      tower ! HeightQuery
      expectMsg(waitTime, 1)
      tower ! ApplyEffectCommand(EnvironmentEffects.Lightning)
      tower ! HeightQuery
      expectMsg(waitTime, 0)
    }
    "return correct color count" in {
      val tower = system.actorOf(Props[Tower])
      addBlockToTower(RED, tower)
      addBlockToTower(RED, tower)
      addBlockToTower(GREEN, tower)
      tower ! HeightQuery
      expectMsg(waitTime, 3)
      tower ! CountCountQuery
      expectMsg(waitTime, ColorCollectionHelper.CountOfColors(Map(RED -> 2, GREEN -> 1)))
    }
    "report builder which placed block to parent given in constructor" in {
      val testProbe = TestProbe()
      val builder = system.actorOf(Builder.props(GREEN))
      val tower = system.actorOf(Tower.props(testProbe.ref))
      tower.tell(AddBlockRequest(GREEN), builder)
      testProbe.expectMsg(ActorJoinEnvironmentAdvisory(builder))
    }
  }

  "A tower without blocks added" should {
    "Not fail when LastColorQuery is sent" in {
      val tower = system.actorOf(Props[Tower])
      tower ! LastColorQuery
      expectMsg(waitTime, None)
    }
    "Lightning environment affect doesn't apply to empty tower" in {
      val tower = system.actorOf(Props[Tower])
      tower ! ApplyEffectCommand(EnvironmentEffects.Lightning)
      tower ! HeightQuery
      expectMsg(waitTime, 0)
    }
    "Provide CountCountQuery" in {
      val tower = system.actorOf(Props[Tower])
      tower ! CountCountQuery
      expectMsg(waitTime, ColorCollectionHelper.CountOfColors(Map()))
    }
  }


  "A tower given multiple blocks" should {
    "Not add any until it's told to, then only add 1" in {
      val tower = system.actorOf(Props[Tower])
      val b1 = system.actorOf(Builder.props(Color.GREEN))
      b1 ! TowerToBuild(tower)
      b1 ! DoWork
      val b2 = system.actorOf(Builder.props(Color.GREEN))
      b2 ! TowerToBuild(tower)
      b2 ! DoWork
      Thread.sleep(100)
      tower ! HeightQuery
      expectMsg(waitTime, 0)
      tower ! ProcessPendingBlocksCommand
      tower ! HeightQuery
      expectMsg(waitTime, 1)
    }
    "Add blocks over time according to some timer" in {
      val tower = system.actorOf(Props[Tower])
      tower ! HeightQuery
      expectMsg(waitTime, 0)
      tower ! AddBlockRequest(Color.GREEN)
      tower ! AddBlockRequest(Color.GREEN)
      Thread.sleep((2 * Tower.processBlocksInterval).toMillis)
      tower ! HeightQuery
      expectMsg(waitTime, 1)
    }
  }

  private def addBlockToTower(color: Color, tower: ActorRef) {
    tower ! AddBlockRequest(color)
    tower ! ProcessPendingBlocksCommand
  }
}