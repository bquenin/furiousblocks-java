package me.pixodro.furiousblocks.game.script;

import me.pixodro.furiousblocks.game.player.human.TutorialPlayer;

public class CursorAction extends Action {
  private final TutorialPlayer player;
  private final byte moveType;


  public CursorAction(final float timeStep, final TutorialPlayer player, final byte moveType) {
    this.player = player;
    this.moveType = moveType;
    this.timeStep = timeStep;
  }

  @Override
  public boolean execute(final float stateTime) {
    if (stateTime > nextStep) {
      player.setMove(moveType);
      nextStep = stateTime + timeStep;
      return true;
    }
    return false;
  }
}
