package com.github.willb611.objects

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.github.willb611.Color
import com.github.willb611.humans.Builder
import com.github.willb611.objects.Environment.ActorJoinEnvironmentAdvisory
import com.github.willb611.objects.TowerSpace.TowersInSpaceQuery
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration.{FiniteDuration, _}

class TowerSpaceSpec(_system: ActorSystem) extends TestKit(_system)
  with Matchers
  with WordSpecLike
  with ImplicitSender
  with BeforeAndAfterAll
  with MockFactory {
  def this() = this(ActorSystem("EnvironmentSpec"))
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }
  val waitTime: FiniteDuration = 50 milliseconds

  "TowerSpace given some environment and some number of towers" should {
    "send ActorJoinEnvironmentAdvisory to environment for each, and make correct number" in {
      val testProbe = TestProbe()
      val num = 4
      val towerSpace = system.actorOf(TowerSpace.props(testProbe.ref, num))
      // <num> messages sent to environment
      val messagesSentToEnv = testProbe.receiveN(num, waitTime)
      // check class
      val expectedClass = ActorJoinEnvironmentAdvisory(testProbe.ref).getClass
      assert(expectedClass == messagesSentToEnv.head.getClass)

      // check towerSpace can return created towers
      towerSpace ! TowersInSpaceQuery
      val responseFromTowerSpec = receiveOne(waitTime)
      val towerListCast = responseFromTowerSpec.asInstanceOf[List[ActorRef]]
      assert(num == towerListCast.size)
    }
  }

  "TowerSpace receiving ActorJoinEnvironmentAdvisory" should {
    "forward to environment" in {
      val testProbeAsEnvironment = TestProbe()
      val towerSpace = system.actorOf(TowerSpace.props(testProbeAsEnvironment.ref, 0))
      val dummyActor = system.actorOf(Builder.props(Color.GREEN))
      val advisory = ActorJoinEnvironmentAdvisory(dummyActor)
      towerSpace ! advisory
      testProbeAsEnvironment.expectMsg(waitTime, advisory)
    }
  }
}
