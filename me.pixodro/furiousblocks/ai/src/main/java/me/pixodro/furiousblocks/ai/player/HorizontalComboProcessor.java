package me.pixodro.furiousblocks.ai.player;

import me.pixodro.furiousblocks.core.panel.BlockType;
import me.pixodro.furiousblocks.core.situations.BlockSituation;
import me.pixodro.furiousblocks.core.situations.PanelSituation;
import me.pixodro.furiousblocks.core.situations.PanelSituationHelper;
import me.pixodro.furiousblocks.core.tools.Point;

class HorizontalComboProcessor {
  private final PanelSituationHelper helper;
  private final BlockSituation[][] blocks;

  public HorizontalComboProcessor(final PanelSituation panelSituation) {
    helper = new PanelSituationHelper(panelSituation);
    blocks = panelSituation.getBlockSituations();
  }

  public Point where(final int low, final int high) {
    for (int y = low; y < high; y++) {
      final int sameBlock[] = new int[BlockType.values().length];
      for (final BlockSituation[] block : blocks) {
        final BlockSituation current = block[y];
        if (!BlockSituation.isComputable(current)) {
          continue;
        }
        sameBlock[current.getType().ordinal()]++;
      }
      for (byte index = 0; index < sameBlock.length; index++) {
        // Same blocks on this line
        if (sameBlock[index] >= 3) {
          final Point target = getHorizontalComboPointOnLine(BlockType.values()[index], y);
          if (target != null) {
            return target;
          }
        }
      }
    }
    return null;
  }

  private Point getHorizontalComboPointOnLine(final BlockType blockType, final int line) {
    // Build a bit mask of blocks of Type blockType
    final boolean mask[] = new boolean[blocks.length];
    for (int x = 0; x < blocks.length; x++) {
      mask[x] = false;
      final BlockSituation current = blocks[x][line];
      if (!BlockSituation.isComputable(current)) {
        continue;
      }
      if (current.getType() != blockType) {
        continue;
      }
      mask[x] = true;
    }

    // 2 in a row
    for (int i = 0; i < (blocks.length - 1); i++) {
      if (mask[i] && mask[i + 1]) {
        // Check right
        for (int x = i + 2; x < (blocks.length - 1); x++) {
          if (mask[x + 1]) {
            final Point comboStarter = new Point(x, line);
            if (helper.isBlockSwitchPossible(comboStarter)) {
              return comboStarter;
            }
          }
        }
        // check left
        for (int x = i - 2; x >= 0; x--) {
          if (mask[x]) {
            final Point comboStarter = new Point(x, line);
            if (helper.isBlockSwitchPossible(comboStarter)) {
              return comboStarter;
            }
          }
        }
      }
    }

    // no consecutive blocks
    for (int i = 0; i < (blocks.length - 1); i++) {
      if (mask[i]) {
        // check right
        for (int x = i + 1; x < (blocks.length - 1); x++) {
          if (mask[x + 1]) {
            final Point comboStarter = new Point(x, line);
            if (helper.isBlockSwitchPossible(comboStarter)) {
              return comboStarter;
            }
          }
        }
        // check left
        for (int x = i - 2; x >= 0; x--) {
          if (mask[x]) {
            final Point comboStarter = new Point(x, line);
            if (helper.isBlockSwitchPossible(comboStarter)) {
              return comboStarter;
            }
          }
        }
      }
    }
    return null;
  }
}
