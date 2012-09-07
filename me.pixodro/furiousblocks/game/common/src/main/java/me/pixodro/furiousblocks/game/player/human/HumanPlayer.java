package me.pixodro.furiousblocks.game.player.human;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import me.pixodro.furiousblocks.core.panel.Move;
import me.pixodro.furiousblocks.core.panel.Player;
import me.pixodro.furiousblocks.core.situations.PanelSituation;

import static me.pixodro.furiousblocks.core.FuriousBlocksCoreDefaults.BLOCK_LOGICALHEIGHT;
import static me.pixodro.furiousblocks.core.panel.MoveType.*;
import static me.pixodro.furiousblocks.game.Assets.TILE_SIZE;

public class HumanPlayer extends Player implements InputProcessor {
  private final Vector3 touchPointDown = new Vector3();
  private final Vector3 touchPointDragged = new Vector3();
  private final Camera camera;
  private boolean switchOnRight;
  private boolean switchOnLeft;
  private boolean rightTrend;
  private boolean leftTrend;

  public HumanPlayer(final String name, final Camera camera) {
    super(name);
    this.camera = camera;
  }

  private static boolean pointInRectangle(Rectangle r, float x, float y) {
    return r.x <= x && r.x + r.width >= x && r.y <= y && r.y + r.height >= y;
  }

  private static boolean valueInRange(float x, float min, float max) {
    return x >= min && x < max;
  }

  @Override
  protected void playerTick(final PanelSituation panelSituation, final boolean isMostRecentData) {
    if (Gdx.input.isTouched()) {
      final int x = 16 + (TILE_SIZE * panelSituation.getCursorPosition().x);
      final int y = -48 + (TILE_SIZE * panelSituation.getCursorPosition().y) + ((panelSituation.getScrollingOffset() * TILE_SIZE) / BLOCK_LOGICALHEIGHT);
      Rectangle cursorPosition = new Rectangle(x + (leftTrend ? TILE_SIZE : 0), y, TILE_SIZE, TILE_SIZE);
      if (pointInRectangle(cursorPosition, touchPointDragged.x + (leftTrend ? TILE_SIZE : 0) - (rightTrend ? TILE_SIZE : 0), touchPointDragged.y) && (switchOnLeft || switchOnRight)) {
        move = new Move(BLOCK_SWITCH);
        camera.unproject(touchPointDown.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        switchOnLeft = false;
        switchOnRight = false;
      } else if (touchPointDown.x < cursorPosition.x) {
        move = new Move(CURSOR_LEFT);
      } else if (touchPointDown.x > cursorPosition.x + cursorPosition.width) {
        move = new Move(CURSOR_RIGHT);
      } else if (touchPointDown.y < cursorPosition.y) {
        move = new Move(CURSOR_DOWN);
      } else if (touchPointDown.y > cursorPosition.y + cursorPosition.height) {
        move = new Move(CURSOR_UP);
      }
    }
  }

  /**
   * Called when the screen was touched or a mouse button was pressed. The button parameter will be {@link com.badlogic.gdx.Input.Buttons#LEFT} on
   * Android.
   *
   * @param x       The x coordinate, origin is in the upper left corner
   * @param y       The y coordinate, origin is in the upper left corner
   * @param pointer the pointer for the event.
   * @param button  the button
   * @return whether the input was processed
   */
  @Override
  public boolean touchDown(final int x, final int y, final int pointer, final int button) {
    camera.unproject(touchPointDown.set(Gdx.input.getX(), Gdx.input.getY(), 0));
    camera.unproject(touchPointDragged.set(Gdx.input.getX(), Gdx.input.getY(), 0));
    switchOnLeft = false;
    switchOnRight = false;
    leftTrend = false;
    rightTrend = false;
    return true;
  }

  /**
   * Called when a finger or the mouse was dragged.
   *
   * @param x       The x coordinate
   * @param y       The y coordinate
   * @param pointer the pointer for the event.
   * @return whether the input was processed
   */
  @Override
  public boolean touchDragged(final int x, final int y, final int pointer) {
    if (switchOnLeft || switchOnRight) {
      return true;
    }
    camera.unproject(touchPointDragged.set(Gdx.input.getX(), Gdx.input.getY(), 0));
    if (touchPointDragged.x < touchPointDown.x) {
      leftTrend = true;
      rightTrend = false;
    } else if (touchPointDragged.x > touchPointDown.x) {
      leftTrend = false;
      rightTrend = true;
    }
    if ((touchPointDragged.x - 16) < (touchPointDown.x - 16) - ((touchPointDown.x - 16) % TILE_SIZE)) {
      switchOnLeft = true;
      switchOnRight = false;
    } else if ((touchPointDragged.x - 16) > (touchPointDown.x - 16) + (TILE_SIZE - ((touchPointDown.x - 16) % TILE_SIZE))) {
      switchOnLeft = false;
      switchOnRight = true;
    }
    return true;
  }

  /**
   * Called when a finger was lifted or a mouse button was released. The button parameter will be {@link com.badlogic.gdx.Input.Buttons#LEFT} on
   * Android.
   *
   * @param x       The x coordinate
   * @param y       The y coordinate
   * @param pointer the pointer for the event.
   * @param button  the button
   * @return whether the input was processed
   */
  @Override
  public boolean touchUp(final int x, final int y, final int pointer, final int button) {
    switchOnLeft = false;
    switchOnRight = false;
    leftTrend = false;
    rightTrend = false;
    return true;
  }

  /**
   * Called when a key was pressed
   *
   * @param keycode one of the constants in {@link com.badlogic.gdx.Input.Keys}
   * @return whether the input was processed
   */
  @Override
  public boolean keyDown(final int keycode) {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * Called when a key was released
   *
   * @param keycode one of the constants in {@link com.badlogic.gdx.Input.Keys}
   * @return whether the input was processed
   */
  @Override
  public boolean keyUp(final int keycode) {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * Called when a key was typed
   *
   * @param character The character
   * @return whether the input was processed
   */
  @Override
  public boolean keyTyped(final char character) {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * Called when the mouse was moved without any buttons being pressed. Will not be called on Android.
   *
   * @param x The x coordinate
   * @param y The y coordinate
   * @return whether the input was processed
   */
  @Override
  public boolean touchMoved(final int x, final int y) {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }

  /**
   * Called when the mouse wheel was scrolled. Will not be called on Android.
   *
   * @param amount the scroll amount, -1 or 1 depending on the direction the wheel was scrolled.
   * @return whether the input was processed.
   */
  @Override
  public boolean scrolled(final int amount) {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
