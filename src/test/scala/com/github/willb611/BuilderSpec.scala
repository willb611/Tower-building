package com.github.willb611

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}
import com.github.willb611.humans.Builder
import com.github.willb611.humans.Builder.DoWork
import com.github.willb611.objects.Environment.ApplyEffect
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
      val builder = system.actorOf(Props(new Builder(color)))
      builder ! Builder.TowerToBuild(testProbe.ref)
      builder ! DoWork
      testProbe.expectMsg(waitTime, Tower.AddBlockRequest(color))
      builder ! DoWork
      testProbe.expectMsg(waitTime, Tower.AddBlockRequest(color))
    }

    "do nothing after being hit by lightning" in {
      val testProbe = TestProbe()
      val builder = system.actorOf(Props(new Builder(Color.randomColor())))
      builder ! Builder.TowerToBuild(testProbe.ref)
      builder ! ApplyEffect(EnvironmentEffects.Lightning)
      builder ! DoWork
      testProbe.expectNoMessage(waitTime)
    }
  }

  "A builder given two towers" should {
    "not add to both" in {
      val testColor = Color.BLUE
      val ignoredTestProbe = TestProbe()
      val testProbe = TestProbe()
      val builder = system.actorOf(Props(new Builder(testColor)))
      builder ! Builder.TowerToBuild(ignoredTestProbe.ref)
      builder ! Builder.TowerToBuild(testProbe.ref)
      builder ! DoWork
      ignoredTestProbe.expectNoMessage(waitTime)
      testProbe.expectMsg(waitTime, Tower.AddBlockRequest(testColor))
    }
  }
}