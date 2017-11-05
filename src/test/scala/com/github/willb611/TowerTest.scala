package com.github.willb611

import org.scalatest.FunSuite

class TowerTest extends FunSuite {
  val greenColor = "green"
  val redColor = "red"

  test("Add block gives tower height") {
    val tower = new Tower
    val newTower = tower.addBlock()
    assert(newTower.height == 1)
  }

  test("Add coloured block saves latest color") {
    val tower = new Tower().addBlock(greenColor)
    assert(tower.lastColor.getOrElse(None) == greenColor)

    val newerTower = tower.addBlock(redColor)
    assert(newerTower.lastColor.getOrElse(None) == redColor)
  }

  test("Lightning environment affect can damage tower") {
    val tower = new Tower().addBlock(redColor)
    tower.environmentEffect(EnvironmentEffects.Lightning)
    assert(tower.height() == 0)
  }

  test("LastColor doesnt fail if empty tower") {
    val tower = new Tower
    assert(tower.lastColor() != null)
  }

  test("Lightning environment affect doesnt apply to empty tower") {
    val tower = new Tower
    tower.environmentEffect(EnvironmentEffects.Lightning)
    assert(tower.height == 0)
  }
}
