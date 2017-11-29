package com.github.willb611.builders

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.github.willb611.builders.Builder.{TowerBeingBuiltQuery, TowerToBuild}
import com.github.willb611.{Color, GameConfig, GameHost}
import com.github.willb611.GameHost.{TowerSpacesResponse, TowerSpacesQuery}
import com.github.willb611.builders.BuilderCoordinator.{BuilderListResponse, BuildersBeingCoordinatedQuery, TowerListResponse}
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
      Thread.sleep(BuilderCoordinator.BuilderWorkInterval.plus(waitTime).toMillis)
      testProbeAsTower.expectMsgType[AddBlockRequest](waitTime.plus(BuilderCoordinator.BuilderWorkInterval))
      TestKit.shutdownActorSystem(tmpSys)
      receiveOne(waitTime)
    }
  }
  "A coordinator given TowerSpaceAdvisory(TowerSpace)" should {
    "poll TowerSpace for towers" in {
      val testProbeAsTowerSpace = TestProbe()
      val coordinator = system.actorOf(BuilderCoordinator.props(0, Color.randomColor()))
      coordinator ! TowerSpacesResponse(List(testProbeAsTowerSpace.ref))
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
      coordinator ! TowerListResponse(List(tower))
      Thread.sleep(waitTime.toMillis)
      child ! TowerBeingBuiltQuery
      expectMsg(waitTime, Some(tower))
      TestKit.shutdownActorSystem(tmpSys)
    }
  }
  "A coordinator" should {
    "Respond to BuildersBeingCoordinated" in {
//      val dummyParent = system.actorOf(GameHost.props(GameConfig.ZeroValues))
      val num = 1
      val coordinator: ActorRef = system.actorOf(BuilderCoordinator.props(num, Color.randomColor()))
      coordinator ! BuildersBeingCoordinatedQuery
      // Then
      val response: AnyRef = receiveOne(waitTime)
      val responseAsAdvisory = response.asInstanceOf[BuilderListResponse]
      assert(null != responseAsAdvisory)
      assert(num == responseAsAdvisory.builders.size)
    }
  }
}