package me.pixodro.furiousblocks.game.script;

import java.util.LinkedList;
import java.util.Queue;

/**
 * User: bquenin
 * Date: 23/05/12
 * Time: 20:25
 */
public class Script {
  private final Queue<Action> actions = new LinkedList<Action>();

  public Script add(Action action) {
    actions.add(action);
    return this;
  }

  public void execute(final float stateTime) {
    final Action action = actions.peek();
    if (action == null) {
      return;
    }
    if (action.execute(stateTime)) {
      actions.remove();
      final Action nextAction = actions.peek();
      if (nextAction == null) {
        return;
      }
      nextAction.setNextStep(stateTime + action.getTimeStep());
    }
  }
}
