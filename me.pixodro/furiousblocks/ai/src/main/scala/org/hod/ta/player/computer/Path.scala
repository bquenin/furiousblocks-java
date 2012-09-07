package org.tsug.hod

import collection.mutable.ListBuffer

/**
 * Developed with pleasure :)<br>
 * @author HamsterofDeath
 * Created 15.01.2010 @ 10:14:21
 */

class Path extends scala.Ordered[Path] with Iterable[AIBlockSwitch]
{
  private var scoreDelta: Int = -1;

  private val plan: ListBuffer[AIBlockSwitch] = new ListBuffer[AIBlockSwitch]();

  override def head: AIBlockSwitch = {
    if (plan isEmpty) return null
    plan head
  }

  def push(bs: AIBlockSwitch) = {
    plan += bs
  }

  def pop() = {
    plan -= (plan last)
  }

  def compare(other: Path): Int = {
    this.scoreDelta - other.scoreDelta
  }

  def addRating(rating: Int) = {
    scoreDelta += rating
  }

  def iterator: Iterator[AIBlockSwitch] = plan.iterator

  override def size: Int = plan size

  def rating: Int = scoreDelta

  def hasRating: Boolean = rating >= 3
}