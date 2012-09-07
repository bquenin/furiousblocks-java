package me.pixodro.furiousblocks.game.script;

import me.pixodro.furiousblocks.core.panel.BlockType;
import me.pixodro.furiousblocks.core.panel.Panel;

/**
 * User: bquenin
 * Date: 23/05/12
 * Time: 20:25
 */
public class SetBlocksAction extends Action {
  private final Panel panel;
  private final BlockType[][] blockTypes;

  public SetBlocksAction(final float timeStep, final Panel panel, final BlockType[][] blockTypes) {
    this.panel = panel;
    this.blockTypes = blockTypes;
    this.timeStep = timeStep;
  }

  @Override
  public boolean execute(final float stateTime) {
    if (stateTime > nextStep) {
      panel.reset();
      panel.setTransposedBlocks(blockTypes);
      nextStep = stateTime + timeStep;
      return true;
    }
    return false;
  }
}
