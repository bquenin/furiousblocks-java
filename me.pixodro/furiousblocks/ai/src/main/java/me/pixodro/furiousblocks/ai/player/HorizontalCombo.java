package me.pixodro.furiousblocks.ai.player;

import me.pixodro.furiousblocks.core.situations.PanelSituation;
import me.pixodro.furiousblocks.core.tools.Point;

public class HorizontalCombo {
  private final BlockSituationEx[] blocks;
  private int size = 0;
  private int line; // Only for rendering
  private static final int INFINITY = 128;

  public HorizontalCombo(final BlockSituationEx[] blocks) {
    super();
    this.blocks = new BlockSituationEx[blocks.length];
    for (int i = 0; i < blocks.length; i++) {
      this.blocks[i] = blocks[i] == null ? null : new BlockSituationEx(blocks[i]);
    }

    // This loop balances the combo moves. Two combinations may have the same cost,
    // sometimes, the wrong one (perform() ends in infinite loop) is selected.
    // It happens when 2 contiguous chain replacement blocks has conflicting origins
    // (i.e. when the 2 origin to destination lines intersects).
    // Just fix it by swapping them assuming they can replace each other as
    // they are of the same type.
    for (int i = 0; i < (blocks.length - 1); i++) {
      // Combo block are contiguous
      final BlockSituationEx current = blocks[i];
      final BlockSituationEx other = blocks[i + 1];
      if ((current == null) || (other == null)) {
        continue;
      }
      if ((current.getOrigin() == null) || (other.getOrigin() == null)) {
        continue;
      }
      if (!current.isChainReplacement() || !other.isChainReplacement()) {
        continue;
      }

      // Swap them
      if (current.getOrigin().x > other.getOrigin().x) {
        blocks[i] = blocks[i + 1];
        blocks[i + 1] = current;
      }
    }
  }

  public final BlockSituationEx[] getBlocks() {
    return blocks;
  }

  public int cost() {
    int cost = 0;
    for (int i = 0; i < blocks.length; i++) {
      final BlockSituationEx current = blocks[i];
      if (current == null) {
        continue;
      }
      if ((current.getOrigin() != null) && current.isChainReplacement()) {
        cost += current.getTargetColumnCost(i);
      }
    }
    return cost;
  }

  public boolean isPossible(final PanelSituation panelSituation) {
    PanelSituation simulation = panelSituation.getSimpleClone();
    for (int i = 0; i < blocks.length; i++) {
      final BlockSituationEx block = blocks[i];
      if (block == null) {
        continue;
      }
      if (!block.isReplacement() || (block.getOrigin() == null)) {
        continue;
      }
      simulation = ComboProcessor.simulateMove(block.getOrigin().x, i, block.getOrigin().y, simulation);
      if (simulation == null) {
        return false;
      }
    }
    return true;
  }

  public Point perform() {
    int cheapestMove = INFINITY;
    int comboBlockIndex = 0;
    BlockSituationEx cheapestMoveBlock = null;
    for (int i = 0; i < blocks.length; i++) {
      final BlockSituationEx block = blocks[i];
      if (block == null) {
        continue;
      }
      if (!block.isReplacement() || (block.getOrigin() == null)) {
        continue;
      }
      final int cost = block.getTargetColumnCost(i);
      if ((cost < cheapestMove) && (cost > 0)) {
        cheapestMove = block.getTargetColumnCost(i);
        cheapestMoveBlock = block;
        comboBlockIndex = i;
      } else if ((cost == cheapestMove) && (cost > 0) && (cheapestMoveBlock != null) && (cheapestMoveBlock.getType() == block.getType()) && (block.getOrigin().x < i)) {
        cheapestMove = block.getTargetColumnCost(i);
        cheapestMoveBlock = block;
        comboBlockIndex = i;
      }
    }

    if ((cheapestMove == INFINITY) || (cheapestMoveBlock == null)) {
      return null;
    }

    final Point target = new Point(cheapestMoveBlock.getOrigin());
    if (comboBlockIndex < target.x) {
      target.x--;
    }
    return target;
  }

  public final int getLine() {
    return line;
  }

  public final void setLine(final int line) {
    this.line = line;
  }

  public final int getSize() {
    return size;
  }

  public final void setSize(final int size) {
    this.size = size;
  }

  public int getHighestOriginLine() {
    int highestLine = 0;
    for (final BlockSituationEx block : blocks) {
      if ((block == null) || (block.getOrigin() == null) || !block.isReplacement()) {
        continue;
      }
      if (block.getOrigin().y > highestLine) {
        highestLine = block.getOrigin().y;
      }
    }
    return highestLine;
  }
}
