package com.github.willb611

import akka.actor.{ActorKilledException, OneForOneStrategy, SupervisorStrategy}
import akka.actor.SupervisorStrategy._
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import scala.language.postfixOps

case class RestartKilledSupervisionStrategy(superSupervisionStrategy: SupervisorStrategy)
  extends LazyLogging {
  val strategy: SupervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
    case e: ActorKilledException =>
      logger.info(s"Actor died from ${e.getMessage}, restarting..", e)
      Restart
    case t =>
      superSupervisionStrategy.decider.applyOrElse(t, (_: Any) => Escalate)
  }
}