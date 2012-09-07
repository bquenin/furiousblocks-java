package me.pixodro.furiousblocks.ai.player;

import java.util.logging.Level;
import java.util.logging.Logger;

import me.pixodro.furiousblocks.core.panel.Move;
import me.pixodro.furiousblocks.core.panel.MoveType;
import me.pixodro.furiousblocks.core.panel.Player;
import me.pixodro.furiousblocks.core.situations.PanelSituation;
import me.pixodro.furiousblocks.core.situations.PanelSituationHelper;
import me.pixodro.furiousblocks.core.tools.Point;

public class ComputerPlayer extends Player {
  private static final Logger LOG = Logger.getLogger(ComputerPlayer.class.getName());
  private boolean cursorMoving = false;
  private Point targetPosition;
  private final ComboProcessor comboProcessor = new ComboProcessor();
  static final Point DONOTMOVE = new Point(1024, 1024);

  public ComputerPlayer(final String name) {
    super(name);
    LOG.setLevel(Level.OFF);
  }

  @Override
  protected void playerTick(final PanelSituation panelSituation, final boolean isMostRecentData) {
    if (!isMostRecentData) {
      LOG.info("Not most recent data, returning");
      return;
    }

    // Helper
    final PanelSituationHelper helper = new PanelSituationHelper(panelSituation);
    comboProcessor.update(panelSituation);

    // Potential target
    Point potentialTarget;

    // The move to submit
    Move myMove = null;

    boolean comboInProgress = helper.isClearingGarbage() | !panelSituation.getComboSituations().isEmpty() | comboProcessor.isComboInProgress();

    // The AI is too fast, just make it stop for a few frames
    if (helper.isAnyBlockSwitching()) {
      // Do nothing
    }

    // If the cursor is moving to a target,
    // we just wait until it reaches its target.
    // Once the target is hit, we perform a block switch and process a new move
    else if (cursorMoving) {
      myMove = moveCursorToCurrentTarget(panelSituation);
    } else if ((panelSituation.getBlockSituations().length <= 6) && ((potentialTarget = comboProcessor.where()) != null)) {
      if (potentialTarget == DONOTMOVE) {
        LOG.fine(name + ": DONOTMOVE! !!");
        return;
      }
      comboInProgress |= comboProcessor.isComboInProgress();
      myMove = moveCursorToNewTarget(panelSituation, potentialTarget);
      LOG.fine(name + ": combining");
    }
    // If the panel is too low, lift it
    else if (!helper.isAnyBlockFallingOrHoveringOrAirBouncing() && (helper.getHeight() < (panelSituation.getBlockSituations()[0].length - 2)) && !panelSituation.isLocked()) {
      LOG.fine(name + ": lifting because helper.getHeight() = " + helper.getHeight() + " <= panelSituation.getBlockSituations()[0].length - 2 = " + (panelSituation.getBlockSituations()[0].length - 2));
      myMove = new Move(MoveType.LIFT);
    }
    // If the panel needs leveling, level it
    else if (!helper.isAnyBlockFallingOrHoveringOrAirBouncing() && !helper.isClearingGarbage() && ((potentialTarget = new LevelingProcessor(panelSituation).where(comboProcessor.getHighestOriginLine() + 1)) != null)) {
      LOG.fine(name + ": need leveling @ line " + potentialTarget);
      myMove = moveCursorToNewTarget(panelSituation, potentialTarget);
    }
    // Nothing's happening, try to find an horizontal combo starter
    else if (!helper.isAnyBlockFallingOrHoveringOrAirBouncing() && !comboInProgress && ((potentialTarget = new HorizontalComboProcessor(panelSituation).where(1, panelSituation.getBlockSituations()[0].length)) != null)) {
      myMove = moveCursorToNewTarget(panelSituation, potentialTarget);
    }
    // Still nothing, try to find a vertical combo starter
    else if (!helper.isAnyBlockFallingOrHoveringOrAirBouncing() && !comboInProgress && ((potentialTarget = new VerticalComboProcessor(panelSituation).where(1, panelSituation.getBlockSituations()[0].length - 2)) != null)) {
      myMove = moveCursorToNewTarget(panelSituation, potentialTarget);
    }
    // Else we're dead :)

    // Only debug info
    if (myMove != null) {
      final StringBuilder stringBuilder = new StringBuilder().append(name).append(": requested move [").append(panelSituation.getCursorPosition().x).append(",").append(panelSituation.getCursorPosition().y).append("]: ").append(myMove.getType());
      if (cursorMoving) {
        stringBuilder.append(", moving to target (").append(targetPosition.x).append(",").append(targetPosition.y).append(")");
      }
      LOG.fine(stringBuilder.toString());
    }
    move = myMove;
  }

  Move moveCursorToNewTarget(final PanelSituation panelSituation, final Point targetPosition) {
    if (panelSituation.getCursorPosition().equals(targetPosition) && (cursorMoving)) {
      cursorMoving = false;
      return null;
    }
    this.targetPosition = targetPosition;
    if (this.targetPosition.x > (panelSituation.getBlockSituations().length - 2)) {
      this.targetPosition.x = panelSituation.getBlockSituations().length - 2;
    }
    if (this.targetPosition.x < 0) {
      this.targetPosition.x = 0;
    }
    if (this.targetPosition.y > (panelSituation.getBlockSituations()[0].length - 2)) {
      this.targetPosition.y = panelSituation.getBlockSituations()[0].length - 2;
    }
    if (this.targetPosition.y < 1) {
      this.targetPosition.y = 1;
    }
    cursorMoving = true;
    return moveCursorToCurrentTarget(panelSituation);
  }

  Move moveCursorToCurrentTarget(final PanelSituation panelSituation) {
    Move move;
    if (panelSituation.getCursorPosition().x < targetPosition.x) {
      move = new Move(MoveType.CURSOR_RIGHT);
    } else if (panelSituation.getCursorPosition().x > targetPosition.x) {
      move = new Move(MoveType.CURSOR_LEFT);
    } else if (panelSituation.getCursorPosition().y < targetPosition.y) {
      move = new Move(MoveType.CURSOR_UP);
    } else if (panelSituation.getCursorPosition().y > targetPosition.y) {
      move = new Move(MoveType.CURSOR_DOWN);
    } else {
      cursorMoving = false;
      move = new Move(MoveType.BLOCK_SWITCH);
    }
    return move;
  }
}
