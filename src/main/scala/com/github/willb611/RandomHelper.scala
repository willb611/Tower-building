package com.github.willb611

import scala.util.Random

object RandomHelper {
  val random: Random = new Random()

  def randomFrom[T](list: List[T]): Option[T] = {
    if (list.isEmpty) {
      None
    } else {
      val index: Int = random.nextInt(list.length)
      Some(list(index))
    }
  }
  def randomFrom[T](set: Set[T]): Option[T] = {
    if (set.isEmpty) {
      None
    } else {
      randomFrom(set.toList)
    }
  }
}
