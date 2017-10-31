package com.github.willb611


class Worker(val tower: Tower) {
  def doWork(time: Int) = {
    for (workDone <- 0 until time) {
      println(workDone)
      tower.addBlock("blah")
    }
    println("time: " + time)
  }
}
