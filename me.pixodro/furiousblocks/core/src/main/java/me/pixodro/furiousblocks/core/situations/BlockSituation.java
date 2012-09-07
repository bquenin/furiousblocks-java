package me.pixodro.furiousblocks.core.situations;

import me.pixodro.furiousblocks.core.panel.BlockState;
import me.pixodro.furiousblocks.core.panel.BlockType;

public class BlockSituation {
  private final int id;
  private final BlockType type;
  private final BlockState state;
  private final int stateTick;
  private final int garbageBlockType;
  private final int garbageOwner;
  private final boolean combo;
  private final boolean justLand;
  private final boolean fallingFromClearing;
  private final int poppingIndex;

  public BlockSituation(final int id, final BlockType type, final BlockState state, final int stateTick, final int garbageBlockType, final int garbageOwner, final boolean combo, final boolean justLand, final boolean fallingFromClearing, final int poppingIndex) {
    super();
    this.id = id;
    this.type = type;
    this.state = state;
    this.stateTick = stateTick;
    this.garbageBlockType = garbageBlockType;
    this.garbageOwner = garbageOwner;
    this.combo = combo;
    this.justLand = justLand;
    this.fallingFromClearing = fallingFromClearing;
    this.poppingIndex = poppingIndex;
  }

  public final int getId() {
    return id;
  }

  public BlockType getType() {
    return type;
  }

  public BlockState getState() {
    return state;
  }

  public int getStateTick() {
    return stateTick;
  }

  public int getGarbageBlockType() {
    return garbageBlockType;
  }

  public int getGarbageOwner() {
    return garbageOwner;
  }

  public boolean isCombo() {
    return combo;
  }

  public boolean hasJustLand() {
    return justLand;
  }

  public static boolean isComputable(final BlockSituation blockSituation) {
    return (blockSituation != null) && blockSituation.getType().movable && (blockSituation.getState() == BlockState.IDLE);
  }

  public static boolean isMovable(final BlockSituation blockSituation) {
    return (blockSituation == null) || ((blockSituation.getState() == BlockState.IDLE) && (blockSituation.getType() != BlockType.GARBAGE));
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final BlockSituation that = (BlockSituation) o;

    if (id != that.id) {
      return false;
    }
    if (state != that.state) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + (state != null ? state.hashCode() : 0);
    return result;
  }

  public int getPoppingIndex() {
    return poppingIndex;
  }

  public boolean isFallingFromClearing() {
    return fallingFromClearing;
  }
}
