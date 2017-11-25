package com.github.willb611.objects

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
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
      val towerSpace = system.actorOf(Props(new TowerSpace(testProbe.ref, num)))
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
}
