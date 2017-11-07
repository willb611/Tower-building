package com.github.willb611

import org.scalatest.FunSuite

class TowerTest extends FunSuite {
  private val green = Color.GREEN
  private val red = Color.RED

  test("Add block gives tower height") {
    val tower = new Tower
    val newTower = tower.addBlock()
    assert(newTower.height == 1)
  }

  test("Add coloured block saves latest color") {
    val tower = new Tower().addBlock(green)
    assert(tower.lastColor().getOrElse(None) == green)

    val newerTower = tower.addBlock(red)
    assert(newerTower.lastColor().getOrElse(None) == red)
  }

  test("Lightning environment affect can damage tower") {
    val tower = new Tower().addBlock(red)
    tower.environmentEffect(EnvironmentEffects.Lightning)
    assert(tower.height() == 0)
  }

  test("LastColor doesn't fail if empty tower") {
    val tower = new Tower
    assert(tower.lastColor() != null)
  }

  test("Lightning environment affect doesn't apply to empty tower") {
    val tower = new Tower
    tower.environmentEffect(EnvironmentEffects.Lightning)
    assert(tower.height == 0)
  }

  test("highestPercentColor should return color which has most number of blocks"
    + " in the tower") {
    val tower = new Tower
    tower.addBlock(red)
    tower.addBlock(red)
    tower.addBlock(green)
    assert(tower.highestPercentColor().getOrElse(green) == red)
  }
  test("highestPercentColor on empty tower shouldn't fail"
    + " in the tower") {
    val tower = new Tower
    assert(tower.highestPercentColor().isEmpty)
  }
}
