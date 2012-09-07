package org.tsug.hod

import com.rits.cloning.Cloner

/**
 * Developed with pleasure :)<br>
 * @author HamsterofDeath
 * Created 14.01.2010 @ 22:13:33
 */

class GameTraverser(board: TetrisBoard, depth:Int) {
  private var bestPath: Path = null

  def makeSuggestion: Path =
    {
      bestPath = new Path
      traverse(depth, board, new Path())
      bestPath
    }

  def traverse(depth: Int, board: TetrisBoard, currentPath: Path): Unit = {
    if (depth == 0)
      {
        if (bestPath < currentPath && currentPath.hasRating)
          {
            bestPath = new Cloner deepClone currentPath;
          }
      }
    else
      {
        for (move <- board allPossibleMoves)
          {
            currentPath push move
            val cloneBoard: TetrisBoard = board clone;
            cloneBoard applyMove move
            val score = cloneBoard.getLastMoveScoreDelta
            currentPath addRating score
            traverse(depth - 1, cloneBoard, currentPath)
            currentPath addRating -score
            currentPath pop
          }
      }
  }
}