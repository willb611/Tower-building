package com.github.willb611

import org.scalatest.FunSuite

class BuilderTest extends FunSuite {
  test("Builder adds blocks") {
    val tower = new Tower
    val worker = new Builder(tower)
    val time = 1
    worker.doWork(time)
    assert(tower.height >= 1)

    worker.doWork(time*2)
    assert(tower.height >= 2)
  }

  test("Builder adds blocks of the correct color") {
    val tower = new Tower
    val color = new Color("TEST")
    val worker = new Builder(tower, color)
    val time = 1
    worker.doWork(time)
    assert(tower.lastColor().getOrElse("FAIL") == color)
  }

  test("Many builders means more work") {
    val tower = new Tower
    val w1 = new Builder(tower)
    val w2 = new Builder(tower)
    w1.doWork(1)
    w2.doWork(2)
    assert(tower.height >= 2)
  }

  test("Builder does nothing for at least 1 time after hit by lightning") {
    val tower = new Tower
    val worker = new Builder(tower)
    worker.environmentEffect(EnvironmentEffects.Lightning)
    worker.doWork(1)
    assert(tower.height == 0)
  }
}