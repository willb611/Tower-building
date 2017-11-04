package com.github.willb611

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

  test("Many workers means more work") {
    val tower = new Tower
    val w1 = new Worker(tower)
    val w2 = new Worker(tower)
    w1.doWork(1)
    w2.doWork(2)
    assert(tower.height >= 2)
  }
}
