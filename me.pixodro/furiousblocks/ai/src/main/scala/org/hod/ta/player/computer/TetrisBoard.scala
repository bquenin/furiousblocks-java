package org.tsug.hod

import java.awt.Point
import com.furiousblocks.game._

/**
 * Developed with pleasure :)<br>
 * @author HamsterofDeath
 * Created 14.01.2010 @ 21:17:50
 */

class TetrisBoard(ps: PanelSituation)
{
  private var scoreDelta: Int = 0;

  def allPossibleMoves: Iterable[AIBlockSwitch] =
    {
      (for (x <- 0 until sizeX - 1;
            y <- 1 until maxReachableY - 1
            if canDoSwitchAt(x, y))
      yield new AIBlockSwitch(x, y, this))
    }

  private def canDoSwitchAt(x: Int, y: Int): Boolean =
    {
      val canMoveLeft = canMoveBlockAt(x, y)
      val canMoveRight = canMoveBlockAt(x + 1, y)
      (canMoveLeft && canMoveRight) ||
         (canMoveLeft && isEmptyAt(x + 1, y)) ||
         (canMoveRight && isEmptyAt(x, y))
    }

  private def isEmptyAt(x: Int, y: Int): Boolean =
    {
      ps.getBlocksSituation()(x)(y) == null
    }


  def sizeX: Int = {
    ps.getBlocksSituation length
  }

  def maxReachableY: Int = {
    Panel.Y_DISPLAY + 1
  }

  def sizeY: Int = {
    ps.getBlocksSituation.head.length
  }

  def applyMove(bs: AIBlockSwitch) = {
    val rating = bs getThisFor this executeVirtual;
    scoreDelta += rating;
  }

  override def clone: TetrisBoard = {
    new TetrisBoard(ps.getSimpleClone)
  }

  def hasCoords(x: Int, y: Int): Boolean = {
    x >= 0 && y >= 0 && x < sizeX && y < maxReachableY
  }

  def blockTypeAt(x: Int, y: Int): BlockType = {
    val bs = ps.getBlocksSituation()(x)(y)
    if (bs == null) null else bs getType
  }

  def canMoveBlockAt(x: Int, y: Int): Boolean = {
    val bsLeft = ps.getBlocksSituation()(x)(y)

    if (bsLeft == null)
      false
    else
      bsLeft.getState == BlockState.IDLE && bsLeft.getType != BlockType.GARBAGE
  }

  private def dump: String = {
    var asText = "\n"
    for (y <- 0 until maxReachableY) {
      for (x <- 0 until sizeX) {
        val bs = ps.getBlocksSituation()(x)(maxReachableY - 1 - y)
        if (bs != null)
          asText += bs.getType.ordinal;
        else
          asText += "x"
      }
      asText += "\n"
    }
    asText
  }

  def getLastMoveScoreDelta: Int = {
    scoreDelta
  }

  def getRawData: PanelSituation = {ps}

  def isAnyBlockAt(p: Point): Boolean = {isAnyBlockAt(p x, p y)}

  def isAnyBlockAt(x: Int, y: Int): Boolean = {blockTypeAt(x, y) != null}

  def highestBlockY: Int = {
    var y = sizeY - 1
    while (y > 0)
      {
        y += -1
        for (x <- 0 until sizeX) if (isAnyBlockAt(x, y)) return y
      }
    y
  }

  def isIdle: Boolean = {
    for (x <- 0 until sizeX) {
      for (y <- 0 until sizeY) {
        val b = blockAt(x, y)
        if (b != null && (b.getState == BlockState.FALLING || b.getState == BlockState.SWITCHING_BACK || b.getState == BlockState.SWITCHING_FORTH))
          {
            return false
          }
      }
    }
    true
  }

  private def blockAt(x: Int, y: Int): BlockSituation = {
    getRawData.getBlocksSituation()(x)(y)
  }


}