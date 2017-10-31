


class Tower(val blocks: List[String]) {
  val defaultColor = "FFFFFF"
  def this() = this(List())

  def height() = blocks.length
  def lastColor() = blocks.head

  def addBlock(str: String) = {
    new Tower(blocks ++ List(str))
  }
  def addBlock(): Tower = {
    addBlock(defaultColor)
  }
}
