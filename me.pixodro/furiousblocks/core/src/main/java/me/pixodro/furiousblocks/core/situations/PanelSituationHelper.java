package me.pixodro.furiousblocks.core.situations;

import me.pixodro.furiousblocks.core.panel.BlockState;
import me.pixodro.furiousblocks.core.panel.BlockType;
import me.pixodro.furiousblocks.core.tools.Point;

public class PanelSituationHelper {
  // private static final Logger LOG = Logger.getLogger(PanelSituationHelper.class.getName());
  private final PanelSituation panelSituation;
  private final BlockSituation blockSituations[][];

  public PanelSituationHelper(final PanelSituation panelSituation) {
    super();
    this.panelSituation = panelSituation;
    blockSituations = panelSituation.getBlockSituations();
  }

  public final PanelSituation getPanelSituation() {
    return panelSituation;
  }

  public int getHeight() {
    for (int j = blockSituations[0].length - 1; j >= 0; j--) {
      for (final BlockSituation[] blockSituation : blockSituations) {
        if (blockSituation[j] != null) {
          return j;
        }
      }
    }
    return 0;
  }

  public boolean isBlockSwitchPossible(final Point location) {
    return BlockSituation.isMovable(blockSituations[location.x][location.y]) && BlockSituation.isMovable(blockSituations[location.x + 1][location.y]);
  }

  public boolean isAnyBlockSwitching() {
    for (int j = 0; j < blockSituations[0].length; j++) {
      for (final BlockSituation[] blockSituation : blockSituations) {
        if (blockSituation[j] == null) {
          continue;
        }
        final BlockState state = blockSituation[j].getState();
        if ((state == BlockState.SWITCHING_FORTH) || (state == BlockState.SWITCHING_BACK) || (state == BlockState.DONE_SWITCHING_FORTH)) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isAnyBlockFallingOrHoveringOrAirBouncing() {
    for (int j = 1; j < blockSituations[0].length; j++) {
      for (final BlockSituation[] blockSituation : blockSituations) {
        if (blockSituation[j] == null) {
          continue;
        }
        final BlockState state = blockSituation[j].getState();
        if (state == BlockState.AIRBOUNCING || state == BlockState.DONE_AIRBOUNCING || state == BlockState.HOVERING || state == BlockState.FALLING) {
          return true;
        }
      }
    }
    return false;
  }

  public boolean isClearingGarbage() {
    return panelSituation.isClearing();
    //    for (int j = 1; j < blockSituations[0].length; j++) {
    //      for (final BlockSituation[] blockSituation : blockSituations) {
    //        if (blockSituation[j] == null) {
    //          continue;
    //        }
    //        if (blockSituation[j].isClearing()) {
    //          return true;
    //        }
    //      }
    //    }
    //    return false;
  }

  public boolean blockSwitchAndDetectFalling(final int x, final int y) {
    final BlockSituation src = blockSituations[x][y];
    final BlockSituation dst = blockSituations[x + 1][y];

    if ((src == null) && (dst == null)) {
      return true;
    }

    if (src != null) {
      if (!BlockSituation.isComputable(src)) {
        return false;
      }
    }
    if (dst != null) {
      if (!BlockSituation.isComputable(dst)) {
        return false;
      }
    }

    blockSituations[x][y] = blockSituations[x + 1][y];
    blockSituations[x + 1][y] = src;

    if ((y - 1) >= 0) {
      if (blockSituations[x][y - 1] == null) {
        return false;
      }
      if (blockSituations[x + 1][y - 1] == null) {
        return false;
      }
    }
    return true;
  }

  public boolean detectCombo() {
    for (int y = 1; y < blockSituations[0].length; y++) {
      for (int x = 0; x < blockSituations.length; x++) {
        // Check each block of the panel
        final BlockSituation current = blockSituations[x][y];

        // null, go on
        if (current == null || current.getState() != BlockState.IDLE || !current.getType().movable || !current.getType().combinable) {
          continue;
        }

        // Check right
        int xidem = 1;
        for (int right = x + 1; right < blockSituations.length; right++) {
          final BlockSituation rightBlock = blockSituations[right][y];

          // null, just break
          if (rightBlock == null || rightBlock.getState() != BlockState.IDLE || rightBlock.getType() != current.getType()) {
            break;
          }
          xidem++;
        }

        if (xidem >= 3) {
          return true;
        }

        // Check above
        int yidem = 1;
        for (int above = y + 1; above < blockSituations[0].length; above++) {
          final BlockSituation aboveBlock = blockSituations[x][above];

          // null, just break
          if (aboveBlock == null || aboveBlock.getState() != BlockState.IDLE || aboveBlock.getType() != current.getType()) {
            break;
          }
          yidem++;
        }

        if (yidem >= 3) {
          return true;
        }
      }
    }
    return false;
  }

  public int isGarbaged() {
    for (int j = 1; j < blockSituations[0].length; j++) {
      for (final BlockSituation[] blockSituation : blockSituations) {
        if (blockSituation[j] == null) {
          continue;
        }
        if ((blockSituation[j].getType() == BlockType.GARBAGE) && (blockSituation[j].getState() == BlockState.IDLE)) {
          return j;
        }
      }
    }
    return -1;
  }

  public boolean isLineEmpty(final int line) {
    for (final BlockSituation[] blockSituation : blockSituations) {
      if (blockSituation[line] != null) {
        return false;
      }
    }
    return true;
  }
}
