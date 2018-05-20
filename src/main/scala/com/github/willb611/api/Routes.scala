package com.github.willb611.api

import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives.{complete, get, path}
import akka.http.scaladsl.server.Route
import com.github.willb611.{Color, TowerGame}
import akka.http.scaladsl.server.RouteConcatenation._

trait Routes {
  private val helloWorldRoute: Route = (get & path("")) {
    complete(HttpEntity("Hello world!"))
  }
  private val currentWinnerRoute: Route = (get & path("game")) {
    complete(
      HttpEntity(
        TowerGame.requestCurrentWinner().getOrElse(Color.randomColor()).description.toString
      )
    )
  }

  val serverRoutes = helloWorldRoute ~ currentWinnerRoute
}
