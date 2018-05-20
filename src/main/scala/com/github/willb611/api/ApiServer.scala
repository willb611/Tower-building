package com.github.willb611.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.RejectionHandler
import akka.http.scaladsl.server.directives.LoggingMagnet
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContextExecutor

class ApiServer(actorSystem: ActorSystem)
  extends LazyLogging
  with Routes {

  val host = "localhost"
  val port: Int = 8080
  override implicit val gameService: GameService = new GameService
  implicit val system: ActorSystem = actorSystem
  implicit val materializer: Materializer = ActorMaterializer()
  implicit def executor: ExecutionContextExecutor = system.dispatcher

  def start() = {
    logger.info(s"[start] Starting ApiServer on http://$host:$port")
    println("println - [start] setting up ApiServer")

    val requestLogging =
      logRequestResult(LoggingMagnet(_ => RequestLogging.logRequestResult)) &
        handleRejections(RejectionHandler.default)

    val loggingRoutes = requestLogging { serverRoutes }

    Http().bindAndHandle(loggingRoutes, host, port)
    logger.info(s"[start] Bound routes leaving method")
    logger.info("println - [start] Bound routes.. leaving method")
  }
}
