package com.github.willb611.builders

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.github.willb611.Color
import com.github.willb611.builders.Builder.{TowerBeingBuiltQuery, TowerToBuild}
import com.github.willb611.builders.BuilderCoordinator.TowerSpaceAdvisory
import com.github.willb611.helper.ActorRetrieverByPath
import com.github.willb611.objects.Tower
import com.github.willb611.objects.Tower.AddBlockRequest
import com.github.willb611.objects.TowerSpace.TowersInSpaceQuery
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration.{FiniteDuration, _}

class BuilderCoordinatorSpec(_system: ActorSystem) extends TestKit(_system)
  with Matchers
  with WordSpecLike
  with BeforeAndAfterAll
  with ImplicitSender
  with LazyLogging
  with ActorRetrieverByPath {
  def this() = this(ActorSystem("BuilderCoordinatorSpec"))
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }
  override val waitTime: FiniteDuration = 20 milliseconds

  "A builder parent" should {
    "Tell a builder to do work" in {
      val tmpSys = ActorSystem("TmpSys-tell-builder-do-work")
      val testProbeAsTower = TestProbe()
      val coordinator = tmpSys.actorOf(BuilderCoordinator.props(1, Color.randomColor()))
      val child = firstChildFromParentInSystem(coordinator, tmpSys, Builder.ActorNamePrefix)
      child ! TowerToBuild(testProbeAsTower.ref)
      Thread.sleep(200)
      testProbeAsTower.expectMsgType[AddBlockRequest](waitTime.plus(BuilderCoordinator.BuilderWorkInterval))
      TestKit.shutdownActorSystem(tmpSys)
      receiveOne(waitTime)
    }
  }
  "A parent given TowerSpaceAdvisory(TowerSpace)" should {
    "poll TowerSpace for towers" in {
      val testProbeAsTowerSpace = TestProbe()
      val coordinator = system.actorOf(BuilderCoordinator.props(0, Color.randomColor()))
      coordinator ! TowerSpaceAdvisory(testProbeAsTowerSpace.ref)
      testProbeAsTowerSpace.expectMsg(waitTime, TowersInSpaceQuery)
    }
    "forward one tower from response, onto tower" in {
      val tmpSys = ActorSystem("TmpSys-forward-one-tower")
      val tower = tmpSys.actorOf(Props[Tower])
      val coordinator = tmpSys.actorOf(BuilderCoordinator.props(1, Color.randomColor()))
      val child = firstChildFromParentInSystem(coordinator, tmpSys, Builder.ActorNamePrefix)
      logger.debug(s"Found child: $child")
      child ! TowerBeingBuiltQuery
      expectMsg(waitTime, None)
      coordinator ! List(tower)
      Thread.sleep(200)
      child ! TowerBeingBuiltQuery
      expectMsg(waitTime, Some(tower))
      TestKit.shutdownActorSystem(tmpSys)
    }
  }
}
