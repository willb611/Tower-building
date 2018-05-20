package com.github.willb611.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.RejectionHandler
import akka.http.scaladsl.server.directives.LoggingMagnet
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContextExecutor, Future}

class ApiServer(actorSystem: ActorSystem)
  extends LazyLogging {
  val host = "localhost"
  val port: Int = 8080
  implicit val system: ActorSystem = actorSystem
  implicit val materializer: Materializer = ActorMaterializer()
  implicit def executor: ExecutionContextExecutor = system.dispatcher

  def start() = {
    logger.info(s"[start] Starting ApiServer on interface $host at $port, try http://$host:$port")
    println("println - [start] setting up ApiServer")

    val requestLogging =
      logRequestResult(LoggingMagnet(_ => RequestLogging.logRequestResult)) &
        handleRejections(RejectionHandler.default)

    val routes = (get & path("/")) {

//      val routes = (get & path("echo" / Segment)) {
        //  s =>
      //      val f = Future.successful(s)
      //      complete(f)
      complete(HttpEntity("Hello"))
    }

    val loggingRoutes = requestLogging { routes }

    Http().bindAndHandle(loggingRoutes, host, port)
    logger.info(s"[start] Bound routes $routes with logging: $loggingRoutes.. leaving method")
    logger.info("println - [start] Bound routes.. leaving method")
  }
}
