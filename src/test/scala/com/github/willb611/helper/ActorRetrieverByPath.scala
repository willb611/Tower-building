package com.github.willb611.helper

import akka.actor.{ActorIdentity, ActorRef, ActorSystem, Identify}
import akka.testkit.TestKit
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._

trait ActorRetrieverByPath extends TestKit with LazyLogging {
  implicit val waitTime: FiniteDuration = 50 milliseconds

  def firstChildFromParentInSystem(coordinator: ActorRef, sys: ActorSystem, childPrefix: String): ActorRef = {
    for (i: Int <- 0 until 5) {
      try {
        sys.actorSelection(coordinator.path./(s"$childPrefix-$i")) ! Identify(None)
        return actorRefFromIdentifyResult(receiveOne(waitTime))
      } catch {
        case e: Exception =>
          logger.debug(s"failed with id $i, trying next..")
      }
    }
    throw new Exception("Failed to find actor child")
  }
  private def actorRefFromIdentifyResult(resultTypeAny: Any): ActorRef = {
    logger.debug("[actorRefFromIdentifyResult] got: {}", resultTypeAny)
    val resultTypeActorIdentity = resultTypeAny.asInstanceOf[ActorIdentity]
    logger.debug("[actorRefFromIdentifyResult] got: {}", resultTypeActorIdentity)
    val actorRefOption = resultTypeActorIdentity.ref
    logger.debug("[actorRefFromIdentifyResult] got: {}", actorRefOption)
    val actorRef = actorRefOption.get
    logger.debug("[actorRefFromIdentifyResult] got: {}", actorRef)
    actorRef
  }
}
