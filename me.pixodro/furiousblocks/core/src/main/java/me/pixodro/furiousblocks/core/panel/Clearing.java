package me.pixodro.furiousblocks.core.panel;

import java.util.HashSet;
import java.util.Set;

import me.pixodro.furiousblocks.core.panel.Panel.BlockBar;

class Clearing {
  private final Set<BlockBar> bars = new HashSet<BlockBar>();
  private long revealingTime;

  public void addBlockBar(final BlockBar bar) {
    for (Block block : bar.barBlocks) {
      block.setClearing(this);
    }
    bars.add(bar);
  }

  public boolean isDoneRevealing(final long tick) {
    return tick == revealingTime;
  }

  public void onDoneRevealing() {
    for (final BlockBar bar : bars) {
      bar.onDoneRevealing();
    }
  }

  public boolean contains(final Block block) {
    for (final BlockBar bar : bars) {
      if (bar.contains(block)) {
        return true;
      }
    }
    return false;
  }

  public boolean isEmpty() {
    return bars.isEmpty();
  }

  public void removeBar(final BlockBar bar) {
    for (Block block : bar.barBlocks) {
      block.setClearing(null);
    }
    bars.remove(bar);
  }

  public void setRevealingTime(final long revealingTime) {
    this.revealingTime = revealingTime;
  }
}
