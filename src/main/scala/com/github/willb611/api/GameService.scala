package com.github.willb611.api

import com.github.willb611.{Color, GameState, TowerGame}
import com.typesafe.scalalogging.LazyLogging
import spray.json._
import JsonProtocolWithGameStates._

class GameService
  extends LazyLogging {

  def getWinningColor: String = {
    val winner = TowerGame.requestCurrentWinner().getOrElse(Color.RED)
    val resultString = winner.description.toString
    logger.debug(s"[getWinningColor] Returning $resultString")
    resultString
  }

  def getGameState: String = {
    val stateOption: Option[GameState] = TowerGame.getGameStateSnapshot()
    val state: GameState = stateOption.getOrElse(GameState(List()))
    val result: String = state.toJson.compactPrint
    logger.debug(s"[getGameState] Returning $result")
    result
  }
}
