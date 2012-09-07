package org.tsug.hod

import com.furiousblocks.game._
import collection.mutable.ListBuffer
import java.awt.Point

/**
 * Developed with pleasure :)<br>
 * @author HamsterofDeath
 * Created 15.01.2010 @ 11:33:19
 */

class AIBlockSwitch(rightX: Int, y: Int, board: TetrisBoard)
{
  def executeVirtual: Int = {
    fillHoles//state "idle" seems to be buggy - floating gargabe can be idle. let it fall to avoid rating it as saved lines
    val linesUsed = board.highestBlockY
    var rating = 0
    var depth = 1
    applySwitch
    var fieldModified = false;

    do {
      var oldRating = rating;
      rating += removeChains(depth);
      if (rating > oldRating) {
        depth += 1
      }
      fieldModified = fillHoles
    } while (fieldModified)

    val linesUsedAfterMove = board.highestBlockY
    val linesSaved = linesUsed - linesUsedAfterMove
    rating += (if (linesUsed <= Panel.Y_DISPLAY) 0 else Math.pow(linesSaved * 5, 2).toInt)
    rating
  }

  def executeSwitch = {
    applySwitch
  }

  private def removeChains(depth: Int): Int = {
    var removedBlocks = 0;
    for (c <- new ChainDetector(board, true).findAllChains; p <- c if board.isAnyBlockAt(p))
      {
        removedBlocks += depth
        removedBlocks += removeBlockAt(p)
      }
    removedBlocks
  }

  private def fillHoles: Boolean = {
    var anythingDone = false;
    for (x <- 0 until board.sizeX;
         y <- 1 until board.sizeY - 1)
      {
        if (rawData(x)(y - 1) == null)
          {
            val bs = rawData(x)(y)
            if (canSimpleBlockFall(bs))
              {
                switch(x, y, x, y - 1)
                anythingDone = true
              }
            else if (canGarbageRectFall(x, y))
              {
                moveDownBlockRange(x, y, bs.getParentGarbage.getRect.width)
                anythingDone = true
              }
          }
      }
    anythingDone
  }

  private def moveDownBlockRange(leftX: Int, y: Int, blockCount: Int) = {
    for (x <- leftX until leftX + blockCount) {
      switch(x, y, x, y - 1)
    }
  }


  private def canGarbageRectFall(leftX: Int, y: Int): Boolean = {
    val bs = rawData(leftX)(y)
    if (bs != null && bs.getType == BlockType.GARBAGE && bs.getState == BlockState.IDLE)
      {
        //only apply check to the first block
        if (leftX > 0)
          {
            if (isGarbageAt(leftX - 1, y))
              {
                return false;
              }
          }
        val yBelowBlock = y - 1
        val r = bs.getParentGarbage.getRect
        for (x <- 0 until r.width) {
          if (rawData(x + leftX)(yBelowBlock) != null)
            {
              return false;
            }
        }
        return true
      }
    false
  }

  private def canSimpleBlockFall(bs: BlockSituation): Boolean =
    {
      bs != null && bs.getType != BlockType.GARBAGE && bs.getState == BlockState.IDLE
    }

  private def removeBlockAt(p: Point): Int = {
    rawData(p.x)(p.y) = null;
    countNearGarbage(p)
  }

  private def countNearGarbage(p: Point): Int = {
    var garbageBlocks = 0
    for (x <- Math.max(p.x - 1, 0) until Math.min(p.x + 2, board.sizeX);
         y <- Math.max(p.y - 1, 0) until Math.min(p.y + 2, board.sizeY)
         if isGarbageAt(x, y))
      {
        garbageBlocks += rawData(x)(y).getParentGarbage.size
      }
    garbageBlocks
  }

  private def isGarbageAt(x: Int, y: Int): Boolean = {
    rawData(x)(y) != null && rawData(x)(y).getType == BlockType.GARBAGE
  }


  private def applySwitch = {
    switch(rightX, y, rightX + 1, y)
  }

  private def switch(x1: Int, y1: Int, x2: Int, y2: Int) = {
    val bsLeft = rawData(x1)(y1)
    val bsRight = rawData(x2)(y2)
    rawData(x1)(y1) = bsRight
    rawData(x2)(y2) = bsLeft
  }


  def asSimpleMoves(cursorX: Int, cursorY: Int): List[Move] = {
    var tmpX = cursorX;
    var tmpY = cursorY;
    val moves = new ListBuffer[Move]
    while (tmpX < rightX)
      {
        moves += new CursorRight()
        tmpX += 1
      }
    while (tmpX > rightX)
      {
        moves += new CursorLeft()
        tmpX -= 1
      }
    while (tmpY < y)
      {
        moves += new CursorUp()
        tmpY += 1
      }
    while (tmpY > y)
      {
        moves += new CursorDown()
        tmpY -= 1
      }
    moves += new BlockSwitch()
    moves toList
  }


  override def toString = {
    rightX + "/" + y
  }

  private def panel: PanelSituation = {
    board.getRawData
  }

  private def rawData: Array[Array[BlockSituation]] = {
    panel.getBlocksSituation
  }

  def position: Point = new Point(rightX, y)

  def getThisFor(board: TetrisBoard) = {
    new AIBlockSwitch(rightX, y, board)
  }

  private def ctx: TetrisBoard = board
}
