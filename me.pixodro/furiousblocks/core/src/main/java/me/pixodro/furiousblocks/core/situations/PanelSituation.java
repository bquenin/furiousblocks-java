package me.pixodro.furiousblocks.core.situations;

import java.util.Collection;
import java.util.Collections;

import me.pixodro.furiousblocks.core.panel.PanelState;
import me.pixodro.furiousblocks.core.tools.Point;

import static me.pixodro.furiousblocks.core.FuriousBlocksCoreDefaults.PANEL_HEIGHT;
import static me.pixodro.furiousblocks.core.FuriousBlocksCoreDefaults.PANEL_WIDTH;

public class PanelSituation {
  private final BlockSituation[][] blockSituations;
  private final boolean isLocked;
  private final Collection<ComboSituation> comboSituations;
  private final Point cursorPosition;
  private final int scrollingOffset;
  private final PanelState state;
  private final int stateTick;
  private final Collection<GarbageSituation> garbageSituations;
  private final Collection<GarbageSituation> garbageStackSituation;
  private final int skillChainLevel;
  private final int freezingTime;
  private final boolean gameOver;
  private final int wallOffset;
  private final boolean gracing;
  private final int score;
  private final boolean clearing;


  public PanelSituation(final BlockSituation[][] blockSituations, final boolean isLocked, final Collection<ComboSituation> comboSituations, final Point cursorPosition, final int scrollingOffset, final PanelState state, final int stateTick, final Collection<GarbageSituation> garbageSituations, final Collection<GarbageSituation> garbageStackSituation, final int skillChainLevel, final int freezingTime, final boolean gameOver, final int wallOffset, final boolean gracing, final int score, final boolean clearing) {
    this.blockSituations = blockSituations;
    this.isLocked = isLocked;
    this.comboSituations = comboSituations;
    this.state = state;
    this.stateTick = stateTick;
    this.garbageSituations = garbageSituations;
    this.garbageStackSituation = garbageStackSituation;
    this.skillChainLevel = skillChainLevel;
    this.cursorPosition = cursorPosition;
    this.scrollingOffset = scrollingOffset;
    this.freezingTime = freezingTime;
    this.gameOver = gameOver;
    this.wallOffset = wallOffset;
    this.gracing = gracing;
    this.score = score;
    this.clearing = clearing;
  }

  public BlockSituation[][] getBlockSituations() {
    return blockSituations;
  }

  public boolean isLocked() {
    return isLocked;
  }

  public Collection<ComboSituation> getComboSituations() {
    return Collections.unmodifiableCollection(comboSituations);
  }

  public Point getCursorPosition() {
    return cursorPosition;
  }

  public int getScrollingOffset() {
    return scrollingOffset;
  }

  public PanelState getState() {
    return state;
  }

  public int getStateTick() {
    return stateTick;
  }

  public int getSkillChainLevel() {
    return skillChainLevel;
  }

  public final int getFreezingTime() {
    return freezingTime;
  }

  public final boolean isGameOver() {
    return gameOver;
  }

  public int getWallOffset() {
    return wallOffset;
  }

  public boolean isGracing() {
    return gracing;
  }

  public int getScore() {
    return score;
  }

  public GarbageSituation getGarbageByBlock(final int blockId) {
    for (final GarbageSituation garbageSituation : garbageSituations) {
      if (garbageSituation.contains(blockId)) {
        return garbageSituation;
      }
    }
    return null;
  }

  public ComboSituation getComboByBlock(final int blockId) {
    for (final ComboSituation comboSituation : comboSituations) {
      if (comboSituation.contains(blockId)) {
        return comboSituation;
      }
    }
    return null;
  }


  public PanelSituation getSimpleClone() {
    final BlockSituation[][] copy = new BlockSituation[blockSituations.length][blockSituations[0].length];
    for (int i = 0; i < copy.length; i++) {
      System.arraycopy(blockSituations[i], 0, copy[i], 0, copy[i].length);
    }
    return new PanelSituation(copy, isLocked, comboSituations, cursorPosition, scrollingOffset, state, stateTick, garbageSituations, garbageStackSituation, skillChainLevel, freezingTime, gameOver, wallOffset, gracing, score, clearing);
  }

  public boolean isClearing() {
    return clearing;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final PanelSituation that = (PanelSituation) o;
    return blockSituations.hashCode() == that.blockSituations.hashCode();
  }

  @Override
  public int hashCode() {
    if (blockSituations == null) {
      return 0;
    }

    int result = 1;
    for (int x = 0; x < PANEL_WIDTH; x++) {
      for (int y = 0; y < PANEL_HEIGHT + 1; y++) {
        result = 31 * result + (blockSituations[x][y] == null ? 0 : blockSituations[x][y].getId());
      }
    }
    result = 31 * result + scrollingOffset;
    return result;
  }
}
