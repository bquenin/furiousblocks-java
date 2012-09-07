package me.pixodro.furiousblocks.ai.player;

import java.util.logging.Level;
import java.util.logging.Logger;

import me.pixodro.furiousblocks.core.panel.BlockState;
import me.pixodro.furiousblocks.core.panel.BlockType;
import me.pixodro.furiousblocks.core.situations.BlockSituation;
import me.pixodro.furiousblocks.core.situations.PanelSituation;
import me.pixodro.furiousblocks.core.tools.Point;

class VerticalComboProcessor {
  private static final Logger LOG = Logger.getLogger(VerticalComboProcessor.class.getName());

  private final PanelSituation panel;
  private final BlockSituation[][] blocks;

  public VerticalComboProcessor(final PanelSituation panelSituation) {
    panel = panelSituation;
    blocks = panelSituation.getBlockSituations();
    LOG.setLevel(Level.OFF);
  }

  public Point where(final int low, final int high) {
    BlockType electedType;
    for (int j = low; j < high; j++) {
      for (int i = 0; i < blocks.length; i++) {
        final BlockSituation current = blocks[i][j];
        if (!BlockSituation.isComputable(current)) {
          continue;
        }
        electedType = current.getType();
        if (!doesRowContainBlockType(j + 1, electedType) || !doesRowContainBlockType(j + 2, electedType)) {
          continue;
        }

        final Point currentPoint = new Point(i, j);
        LOG.fine("electedBlock = " + currentPoint);
        if ((panel.getBlockSituations()[i][j + 1] == null) || (panel.getBlockSituations()[i][j + 1].getType() != electedType)) {
          final Point closestBlock = getClosestTypedBlock(i, j + 1, electedType);
          LOG.fine("closestBlock(1) = " + closestBlock);
          if (closestBlock == null) {
            continue;
          }
          LOG.fine("currentblock(1) = " + currentPoint);
          LOG.fine("closestBlock(1) = " + closestBlock);
          return closestBlock;
        }

        if ((panel.getBlockSituations()[i][j + 2] == null) || (panel.getBlockSituations()[i][j + 2].getType() != electedType)) {
          final Point closestBlock = getClosestTypedBlock(i, j + 2, electedType);
          if (closestBlock == null) {
            continue;
          }
          LOG.fine("currentblock(2) = " + currentPoint);
          LOG.fine("closestBlock(2) = " + closestBlock);
          return closestBlock;
        }
      }
    }
    return null;
  }

  private boolean doesRowContainBlockType(final int row, final BlockType type) {
    for (final BlockSituation[] block : blocks) {
      final BlockSituation current = block[row];
      if (!BlockSituation.isComputable(current)) {
        continue;
      }
      if (current.getType() != type) {
        continue;
      }
      return true;
    }
    return false;
  }

  private Point getClosestTypedBlock(final int column, final int line, final BlockType type) {
    Point closestBlockLeft = null;
    for (int i = column - 1; i >= 0; i--) {
      if ((blocks[i][line] != null) && (blocks[i][line].getState() != BlockState.IDLE)) {
        break;
      }
      if (!BlockSituation.isComputable(blocks[i][line])) {
        continue;
      }
      if (blocks[i][line].getType() != type) {
        continue;
      }
      closestBlockLeft = new Point(i, line);
      break;
    }
    Point closestBlockRight = null;
    for (int i = column; i < (blocks.length - 1); i++) {
      if (!BlockSituation.isComputable(blocks[i + 1][line])) {
        continue;
      }
      if (blocks[i + 1][line].getType() != type) {
        continue;
      }
      closestBlockRight = new Point(i, line);
      break;
    }
    if ((closestBlockLeft != null) && (closestBlockRight == null)) {
      return closestBlockLeft;
    }
    if ((closestBlockLeft == null) && (closestBlockRight != null)) {
      return closestBlockRight;
    }
    if (closestBlockLeft != null) {
      final int deltax = column - closestBlockLeft.x;
      final int deltay = closestBlockRight.x - column;
      if (deltax > deltay) {
        return closestBlockRight;
      }
      if (deltay > deltax) {
        return closestBlockLeft;
      }
      if (deltax == deltay) {
        return closestBlockLeft;
      }
    }
    return null;
  }

}
