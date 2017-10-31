import org.scalatest.FunSuite

class ScalaTest extends FunSuite {
  test("Add block") {
    val tower = new Tower
    val newTower = tower.addBlock()
    assert(newTower.height == 1)
  }

  test("Add coloured block") {
    val tower = new Tower
    val newTower = tower.addBlock("green")
    assert(newTower.height == 1)
    assert(newTower.lastColor == "green")

    val newerTower = tower.addBlock("red")
    assert(newerTower.lastColor == "red")
  }
}
