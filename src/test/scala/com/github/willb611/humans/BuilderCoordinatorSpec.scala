package com.github.willb611.humans

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration._
class BuilderCoordinatorSpec(_system: ActorSystem) extends TestKit(_system)
  with Matchers
  with WordSpecLike
  with BeforeAndAfterAll {
  def this() = this(ActorSystem("BuilderCoordinatorSpec"))
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }
  val waitTime: FiniteDuration = 20 milliseconds
//
//  "A builderCoordinator" should {
//    "Instruct builders to work" in {
//      val probe = TestProbe()
//      val builder = system.actorOf(Props(classOf[Builder], probe.ref))
//
//    }
//  }
}
