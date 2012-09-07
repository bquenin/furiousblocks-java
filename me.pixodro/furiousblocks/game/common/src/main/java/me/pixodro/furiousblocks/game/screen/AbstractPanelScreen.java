package me.pixodro.furiousblocks.game.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.pixodro.furiousblocks.core.FuriousBlocksCoreDefaults;
import me.pixodro.furiousblocks.core.panel.BlockState;
import me.pixodro.furiousblocks.core.panel.BlockType;
import me.pixodro.furiousblocks.core.panel.GarbageBlockType;
import me.pixodro.furiousblocks.core.situations.BlockSituation;
import me.pixodro.furiousblocks.core.situations.ComboSituation;
import me.pixodro.furiousblocks.core.situations.GarbageSituation;
import me.pixodro.furiousblocks.core.situations.PanelSituation;
import me.pixodro.furiousblocks.game.Assets;

import static me.pixodro.furiousblocks.core.FuriousBlocksCoreDefaults.BLOCK_SWITCHINGTIME;
import static me.pixodro.furiousblocks.core.panel.BlockState.*;
import static me.pixodro.furiousblocks.game.Assets.TILE_SIZE;

public abstract class AbstractPanelScreen extends AbstractScreen {

  public AbstractPanelScreen(final Game game, final Assets assets) {
    super(game, assets);
  }

  protected void renderBlocks(final PanelSituation panelSituation, SpriteBatch batcher, int xpos, int ypos, final float stateTime, final int delta) {
    for (int j = panelSituation.getWallOffset(), k = 0; j < (FuriousBlocksCoreDefaults.PANEL_HEIGHT + 1); j++, k++) {
      for (int i = 0; i < FuriousBlocksCoreDefaults.PANEL_WIDTH; i++) {
        final BlockSituation current = panelSituation.getBlockSituations()[i][k];

        if (current == null) {
          continue;
        }

        if (current.getType() == BlockType.INVISIBLE) {
          continue;
        }

        TextureRegion region = assets.getBlockRegion(current, stateTime, panelSituation.getBlockSituations()[i][FuriousBlocksCoreDefaults.PANEL_HEIGHT] != null, panelSituation.getBlockSituations()[i][FuriousBlocksCoreDefaults.PANEL_HEIGHT - 1] != null);
        if (region == null) {
          continue;
        }

        if (current.getState() == BlockState.REVEALING || (current.getType() == BlockType.GARBAGE && current.getState() != BlockState.BLINKING)) {
          batcher.setColor(Color.PINK);
        } else {
          batcher.setColor(Color.WHITE);
        }

        int x = xpos + (TILE_SIZE * i);
        final int y = ypos + (TILE_SIZE * j) + ((panelSituation.getScrollingOffset() * TILE_SIZE) / FuriousBlocksCoreDefaults.BLOCK_LOGICALHEIGHT) + delta;

        if (current.getState() == SWITCHING_BACK) {
          x -= ((float) TILE_SIZE / BLOCK_SWITCHINGTIME) * (BLOCK_SWITCHINGTIME - current.getStateTick());
        }

        if (current.getState() == SWITCHING_FORTH) {
          x += ((float) TILE_SIZE / BLOCK_SWITCHINGTIME) * (BLOCK_SWITCHINGTIME - current.getStateTick());
        }

        batcher.draw(region, x, y);

        //        mechanicsDebug(batcher, x, y, current);
      }
    }
  }

  protected void renderCursor(final PanelSituation panelSituation, final SpriteBatch batcher, final int xpos, final int ypos, final float stateTime, final int delta) {
    final int x = xpos + (TILE_SIZE * panelSituation.getCursorPosition().x) - 22;
    final int y = ypos + (TILE_SIZE * panelSituation.getCursorPosition().y) - 24 + ((panelSituation.getScrollingOffset() * TILE_SIZE) / FuriousBlocksCoreDefaults.BLOCK_LOGICALHEIGHT) + delta;

    batcher.setColor(Color.WHITE);
    batcher.draw(assets.CURSOR.getKeyFrame(stateTime, true), x, y);
  }

  protected void renderFaces(final PanelSituation panelSituation, final SpriteBatch batcher, final int xpos, final int ypos, final float stateTime, final int delta) {
    for (int j = panelSituation.getWallOffset(), k = 0; j < (FuriousBlocksCoreDefaults.PANEL_HEIGHT + 1); j++, k++) {
      for (int i = 0; i < FuriousBlocksCoreDefaults.PANEL_WIDTH; i++) {
        final BlockSituation current = panelSituation.getBlockSituations()[i][k];

        if (current == null) {
          continue;
        }

        if (current.getType() != BlockType.GARBAGE || current.getGarbageBlockType() != GarbageBlockType.DOWNLEFT) {
          continue;
        }

        GarbageSituation garbageSituation = panelSituation.getGarbageByBlock(current.getId());

        final int x = xpos + (TILE_SIZE * i);
        final int x2 = xpos + (TILE_SIZE * (i + garbageSituation.getWidth()));
        final int y = ypos + (TILE_SIZE * j) + ((panelSituation.getScrollingOffset() * TILE_SIZE) / FuriousBlocksCoreDefaults.BLOCK_LOGICALHEIGHT) + delta;

        batcher.setColor(Color.WHITE);
        batcher.draw(current.getState() == BlockState.REVEALING || current.getState() == BlockState.BLINKING ? assets.EYE_SURPRISE : assets.EYE, //
            x, y, //
            TILE_SIZE * garbageSituation.getHeight() >= 3 * TILE_SIZE ? 3 * TILE_SIZE : TILE_SIZE * garbageSituation.getHeight(), //
            TILE_SIZE * garbageSituation.getHeight() >= 3 * TILE_SIZE ? 3 * TILE_SIZE : TILE_SIZE * garbageSituation.getHeight());

        batcher.draw(current.getState() == BlockState.REVEALING || current.getState() == BlockState.BLINKING ? assets.EYE_SURPRISE : assets.EYE, //
            x2, y, //
            TILE_SIZE * garbageSituation.getHeight() >= 3 * TILE_SIZE ? -3 * TILE_SIZE : -TILE_SIZE * garbageSituation.getHeight(), //
            TILE_SIZE * garbageSituation.getHeight() >= 3 * TILE_SIZE ? 3 * TILE_SIZE : TILE_SIZE * garbageSituation.getHeight());
      }
    }
  }

