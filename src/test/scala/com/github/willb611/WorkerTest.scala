package com.github.willb611

import com.github.willb611.EnvironmentEffects.EnvironmentEffect
import org.scalatest.FunSuite

class WorkerTest extends FunSuite {
  test("Worker adds blocks") {
    val tower = new Tower
    val worker = new Worker(tower)
    val time = 1
    worker.doWork(time)
    assert(tower.height >= 1)

    worker.doWork(time*2)
    assert(tower.height >= 2)
  }

  test("Worker adds blocks of the correct color") {
    val tower = new Tower
    val color = new Color("TEST")
    val worker = new Worker(tower, color)
    val time = 1
    worker.doWork(time)
    assert(tower.lastColor.getOrElse("FAIL") == color)
  }

  test("Many workers means more work") {
    val tower = new Tower
    val w1 = new Worker(tower)
    val w2 = new Worker(tower)
    w1.doWork(1)
    w2.doWork(2)
    assert(tower.height >= 2)
  }

  test("Worker does nothing for at least 1 time after hit by lightning") {
    val tower = new Tower
    val worker = new Worker(tower)
    worker.environmentEffect(EnvironmentEffects.Lightning)
    worker.doWork(1)
    assert(tower.height == 0)
  }
}