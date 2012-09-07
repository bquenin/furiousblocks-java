package me.pixodro.furiousblocks.game.player.human;

import me.pixodro.furiousblocks.core.panel.Move;
import me.pixodro.furiousblocks.core.panel.Player;
import me.pixodro.furiousblocks.core.situations.PanelSituation;

public class TutorialPlayer extends Player {

  public TutorialPlayer(final String name) {
    super(name);
  }

  public void setMove(byte moveType) {
    this.move = new Move(moveType);
  }

  @Override
  protected void playerTick(final PanelSituation panelSituation, final boolean isMostRecentData) {
    //    if (Gdx.input.isTouched()) {
    //      final int x = 16 + (TILE_SIZE * panelSituation.getCursorPosition().x);
    //      final int y = -48 + (TILE_SIZE * panelSituation.getCursorPosition().y) + ((panelSituation.getScrollingOffset() * TILE_SIZE) / BLOCK_LOGICALHEIGHT);
    //      Rectangle cursorPosition = new Rectangle(x + (leftTrend ? TILE_SIZE : 0), y, TILE_SIZE, TILE_SIZE);
    //      if (pointInRectangle(cursorPosition, touchPointDragged.x + (leftTrend ? TILE_SIZE : 0) - (rightTrend ? TILE_SIZE : 0), touchPointDragged.y) && (switchOnLeft || switchOnRight)) {
    //        move.set(new Move(BLOCK_SWITCH));
    //        camera.unproject(touchPointDown.set(Gdx.input.getX(), Gdx.input.getY(), 0));
    //        switchOnLeft = false;
    //        switchOnRight = false;
    //      } else if (touchPointDown.x < cursorPosition.x) {
    //        move.set(new Move(CURSOR_LEFT));
    //      } else if (touchPointDown.x > cursorPosition.x + cursorPosition.width) {
    //        move.set(new Move(CURSOR_RIGHT));
    //      } else if (touchPointDown.y < cursorPosition.y) {
    //        move.set(new Move(CURSOR_DOWN));
    //      } else if (touchPointDown.y > cursorPosition.y + cursorPosition.height) {
    //        move.set(new Move(CURSOR_UP));
    //      }
    //    }
  }
}
