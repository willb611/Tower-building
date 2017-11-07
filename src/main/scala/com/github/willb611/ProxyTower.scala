package com.github.willb611

class ProxyTower extends Tower {
  private var blockBuffer: List[Color] = List()

  override def addBlock(str: Color): Tower = {
    blockBuffer = str :: blockBuffer
    this
  }

  def processPendingBlocks(): Unit = {
    val colorToPlace = highestPercentColorFromBlockList(blockBuffer)
    if (colorToPlace.nonEmpty) {
      blockBuffer = List()
      super.addBlock(colorToPlace.get)
    }
  }
}
