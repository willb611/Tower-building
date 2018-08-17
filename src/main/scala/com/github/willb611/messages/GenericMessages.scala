package com.github.willb611.messages

import akka.util.Timeout

import scala.concurrent.duration._
import scala.language.postfixOps

object GenericMessages {
  val defaultMessageTimeout: Timeout = Timeout(10 seconds)
  // Queries
  final case object StateQuery extends Query
  final case class WinningColorQuery(maxTimeout: Timeout = defaultMessageTimeout) extends Query
}
