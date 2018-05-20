package com.github.willb611.api

import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.github.willb611.Color
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class RoutesSpec
  extends WordSpecLike
  with Matchers
  with BeforeAndAfterAll
  with MockFactory
  with ScalatestRouteTest
  with Routes {

  override implicit val gameService: GameService = mock[GameService]

  "HelloWorldRoute" should {
    "Return hello" in {
      Get() ~> serverRoutes ~> check {
        responseAs[String] shouldEqual "Hello world!"
      }
    }
    "Return description of current winner" in {
      // When
      val redDescription = Color.RED.description
      (gameService.getWinningColor _).expects().returning(redDescription).atLeastOnce()
      // Then
      Get("/game") ~> serverRoutes ~> check {
        responseAs[String] shouldEqual redDescription
      }
    }
  }
}
