package com.github.willb611.humans

import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration.{FiniteDuration, _}
class BuilderCoordinatorSpec(_system: ActorSystem) extends TestKit(_system)
  with Matchers
  with WordSpecLike
  with BeforeAndAfterAll {
  def this() = this(ActorSystem("BuilderCoordinatorSpec"))
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }
  val waitTime: FiniteDuration = 20 milliseconds

  "A builder coordinator" should {
    "Tell a builder to do work" in {
      throw new Exception("TODO")
    }
  }
}
