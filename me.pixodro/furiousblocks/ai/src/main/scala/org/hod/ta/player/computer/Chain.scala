package org.tsug.hod

import java.awt.Point

/**
 * Developed with pleasure :)<br>
 * @author HamsterofDeath
 * Created 19.01.2010 @ 20:07:27
 */

class Chain(chainData: Array[Point]) extends scala.Iterable[Point]
{
  override def toString = chainData.deep.toString

  override def iterator = chainData iterator
}