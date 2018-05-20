package com.github.willb611.api

import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives.{complete, get, path}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.RouteConcatenation._

trait Routes {
  implicit val gameService: GameService

  private val helloWorldRoute: Route = (get & path("")) {
    complete(HttpEntity("Hello world!"))
  }

  private val currentWinnerRoute: Route = (get & path("game")) {
    complete(
      HttpEntity(
        gameService.getWinningColor
      )
    )
  }

  val serverRoutes: Route = helloWorldRoute ~ currentWinnerRoute
}
