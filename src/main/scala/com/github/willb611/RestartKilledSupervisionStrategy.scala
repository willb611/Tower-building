//package com.github.willb611
//
//import akka.actor.OneForOneStrategy
//import akka.actor.SupervisorStrategy.Escalate
//
//class RestartKilledSupervisionStrategy extends OneForOneStrategy {
////= OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
//
//  case _: ActorKill => Resume
//  case t =>
//  super.supervisorStrategy.decider.applyOrElse(t, (_: Any) => Escalate)
//
//}
