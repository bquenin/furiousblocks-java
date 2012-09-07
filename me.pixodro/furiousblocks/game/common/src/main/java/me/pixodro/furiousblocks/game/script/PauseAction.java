package me.pixodro.furiousblocks.game.script;

public class PauseAction extends Action {

  public PauseAction(final float timeStep) {
    this.timeStep = timeStep;
  }

  @Override
  public boolean execute(final float stateTime) {
    return stateTime > nextStep;
  }
}
