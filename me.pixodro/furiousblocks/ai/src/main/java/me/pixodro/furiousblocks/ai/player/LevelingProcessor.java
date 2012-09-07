package me.pixodro.furiousblocks.ai.player;

import me.pixodro.furiousblocks.core.panel.BlockState;
import me.pixodro.furiousblocks.core.panel.BlockType;
import me.pixodro.furiousblocks.core.situations.BlockSituation;
import me.pixodro.furiousblocks.core.situations.PanelSituation;
import me.pixodro.furiousblocks.core.situations.PanelSituationHelper;
import me.pixodro.furiousblocks.core.tools.Point;

class LevelingProcessor {
  private final PanelSituationHelper helper;
  private final BlockSituation[][] blocks;

  public LevelingProcessor(final PanelSituation panelSituation) {
    helper = new PanelSituationHelper(panelSituation);
    blocks = panelSituation.getBlockSituations();
  }

  public Point where(final int low) {
    final Point lowestPointToLevel = findLowestPointToLevel(low, Math.min(getHighestLineUnderGarbage(), helper.getHeight()));
    if (lowestPointToLevel != null) {
      return getClosestBlockUnderGarbage(lowestPointToLevel);
    }
    return null;
  }

  private Point findLowestPointToLevel(final int low, final int high) {
    Point needLevel = null;
    for (int j = low; j < high; j++) {
      boolean emptyLine = true;
      for (int i = 0; i < blocks.length; i++) {
        if ((blocks[i][j] == null) && BlockSituation.isMovable(blocks[i][j + 1])) {
          needLevel = new Point(i, j);
        }
        if (blocks[i][j] != null) {
          emptyLine = false;
          if (blocks[i][j].getType() == BlockType.GARBAGE) {
            needLevel = null;
            break;
          }
        }
      }
      if (emptyLine) {
        return null;
      }
      if (needLevel != null) {
        return needLevel;
      }
    }
    return needLevel;
  }

  private int getHighestLineUnderGarbage() {
    for (int j = 0; j < blocks[0].length; j++) {
      boolean highestLine = false;
      for (final BlockSituation[] block : blocks) {
        if (block[j] == null) {
          continue;
        }
        if (block[j].getType() == BlockType.GARBAGE) {
          highestLine = true;
          break;
        }
      }
      if (highestLine) {
        return j - 1;
      }
    }
    return blocks[0].length - 1;
  }

  private Point getClosestBlockUnderGarbage(final Point Point) {
    if (((Point.y + 1) < (getHighestLineUnderGarbage() + 1)) && ((Point.y + 1) < blocks[0].length)) {
      return getClosestBlock(Point.x, Point.y + 1);
    }
    return null;
  }

  private Point getClosestBlock(final int column, final int line) {
    Point closestBlockLeft = null;
    for (int i = column - 1; i >= 0; i--) {
      if (blocks[i][line] == null) {
        continue;
      }
      if (blocks[i][line].getType() == BlockType.GARBAGE) {
        continue;
      }
      if (blocks[i][line].getState() != BlockState.IDLE) {
        continue;
      }
      closestBlockLeft = new Point(i, line);
      break;
    }
    Point closestBlockRight = null;
    for (int i = column; i < (blocks.length - 1); i++) {
      if (blocks[i + 1][line] == null) {
        continue;
      }
      if (blocks[i + 1][line].getType() == BlockType.GARBAGE) {
        continue;
      }
      if (blocks[i + 1][line].getState() != BlockState.IDLE) {
        continue;
      }
      closestBlockRight = new Point(i, line);
      break;
    }
    if (closestBlockLeft != null) {
      if (closestBlockRight == null) {
        return closestBlockLeft;
      }

      final int deltax = column - closestBlockLeft.x;
      final int deltay = closestBlockRight.x - column;
      if (deltax > deltay) {
        return closestBlockRight;
      }
      return closestBlockLeft;
    }

    if (closestBlockRight != null) {
      return closestBlockRight;
    }

    return null;
  }
}
