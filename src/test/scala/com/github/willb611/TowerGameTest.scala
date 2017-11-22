package com.github.willb611

import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite


class TowerGameTest extends FunSuite with MockFactory {
  test("Current winning color is based on highest number of towers of that color") {
    var towers: List[Tower] = List()
    val winningColor = Color.GREEN
    val nonWinningColor = Color.RED
    towers = makeTowerOfColor(winningColor) :: towers
    towers = makeTowerOfColor(winningColor) :: towers
    towers = makeTowerOfColor(nonWinningColor) :: towers
    val workers: List[Worker] = List()
    val game: TowerGame = new TowerGame(workers, towers)
    // check
    assert(Color.GREEN == game.currentlyWinningColor())
  }

  def makeTowerOfColor(Color color): Tower = {
    val t:Tower = new Tower
    t.add(color)
    t
  }
}