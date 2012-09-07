package me.pixodro.furiousblocks.game.script;

/**
 * User: bquenin
 * Date: 23/05/12
 * Time: 20:25
 */
public class TypeTextAction extends Action {
  private final String text;
  private final TextToType out;
  private int index;

  public TypeTextAction(final float timeStep, final String text, final TextToType out) {
    this.timeStep = timeStep;
    this.text = text;
    this.out = out;
  }

  @Override
  public boolean execute(final float stateTime) {
    if (stateTime > nextStep) {
      if (index == text.length()) {
        return true;
      } else {
        index++;
        out.setText(text.substring(0, index));
      }
      nextStep = stateTime + timeStep;
    }
    return false;
  }
}
