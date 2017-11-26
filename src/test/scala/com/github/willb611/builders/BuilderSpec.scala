package com.github.willb611.builders

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import com.github.willb611.builders.Builder.{DoWork, TowerToBuild}
import com.github.willb611.Color
import com.github.willb611.objects.Environment.ApplyEffectCommand
import com.github.willb611.objects.{EnvironmentEffects, Tower}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._

class BuilderSpec(_system: ActorSystem) extends TestKit(_system)
  with Matchers
  with WordSpecLike
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("BuilderSpec"))
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }
  val waitTime: FiniteDuration = 20 milliseconds

  "A builder given a tower" should {
    "adds blocks of the right color" in {
      val color = new Color("TEST")
      val testProbe = TestProbe()
      val builder = system.actorOf(Builder.props(color))
      builder ! TowerToBuild(testProbe.ref)
      builder ! DoWork
      testProbe.expectMsg(waitTime, Tower.AddBlockRequest(color))
      builder ! DoWork
      testProbe.expectMsg(waitTime, Tower.AddBlockRequest(color))
    }

    "do nothing after being hit by lightning" in {
      val testProbe = TestProbe()
      val builder = system.actorOf(Builder.props(Color.randomColor()))
      builder ! TowerToBuild(testProbe.ref)
      builder ! ApplyEffectCommand(EnvironmentEffects.Lightning)
      builder ! DoWork
      testProbe.expectNoMessage(waitTime)
    }
  }

  "A builder given two towers" should {
    "not add to both" in {
      val testColor = Color.BLUE
      val ignoredTestProbe = TestProbe()
      val testProbe = TestProbe()
      val builder = system.actorOf(Builder.props(testColor))
      builder ! TowerToBuild(ignoredTestProbe.ref)
      builder ! TowerToBuild(testProbe.ref)
      builder ! DoWork
      ignoredTestProbe.expectNoMessage(waitTime)
      testProbe.expectMsg(waitTime, Tower.AddBlockRequest(testColor))
    }
  }
}