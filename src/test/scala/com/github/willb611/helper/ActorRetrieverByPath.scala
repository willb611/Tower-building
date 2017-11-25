package com.github.willb611.helper

import akka.actor.{ActorIdentity, ActorRef, ActorSystem, Identify}
import akka.testkit.{TestKit, TestProbe}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._

trait ActorRetrieverByPath extends TestKit with LazyLogging {
  implicit val waitTime: FiniteDuration = 50 milliseconds

  def firstChildFromParentInSystem(parent: ActorRef, sys: ActorSystem, childPrefix: String): ActorRef = {
    val probe = TestProbe()
    for (i: Int <- 0 until 5) {
      val path = parent.path./(s"$childPrefix-$i")
      try {
        sys.actorSelection(path).tell(Identify(None), probe.ref)
        logger.debug(s"[firstChildFromParentInSystem] maybe found one at path $path")
        return actorRefFromIdentifyResult(probe.receiveOne(waitTime))
      } catch {
        case e: Exception =>
          logger.debug(s"[firstChildFromParentInSystem] failed with path $path, trying next..")
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
