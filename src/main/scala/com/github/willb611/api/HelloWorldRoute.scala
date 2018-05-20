package com.github.willb611.api

import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.server.Directives.{complete, get, path}
import akka.http.scaladsl.server.Route

trait HelloWorldRoute {

  val helloWorldRoute: Route = (get & path("")) {
    complete(HttpEntity("Hello world!"))
  }
}
