package com.github.willb611

import org.scalatest.FunSuite

class BuilderTest extends FunSuite {
  test("Builder adds blocks") {
    val tower = new Tower
    val worker = new Builder(tower :: List())
    val time = 1
    worker.doWork(time)
    assert(tower.height >= 1)

    worker.doWork(time*2)
    assert(tower.height >= 2)
  }

  test("Builder adds blocks of the correct color") {
    val tower = new Tower
    val color = new Color("TEST")
    val worker = new Builder(tower :: List(), color)
    val time = 1
    worker.doWork(time)
    assert(tower.lastColor().getOrElse("FAIL") == color)
  }

  test("Many builders means more work") {
    val tower = new Tower
    val w1 = new Builder(tower :: List())
    val w2 = new Builder(tower :: List())
    w1.doWork(1)
    w2.doWork(2)
    assert(tower.height >= 2)
  }

  test("Builder does nothing for at least 1 time after hit by lightning") {
    val tower = new Tower
    val worker = new Builder(tower :: List())
    worker.environmentEffect(EnvironmentEffects.Lightning)
    worker.doWork(1)
    assert(tower.height == 0)
  }

  test("When builder has choice of towers, only adds blocks to 1 tower") {
    val t1 = new Tower
    val t2 = new Tower
    val towers: List[Tower] = List(t1, t2)
    val builder = new Builder(towers)
    val workDone = 1
    builder.doWork(workDone)
    var count = 0
    towers.foreach(count += _.height)
    assert(count == workDone)
  }
}