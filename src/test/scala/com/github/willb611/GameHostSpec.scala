package com.github.willb611

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import com.github.willb611.GameHost.{BuilderCoordinatorsAdvisory, TowerSpacesAdvisory}
import com.github.willb611.builders.Builder.TowerBeingBuiltQuery
import com.github.willb611.builders.{Builder, BuilderCoordinator}
import com.github.willb611.helper.ActorRetrieverByPath
import com.github.willb611.objects.{Tower, TowerSpace}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class GameHostSpec(_system: ActorSystem) extends TestKit(_system)
  with Matchers
  with WordSpecLike
  with ImplicitSender
  with BeforeAndAfterAll
  with MockFactory
  with ActorRetrieverByPath {
  def this() = this(ActorSystem("GameHostSpec"))
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "GameHost given gameConfig" should {
    "create a game according to config" in {
      val host = system.actorOf(GameHost.props(GameConfig(1, 1, 1, 1)))
      Thread.sleep(waitTime.toMillis)
      val towerSpace = firstChildFromParentInSystem(host, system, TowerSpace.ActorNamePrefix)
      assert(towerSpace != null)
      val coordinator = firstChildFromParentInSystem(host, system, BuilderCoordinator.ActorNamePrefix)
      assert(coordinator != null)
      val builder = firstChildFromParentInSystem(coordinator, system, Builder.ActorNamePrefix)
      assert(builder != null)
      builder ! TowerBeingBuiltQuery
      val towerWhichBuilderIsBuilding: Option[ActorRef] = receiveOne(waitTime).asInstanceOf[Option[ActorRef]]
      assert(towerWhichBuilderIsBuilding != null)
      assert(towerWhichBuilderIsBuilding.isDefined)
      assert(towerWhichBuilderIsBuilding.get.path.toStringWithoutAddress.contains(Tower.ActorNamePrefix))
    }
  }

  "GameHost " should {
    "respond to chaos monkey queries" in {
      val gameHost = system.actorOf(GameHost.props(GameConfig.ZeroValues))
      gameHost ! GameHost.BuilderCoordinatorsQuery
      expectMsg(waitTime, BuilderCoordinatorsAdvisory(List()))
      gameHost ! GameHost.TowerSpacesQuery
      expectMsg(waitTime, TowerSpacesAdvisory(List()))
    }
  }
}
