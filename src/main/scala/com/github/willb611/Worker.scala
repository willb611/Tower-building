package com.github.willb611

object Worker {
  def nextColor(): Color = {
    return new Color
  }
}

class Worker(val tower: Tower, val colorToUseForBlocks: Color) {
  def this(tower: Tower) = this(tower, Worker.nextColor())

  def doWork(time: Int) = {
    for (workDone <- 0 until time) {
      tower.addBlock(colorToUseForBlocks)
    }
  }
}
