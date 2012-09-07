package me.pixodro.furiousblocks.game.script;

/**
 * User: bquenin
 * Date: 23/05/12
 * Time: 20:21
 */
public abstract class Action {
  protected float timeStep;
  protected float nextStep;

  public void setNextStep(final float nextStep) {
    this.nextStep = nextStep;
  }

  protected abstract boolean execute(final float stateTime);

  public float getTimeStep() {
    return timeStep;
  }
}
