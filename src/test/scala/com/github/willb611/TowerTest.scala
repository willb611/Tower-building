package com.github.willb611

import org.scalatest.FunSuite

class TowerTest extends FunSuite {
  val greenColor = "green"

  test("Add block gives tower height") {
    val tower = new Tower
    val newTower = tower.addBlock()
    assert(newTower.height == 1)
  }

  test("Add coloured block saves latest color") {
    val tower = new Tower().addBlock(greenColor)
    assert(tower.lastColor == greenColor)

    val newerTower = tower.addBlock("red")
    assert(newerTower.lastColor == "red")
  }
}
