package com.github.willb611

import akka.actor.{ActorSystem, Kill}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.github.willb611.ChaosMonkey.{CauseChaos, QueryForVictims}
import com.github.willb611.GameHost.{BuilderCoordinatorsQuery, TowerSpacesQuery}
import com.github.willb611.builders.BuilderCoordinator.TowerListAdvisory
import com.github.willb611.helper.ActorRetrieverByPath
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.util.Random

class ChaosMonkeySpec(_system: ActorSystem) extends TestKit(_system)
  with Matchers
  with WordSpecLike
  with ImplicitSender
  with BeforeAndAfterAll
  with MockFactory
  with ActorRetrieverByPath {
  def this() = this(ActorSystem("ChaosMonkeySpec"))
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A chaos monkey given a rigged random" should {
    "Always kill something" in {
      val watcher = TestProbe()
      val probe = TestProbe()
      val victim = probe.ref
      watcher.watch(victim)
      val randomMock = mock[Random]
      (randomMock.nextBoolean _).expects().returning(true).atLeastOnce()
      val monkey = system.actorOf(ChaosMonkey.props(randomMock))
      monkey ! TowerListAdvisory(List(victim))
      monkey ! CauseChaos
      watcher.expectTerminated(victim, waitTime)
    }
    "Kill something on a regular schedule" in {
      val watcher = TestProbe()
      val probe = TestProbe()
      val victim = probe.ref
      watcher.watch(victim)
      val randomMock = mock[Random]
      (randomMock.nextBoolean _).expects().returning(true).atLeastOnce()
      val monkey = system.actorOf(ChaosMonkey.props(randomMock))
      monkey ! TowerListAdvisory(List(victim))
      watcher.expectTerminated(victim, waitTime.plus(ChaosMonkey.IntervalForChaos))
    }
  }

  "A chaosMonkey" should {
    "poll parent for victims" in {
      val parent = TestProbe()
      val monkey = parent.childActorOf(ChaosMonkey.props(new Random))
      monkey ! QueryForVictims
      parent.expectMsg(waitTime, TowerSpacesQuery)
      parent.expectMsg(waitTime, BuilderCoordinatorsQuery)
    }
  }
}
