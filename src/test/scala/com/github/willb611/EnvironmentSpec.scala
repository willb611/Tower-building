package com.github.willb611

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import com.github.willb611.objects.Environment.{ActorJoinEnvironmentAdvisory, ApplyEffectCommand, ApplyEffectsToActors}
import com.github.willb611.objects.{Environment, EnvironmentEffects}
import com.typesafe.scalalogging.LazyLogging
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration.{FiniteDuration, _}
import scala.util.Random
import scala.language.{higherKinds, postfixOps}

class EnvironmentSpec extends TestKit(ActorSystem("EnvironmentSpec"))
  with Matchers
  with WordSpecLike
  with BeforeAndAfterAll
  with MockFactory
  with LazyLogging {
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }
  val waitTime: FiniteDuration = 150 milliseconds
  val effectInterval: FiniteDuration = 1 milliseconds

  "Environment given an actor, and timing, and random which returns true" should {
    "Apply a random effect to the actor after the given timing" in {
      val randomMock: Random = mock[Random]
      (randomMock.nextBoolean _).expects().returning(true).anyNumberOfTimes()
      val testProbe = TestProbe()
      val environment = system.actorOf(Environment.props(randomMock, effectInterval))
      environment ! ActorJoinEnvironmentAdvisory(testProbe.ref)
      testProbe.expectMsg(waitTime, ApplyEffectCommand(EnvironmentEffects.Lightning))
    }
    "Apply effect to given actor when commanded" in {
      val randomMock: Random = mock[Random]
      (randomMock.nextBoolean _).expects().returning(true).anyNumberOfTimes()
      val testProbe = TestProbe()
      val environment = system.actorOf(Environment.props(randomMock, effectInterval))
      environment ! ActorJoinEnvironmentAdvisory(testProbe.ref)
      environment ! ApplyEffectsToActors
      testProbe.expectMsg(waitTime, ApplyEffectCommand(EnvironmentEffects.Lightning))
    }
  }
}