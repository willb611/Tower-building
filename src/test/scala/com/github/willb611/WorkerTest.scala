package com.github.willb611

import org.scalatest.FunSuite

class WorkerTest extends FunSuite {
  test("com.github.willb611.Worker adds blocks") {
    val tower = new Tower
    val worker = new Worker(tower)
    val time = 1
    worker.doWork(time)
    assert(tower.height >= 1)
  }
}
