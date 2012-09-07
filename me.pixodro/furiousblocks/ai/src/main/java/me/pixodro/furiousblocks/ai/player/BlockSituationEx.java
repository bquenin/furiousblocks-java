package me.pixodro.furiousblocks.ai.player;

import java.util.HashMap;
import java.util.Map;

import me.pixodro.furiousblocks.core.panel.BlockState;
import me.pixodro.furiousblocks.core.panel.BlockType;
import me.pixodro.furiousblocks.core.situations.BlockSituation;
import me.pixodro.furiousblocks.core.tools.Point;

public class BlockSituationEx extends BlockSituation {
  private final Map<Integer, Integer> targetColumnCosts;
  private Point origin = null;
  private boolean chainReplacement = false;

  public BlockSituationEx(final BlockSituation bs) {
    super(bs.getId(), bs.getType(), bs.getState(), bs.getStateTick(), bs.getGarbageBlockType(), bs.getGarbageOwner(), bs.isCombo(), bs.hasJustLand(), bs.isFallingFromClearing(), bs.getPoppingIndex());
    targetColumnCosts = new HashMap<Integer, Integer>();
    chainReplacement = false;
  }

  public BlockSituationEx(final BlockSituationEx bsEx) {
    super(bsEx.getId(), bsEx.getType(), bsEx.getState(), bsEx.getStateTick(), bsEx.getGarbageBlockType(), bsEx.getGarbageOwner(), bsEx.isCombo(), bsEx.hasJustLand(), bsEx.isFallingFromClearing(), bsEx.getPoppingIndex());
    origin = bsEx.getOrigin();
    targetColumnCosts = bsEx.targetColumnCosts;
    chainReplacement = bsEx.chainReplacement;
  }

  private BlockSituationEx() {
    super(0, BlockType.INVISIBLE, BlockState.IDLE, 0, (byte) 0, 0, false, false, false, 0);
    targetColumnCosts = new HashMap<Integer, Integer>();
  }

  public final int getTargetColumnCost(final int targetColumn) {
    // TODO: this is a bug, target cost should never be null
    // return targetColumnCosts.get(targetColumn);
    final Integer cost = targetColumnCosts.get(targetColumn);
    return cost == null ? ComboProcessor.INFINITY : cost;
  }

  public final void addTargetCost(final int targetColumn, final int cost) {
    targetColumnCosts.put(targetColumn, cost);
  }

  public final Point getOrigin() {
    return origin;
  }

  public final void setOrigin(final Point position) {
    origin = position;
  }

  public final void reset() {
    origin = null;
    targetColumnCosts.clear();
    chainReplacement = false;
  }

  public static BlockSituationEx newInvisibleBlock() {
    return new BlockSituationEx();
  }

  @Override
  public String toString() {
    return "BlockSituationEx [origin=[" + (origin == null ? "null" : (origin.x + ":" + origin.y)) + "], isChainReplacement = " + isChainReplacement() + ", costs=" + targetColumnCosts + "]";
  }

  public void setChainReplacement(final boolean comboReplacement) {
    chainReplacement = comboReplacement;
  }

  public final boolean isChainReplacement() {
    return chainReplacement;
  }

  public final boolean isReplacement() {
    return !targetColumnCosts.isEmpty();
  }
}
