package com.github.willb611

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import com.github.willb611.ChaosMonkey.{CauseChaos, ChaosMonkeyConfig, QueryForVictims}
import com.github.willb611.GameHost.{BuilderCoordinatorsAdvisory, BuilderCoordinatorsQuery, TowerSpacesAdvisory, TowerSpacesQuery}
import com.github.willb611.builders.BuilderCoordinator.{BuilderListAdvisory, BuildersBeingCoordinatedQuery, TowerListAdvisory}
import com.github.willb611.helper.ActorRetrieverByPath
import com.github.willb611.objects.TowerSpace.TowersInSpaceQuery
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random

class ChaosMonkeySpec(_system: ActorSystem) extends TestKit(_system)
  with WordSpecLike
  with BeforeAndAfterAll
  with MockFactory
  with ActorRetrieverByPath {
  def this() = this(ActorSystem("ChaosMonkeySpec"))
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  val shortFiniteDuration: FiniteDuration = 10 millis
  val shortDurationConfig = ChaosMonkeyConfig(shortFiniteDuration, shortFiniteDuration)

  "A chaos monkey given a rigged random" should {
    "Always kill something" in {
      val watcher = TestProbe()
      val probe = TestProbe()
      val victim = probe.ref
      // When
      val randomMock = mock[Random]
      watcher.watch(victim)
      (randomMock.nextBoolean _).expects().returning(true).atLeastOnce()
      val monkey = system.actorOf(ChaosMonkey.props(randomMock))
      monkey ! TowerListAdvisory(List(victim))
      monkey ! CauseChaos
      // Then
      watcher.expectTerminated(victim, waitTime)
    }
    "Kill something on a regular schedule" in {
      val watcher = TestProbe()
      val probe = TestProbe()
      val victim = probe.ref
      watcher.watch(victim)
      val randomMock = mock[Random]
      (randomMock.nextBoolean _).expects().returning(true).atLeastOnce()
      // When
      val monkey = system.actorOf(ChaosMonkey.props(randomMock, shortDurationConfig))
      monkey ! TowerListAdvisory(List(victim))
      // Then
      watcher.expectTerminated(victim, waitTime)
    }
  }

  "A non-violent chaosMonkey" should {
    "poll parent for victims" in {
      val ignoreAllRandom: Random = mock[Random]
      logger.info(s"Found random: $ignoreAllRandom")
      (ignoreAllRandom.nextBoolean _).expects().returning(false).anyNumberOfTimes()
      val parent = TestProbe()
      val monkey = parent.childActorOf(ChaosMonkey.props(ignoreAllRandom))
      monkey ! QueryForVictims
      parent.expectMsg(waitTime, TowerSpacesQuery)
      parent.expectMsg(waitTime, BuilderCoordinatorsQuery)
    }
    "poll towerSpace's for towers" in {
      val ignoreAllRandom: Random = mock[Random]
      logger.info(s"Found random: $ignoreAllRandom")
      (ignoreAllRandom.nextBoolean _).expects().returning(false).anyNumberOfTimes()
      val towerSpace = TestProbe()
      val monkey = system.actorOf(ChaosMonkey.props(ignoreAllRandom, shortDurationConfig))
      // When
      monkey ! TowerSpacesAdvisory(List(towerSpace.ref))
      // Then
      towerSpace.expectMsg(waitTime, TowersInSpaceQuery)
    }
    "poll BuilderCoordinator for builders" in {
      val ignoreAllRandom: Random = mock[Random]
      logger.info(s"Found random: $ignoreAllRandom")
      (ignoreAllRandom.nextBoolean _).expects().returning(false).anyNumberOfTimes()
      val coordinator = TestProbe()
      val monkey = system.actorOf(ChaosMonkey.props(ignoreAllRandom, shortDurationConfig))
      // When
      monkey ! BuilderCoordinatorsAdvisory(List(coordinator.ref))
      // Then
      coordinator.expectMsg(waitTime, BuildersBeingCoordinatedQuery)
    }
  }
}
