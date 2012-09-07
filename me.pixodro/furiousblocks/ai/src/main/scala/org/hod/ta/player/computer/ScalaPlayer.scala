package org.tsug

import hod.{GameTraverser, TetrisBoard}
import collection.mutable.ListBuffer
import java.awt.Point
import java.util.{LinkedList, Queue}
import ta.game._

/**
 * Developed with pleasure :)<br>
 * @author HamsterofDeath
 * Created 14.01.2010 @ 20:37:18
 */

class ScalaPlayer private(name: String) extends Player(name: String) {
  def this() = {
    this ("Hod AI - Scala experiment")
  }

  def playerTick(ps: PanelSituation, isMostRecentData: Boolean, areCommandsInQueue: Boolean): Queue[Move] = {
    var asQueue: LinkedList[Move] = null
    if (isMostRecentData && !areCommandsInQueue)
      {
        var plan: ListBuffer[Move] = null
        val tb = new TetrisBoard(ps)

        if (tb.isIdle)
          {
            val startMillis = System.currentTimeMillis
            plan = new ListBuffer[Move]
            val virtualCursor = new Point(ps getCursorX, ps getCursorY)
            val path = new GameTraverser(tb, if (tb.highestBlockY > 13) 2 else 3) makeSuggestion;
            for (nextStep <- path) {
              val movesToDo = nextStep.asSimpleMoves(virtualCursor x, virtualCursor y)
              plan appendAll movesToDo
              virtualCursor setLocation (nextStep position)
            }
            if (plan isEmpty)
              {
                for (x <- 0 to 15) {
                  plan += new Lift
                }
              }
          } else {
        }
        if (plan != null)
          {
            asQueue = new LinkedList[Move]
            for (e <- plan) {asQueue add e}
          }
      }
    asQueue
  }
}