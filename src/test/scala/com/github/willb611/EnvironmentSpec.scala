package com.github.willb611

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}
import com.github.willb611.objects.Environment.{ActorJoinEnvironmentAdvisory, ApplyEffectCommand, ApplyEffectsToActors}
import com.github.willb611.objects.{Environment, EnvironmentEffects}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration.{FiniteDuration, _}
import scala.util.Random

class EnvironmentSpec(_system: ActorSystem) extends TestKit(_system)
  with Matchers
  with WordSpecLike
  with BeforeAndAfterAll
  with MockFactory {
  def this() = this(ActorSystem("EnvironmentSpec"))
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }
  val waitTime: FiniteDuration = 50 milliseconds
  val effectInterval: FiniteDuration = 1 milliseconds

  "Environment given an actor, and timing, and random which returns true" should {
    "Apply a random effect to the actor after the given timing" in {
      val randomMock = mock[Random]
      (randomMock.nextBoolean _).expects().returning(true).atLeastOnce()
      val testProbe = TestProbe()
      val environment = system.actorOf(Environment.props(randomMock, effectInterval))
      environment ! ActorJoinEnvironmentAdvisory(testProbe.ref)
      testProbe.expectMsg(waitTime, ApplyEffectCommand(EnvironmentEffects.Lightning))
    }
    "Apply effect to given actor when commanded" in {
      val randomMock = mock[Random]
      (randomMock.nextBoolean _).expects().returning(true).atLeastOnce()
      val testProbe = TestProbe()
      val environment = system.actorOf(Environment.props(randomMock, effectInterval))
      environment ! ActorJoinEnvironmentAdvisory(testProbe.ref)
      environment ! ApplyEffectsToActors
      testProbe.expectMsg(waitTime, ApplyEffectCommand(EnvironmentEffects.Lightning))
    }
  }
}