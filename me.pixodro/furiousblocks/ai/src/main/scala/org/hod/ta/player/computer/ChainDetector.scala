package org.tsug.hod

import java.awt.Point
import collection.mutable.{ListBuffer}

/**
 * Developed with pleasure :)<br>
 * @author HamsterofDeath
 * Created 15.01.2010 @ 11:26:25
 */

class ChainDetector(board: TetrisBoard, ignoreZeroLine: Boolean)
{
  def isPartOfChain(x: Int, y: Int): Boolean =
    {
      countHorizontalLineElements(x, y) >= 3 || countVerticalLineElements(x, y) >= 3
    }

  private def countLineElements(beginX: Int, beginY: Int, xDir: Int, yDir: Int): Int = {
    var x = beginX + xDir;
    var y = beginY + yDir;
    var equalElements = 0;
    val blockType = board blockTypeAt (beginX, beginY)
    while (board.hasCoords(x, y) && board.blockTypeAt(x, y) == blockType && board.canMoveBlockAt(x, y) && (!ignoreZeroLine || y > 0))
      {
        x += xDir
        y += yDir
        equalElements += 1;
      }
    equalElements
  }

  private def countVerticalLineElements(x: Int, y: Int): Int = {
    countLineElements(x, y, 0, -1) + countLineElements(x, y, 0, 1) + 1
  }

  private def countHorizontalLineElements(x: Int, y: Int): Int = {
    countLineElements(x, y, 1, 0) + countLineElements(x, y, -1, 0) + 1
  }

  def findAllChains: Seq[Chain] = {
    val ret = new ListBuffer[Chain]
    val beginY = if (ignoreZeroLine) 1 else 0
    for (x <- 0 until board.sizeX;
         y <- beginY until board.maxReachableY - 1)
      {
        if (isPartOfChain(x, y))
          {
            ret += findChainAt(x, y)
          }
      }
    ret
  }

  private def findChainAt(x: Int, y: Int): Chain =
    {
      var chainData = new ListBuffer[Point]
      if (countHorizontalLineElements(x, y) >= 3)
        {
          new Chain((for (x0 <- x - countLineElements(x, y, -1, 0) to x + countLineElements(x, y, 1, 0)) yield new Point(x0, y)).toArray)
        }
      else if (countVerticalLineElements(x, y) >= 3)
        {
          new Chain((for (y0 <- y - countLineElements(x, y, 0, -1) to y + countLineElements(x, y, 0, 1)) yield new Point(x, y0)).toArray)
        }
      else throw new IllegalArgumentException
    }
}