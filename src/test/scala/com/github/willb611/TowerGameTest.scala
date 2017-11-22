package com.github.willb611

import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite

class TowerGameTest extends FunSuite with MockFactory {
  test("Current winning color is based on highest number of towers of that color") {
    var towers: List[ProxyTower] = List()
    val winningColor = Color.GREEN
    val nonWinningColor = Color.RED
    towers = makeProxyTowerOfColor(winningColor) :: towers
    towers = makeProxyTowerOfColor(winningColor) :: towers
    towers = makeProxyTowerOfColor(nonWinningColor) :: towers
    val builders: List[Builder] = List()
    val game: TowerGame = new TowerGame(builders, towers)
    // check
    assert(Color.GREEN == game.currentlyWinningColor().get)
  }

  def makeProxyTowerOfColor(color: Color): ProxyTower = {
    val t = new ProxyTower
    t.addBlock(color)
    t.processPendingBlocks()
    t
  }
}