  protected void renderCombos(final PanelSituation panelSituation, final SpriteBatch batcher, final int xpos, final int ypos, final float stateTime, final int delta) {
    for (int j = panelSituation.getWallOffset(), k = 0; j < (FuriousBlocksCoreDefaults.PANEL_HEIGHT + 1); j++, k++) {
      for (int i = 0; i < FuriousBlocksCoreDefaults.PANEL_WIDTH; i++) {
        final BlockSituation current = panelSituation.getBlockSituations()[i][k];

        if (current == null) {
          continue;
        }

        if (current.getState() != BLINKING || current.getPoppingIndex() != 0) {
          continue;
        }

        ComboSituation comboSituation = panelSituation.getComboByBlock(current.getId());

        final int comboBlinkingTime = FuriousBlocksCoreDefaults.BLOCK_BLINKINGTIME;
        final float deltaCombo = ((float) TILE_SIZE / (float) comboBlinkingTime / 2) * (comboBlinkingTime - current.getStateTick());
        final int x = xpos + (TILE_SIZE * i);
        final int y = ypos + (TILE_SIZE * j) + ((panelSituation.getScrollingOffset() * TILE_SIZE) / FuriousBlocksCoreDefaults.BLOCK_LOGICALHEIGHT) + delta + (int) deltaCombo;

        if (comboSituation.getSkillChainLevel() > 1) {
          if (comboSituation.getSize() > 3) {
            batcher.setColor(Color.PINK);
            batcher.draw(assets.GARBAGE_BLINK, x, y);
            assets.joy32.draw(batcher, "x" + comboSituation.getSkillChainLevel(), x + 8, y + TILE_SIZE - 8);
            batcher.setColor(Color.GREEN);
            batcher.draw(assets.GARBAGE_BLINK, x, y + TILE_SIZE);
            assets.joy32.draw(batcher, Integer.toString(comboSituation.getSize()), x + 8, y + TILE_SIZE + TILE_SIZE - 8);
          } else {
            batcher.setColor(Color.PINK);
            batcher.draw(assets.GARBAGE_BLINK, x, y);
            assets.joy32.draw(batcher, "x" + comboSituation.getSkillChainLevel(), x + 8, y + TILE_SIZE - 8);
          }
        } else if (comboSituation.getSize() > 3) {
          batcher.setColor(Color.GREEN);
          batcher.draw(assets.GARBAGE_BLINK, x, y);
          assets.joy32.draw(batcher, Integer.toString(comboSituation.getSize()), x + 8, y + TILE_SIZE - 8);
        }
      }
    }
  }

  protected void mechanicsDebug(final SpriteBatch batcher, final int x, final int y, final BlockSituation blockSituation) {
    assets.coop32.draw(batcher, Integer.toString(blockSituation.getStateTick()), x, y + TILE_SIZE - 32);
    if (blockSituation.isFallingFromClearing()) {
      assets.coop32.draw(batcher, "x", x + TILE_SIZE - 32, y + TILE_SIZE - 32);
    }
    switch (blockSituation.getState()) {
      case BLINKING:
        assets.coop32.draw(batcher, "B", x, y + TILE_SIZE);
        break;
      case DONE_BLINKING:
        assets.coop32.draw(batcher, "DB", x, y + TILE_SIZE);
        break;
      case EXPLODING:
        assets.coop32.draw(batcher, "Ex", x, y + TILE_SIZE);
        break;
      case DONE_EXPLODING:
        assets.coop32.draw(batcher, "DE", x, y + TILE_SIZE);
        break;
      case IDLE:
        assets.coop32.draw(batcher, "I", x, y + TILE_SIZE);
        break;
      case REVEALING:
        assets.coop32.draw(batcher, "R", x, y + TILE_SIZE);
        break;
      case DONE_REVEALING:
        assets.coop32.draw(batcher, "DR", x, y + TILE_SIZE);
        break;
      case SWITCHING_BACK:
        assets.coop32.draw(batcher, "SB", x, y + TILE_SIZE);
        break;
      case SWITCHING_FORTH:
        assets.coop32.draw(batcher, "SF", x, y + TILE_SIZE);
        break;
      case DONE_SWITCHING_FORTH:
        assets.coop32.draw(batcher, "DSF", x, y + TILE_SIZE);
        break;
      case TO_DELETE:
        assets.coop32.draw(batcher, "TD", x, y + TILE_SIZE);
        break;
      case HOVERING:
        assets.coop32.draw(batcher, "H", x, y + TILE_SIZE);
        break;
      case DONE_HOVERING:
        assets.coop32.draw(batcher, "DH", x, y + TILE_SIZE);
        break;
      case FALLING:
        assets.coop32.draw(batcher, "F", x, y + TILE_SIZE);
        break;
      case AIRBOUNCING:
        assets.coop32.draw(batcher, "AB", x, y + TILE_SIZE);
        break;
      case DONE_AIRBOUNCING:
        assets.coop32.draw(batcher, "DAB", x, y + TILE_SIZE);
        break;
      default:
        throw new IllegalStateException("Undefined block state: " + blockSituation);
    }
  }
}
