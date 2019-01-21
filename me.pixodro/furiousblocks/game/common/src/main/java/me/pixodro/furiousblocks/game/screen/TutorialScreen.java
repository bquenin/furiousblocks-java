package me.pixodro.furiousblocks.game.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.utils.Align;
import me.pixodro.furiousblocks.core.FuriousBlocksCore;
import me.pixodro.furiousblocks.core.FuriousBlocksCoreDefaults;
import me.pixodro.furiousblocks.core.FuriousBlocksCoreListener;
import me.pixodro.furiousblocks.core.panel.*;
import me.pixodro.furiousblocks.core.situations.GameSituation;
import me.pixodro.furiousblocks.core.situations.PanelSituation;
import me.pixodro.furiousblocks.game.Assets;
import me.pixodro.furiousblocks.game.MenuNinePatch;
import me.pixodro.furiousblocks.game.player.human.TutorialPlayer;
import me.pixodro.furiousblocks.game.script.*;

import static me.pixodro.furiousblocks.core.panel.BlockType.*;

public class TutorialScreen extends AbstractPanelScreen implements FuriousBlocksCoreListener {
  private final FuriousBlocksCore core;
  private final TutorialPlayer tutorialPlayer = new TutorialPlayer("tutorial");
  private final Panel tutorialPanel;
  private final Script script = new Script();
  private final Thread coreThread;

  private TextToType textToType = new TextToType();


  private static final BlockType[][] empty = new BlockType[][]{ //
      {TUTORIAL, TUTORIAL, TUTORIAL, TUTORIAL, TUTORIAL, TUTORIAL}, //
  };

  private static final BlockType[][] combo4and5 = new BlockType[][]{ //
      {TUTORIAL, TUTORIAL, TUTORIAL, TUTORIAL, TUTORIAL, TUTORIAL}, //
      {YELLOW, RED, null, null, RED, YELLOW}, //
      {YELLOW, RED, null, null, RED, YELLOW}, //
      {RED, GREEN, null, null, GREEN, RED}, //
      {null, RED, null, null, RED, null}, //
      {null, null, null, null, RED, null}, //
  };

  private static final BlockType[][] combo6and10 = new BlockType[][]{ //
      {TUTORIAL, TUTORIAL, TUTORIAL, TUTORIAL, TUTORIAL, TUTORIAL}, //
      {YELLOW, RED, null, null, RED, YELLOW}, //
      {RED, YELLOW, null, null, RED, YELLOW}, //
      {YELLOW, RED, null, null, YELLOW, RED}, //
      {null, null, null, null, RED, YELLOW}, //
      {null, null, null, null, RED, YELLOW}, //
  };

  private static final BlockType[][] chainx2 = new BlockType[][]{ //
      {TUTORIAL, TUTORIAL, TUTORIAL, TUTORIAL, TUTORIAL, TUTORIAL}, //
      {null, GREEN, RED, RED, null, RED, null}, //
      {null, null, GREEN, GREEN, null, null}, //
  };

  private static final BlockType[][] chainx3 = new BlockType[][]{ //
      {TUTORIAL, TUTORIAL, TUTORIAL, TUTORIAL, TUTORIAL, TUTORIAL}, //
      {PURPLE, GREEN, RED, RED, null, RED, null}, //
      {null, PURPLE, GREEN, GREEN, null, null}, //
      {null, null, PURPLE, null, null, null}, //
  };

  private static final BlockType[][] Etc4ChainsPanel = new BlockType[][]{ //
      {TUTORIAL, TUTORIAL, TUTORIAL, TUTORIAL, TUTORIAL, TUTORIAL}, //
      {BLUE, BLUE, RED, RED, YELLOW, TUTORIAL}, //
      {TUTORIAL, TUTORIAL, BLUE, GREEN, PURPLE, PURPLE}, //
      {TUTORIAL, TUTORIAL, YELLOW, RED, RED, TUTORIAL}, //
      {TUTORIAL, BLUE, PURPLE, PURPLE, YELLOW, TUTORIAL}, //
      {BLUE, TUTORIAL, YELLOW, GREEN, YELLOW, TUTORIAL}, //
      {TUTORIAL, BLUE, YELLOW, GREEN, RED, TUTORIAL}, //
      {TUTORIAL, PURPLE, RED, PURPLE, TUTORIAL, TUTORIAL} //
  };

  public TutorialScreen(final Game game, final Assets assets) {
    super(game, assets);

    // Create a core
    core = new FuriousBlocksCore(2064, this);
    core.initialBlockTypes[5][2] = null;
    core.initialBlockTypes[5][3] = null;
    core.initialBlockTypes[4][3] = null;

    // Create the tutorial panel
    tutorialPanel = new Panel(2064, tutorialPlayer.getId(), core.initialBlockTypes, null);
    tutorialPanel.scrollingEnabled = false;

    // Create players
    core.addPlayer(tutorialPlayer, tutorialPanel);

    // Create the core thread
    coreThread = new Thread(core);
  }

  @Override
  public void show() {
    //    assets.harmonic.setLooping(true);
    //    assets.harmonic.play();

    script.add(new TypeTextAction(.1f, "Hi!\nYou have to clear these blocks.", textToType));
    script.add(new PauseAction(1f));
    script.add(new TypeTextAction(.1f, "It's made of blocks you can move horizontally.", textToType));
    script.add(new CursorAction(1f, tutorialPlayer, MoveType.CURSOR_DOWN));
    script.add(new TypeTextAction(.1f, "Slide horizontally with your finger to move them.", textToType));
    script.add(new CursorAction(.5f, tutorialPlayer, MoveType.BLOCK_SWITCH));
    script.add(new CursorAction(.5f, tutorialPlayer, MoveType.BLOCK_SWITCH));
    script.add(new CursorAction(.5f, tutorialPlayer, MoveType.BLOCK_SWITCH));
    script.add(new CursorAction(.5f, tutorialPlayer, MoveType.BLOCK_SWITCH));
    script.add(new CursorAction(.5f, tutorialPlayer, MoveType.BLOCK_SWITCH));
    script.add(new CursorAction(.5f, tutorialPlayer, MoveType.BLOCK_SWITCH));

    script.add(new TypeTextAction(.1f, "You can make them fall to fill in holes.", textToType));
    script.add(new CursorAction(.05f, tutorialPlayer, MoveType.CURSOR_RIGHT));
    script.add(new CursorAction(.05f, tutorialPlayer, MoveType.BLOCK_SWITCH));
    script.add(new CursorAction(.05f, tutorialPlayer, MoveType.CURSOR_RIGHT));
    script.add(new CursorAction(.05f, tutorialPlayer, MoveType.BLOCK_SWITCH));

    script.add(new TypeTextAction(.1f, "Align 3 blocks horizontally or vertically...", textToType));
    script.add(new CursorAction(.5f, tutorialPlayer, MoveType.CURSOR_LEFT));
    script.add(new CursorAction(.5f, tutorialPlayer, MoveType.CURSOR_LEFT));
    script.add(new CursorAction(.05f, tutorialPlayer, MoveType.CURSOR_LEFT));
    script.add(new CursorAction(.05f, tutorialPlayer, MoveType.CURSOR_LEFT));
    script.add(new CursorAction(.05f, tutorialPlayer, MoveType.BLOCK_SWITCH));
    script.add(new TypeTextAction(.1f, "... to clear them.", textToType));
    script.add(new PauseAction(1f));
    script.add(new CursorAction(.05f, tutorialPlayer, MoveType.CURSOR_RIGHT));
    script.add(new CursorAction(.05f, tutorialPlayer, MoveType.BLOCK_SWITCH));
    script.add(new CursorAction(.05f, tutorialPlayer, MoveType.CURSOR_LEFT));
    script.add(new CursorAction(.05f, tutorialPlayer, MoveType.BLOCK_SWITCH));
    script.add(new PauseAction(1f));

    script.add(new TypeTextAction(.1f, "If you slide vertically with your finger.", textToType));
    script.add(new PauseAction(1f));
    script.add(new TypeTextAction(.1f, "It raises the stack.", textToType));
    script.add(new CursorAction(.1f, tutorialPlayer, MoveType.LIFT));
    script.add(new PauseAction(.5f));
    script.add(new CursorAction(.1f, tutorialPlayer, MoveType.LIFT));
    script.add(new PauseAction(.5f));
    script.add(new CursorAction(.1f, tutorialPlayer, MoveType.LIFT));
    script.add(new PauseAction(.5f));
    script.add(new CursorAction(.1f, tutorialPlayer, MoveType.LIFT));
    script.add(new PauseAction(.5f));
    script.add(new TypeTextAction(.1f, "Don't raise it too much though, or ...", textToType));
    script.add(new CursorAction(.1f, tutorialPlayer, MoveType.LIFT));
    script.add(new PauseAction(.5f));
    script.add(new CursorAction(.1f, tutorialPlayer, MoveType.LIFT));
    script.add(new PauseAction(.5f));
    script.add(new CursorAction(.1f, tutorialPlayer, MoveType.LIFT));
    script.add(new PauseAction(.5f));
    script.add(new CursorAction(.1f, tutorialPlayer, MoveType.LIFT));
    script.add(new PauseAction(.5f));
    script.add(new TypeTextAction(.1f, "... blocks start panicking ...", textToType));
    script.add(new CursorAction(.1f, tutorialPlayer, MoveType.LIFT));
    script.add(new PauseAction(1f));
    script.add(new TypeTextAction(.1f, "... and eventually the game is over.", textToType));
    script.add(new PauseAction(2f));

    script.add(new SetBlocksAction(1f, tutorialPanel, empty));
    script.add(new TypeTextAction(.1f, "Now let's have a look at combos and chains.", textToType));
    script.add(new SetBlocksAction(1f, tutorialPanel, combo4and5));
    script.add(new TypeTextAction(.1f, "A combo is triggered when you clear at least 4 blocks.", textToType));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.CURSOR_DOWN));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.CURSOR_DOWN));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.CURSOR_DOWN));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.CURSOR_DOWN));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.CURSOR_DOWN));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.CURSOR_DOWN));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.CURSOR_DOWN));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.CURSOR_DOWN));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.BLOCK_SWITCH));
    script.add(new TypeTextAction(.1f, "Clear 4!", textToType));
    script.add(new PauseAction(1f));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.CURSOR_RIGHT));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.CURSOR_RIGHT));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.CURSOR_RIGHT));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.CURSOR_RIGHT));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.BLOCK_SWITCH));
    script.add(new TypeTextAction(.1f, "Clear 5!", textToType));
    script.add(new PauseAction(2f));
    script.add(new SetBlocksAction(0.01f, tutorialPanel, combo6and10));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.CURSOR_LEFT));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.CURSOR_LEFT));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.CURSOR_LEFT));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.CURSOR_LEFT));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.CURSOR_DOWN));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.BLOCK_SWITCH));
    script.add(new TypeTextAction(.1f, "Clear 6!", textToType));
    script.add(new PauseAction(1f));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.CURSOR_RIGHT));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.CURSOR_RIGHT));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.CURSOR_RIGHT));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.CURSOR_RIGHT));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.CURSOR_UP));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.BLOCK_SWITCH));
    script.add(new TypeTextAction(.1f, "Clear 10!", textToType));
    script.add(new PauseAction(2f));

    script.add(new SetBlocksAction(1f, tutorialPanel, empty));
    script.add(new TypeTextAction(.1f, "Now on to chains.", textToType));
    script.add(new PauseAction(1f));
    script.add(new SetBlocksAction(0.01f, tutorialPanel, chainx2));
    script.add(new TypeTextAction(.1f, "Chains are triggered when a clearing is done with blocks ...", textToType));
    script.add(new PauseAction(1f));
    script.add(new TypeTextAction(.1f, "... falling from a previous clearing.", textToType));
    script.add(new PauseAction(1f));
    script.add(new TypeTextAction(.1f, "Chain x2", textToType));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.CURSOR_DOWN));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.CURSOR_DOWN));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.BLOCK_SWITCH));
    script.add(new PauseAction(4f));
    script.add(new SetBlocksAction(0.01f, tutorialPanel, chainx3));
    script.add(new TypeTextAction(.1f, "Chain x3", textToType));
    script.add(new CursorAction(.02f, tutorialPlayer, MoveType.BLOCK_SWITCH));
    script.add(new PauseAction(5f));

    script.add(new TypeTextAction(.1f, "This closes the tutorial.\nGo play to practice!", textToType));
    script.add(new PauseAction(1f));

    // Start the core thread
    coreThread.start();
  }

  long soundId;

  @Override
  public void onEvent(final long playerId, final PanelEvent panelEvent) {
    if (playerId == tutorialPlayer.getId()) {
      if (panelEvent.type == PanelEventType.BLOCK_POP) {
        assets.pop.stop(soundId);
        soundId = assets.move.play();
        assets.move.setPitch(soundId, 1.f + 0.1f * (panelEvent.data2 > 10 ? 10 : panelEvent.data2));
      }
    }
  }

  @Override
  public void hide() {
    assets.harmonic.stop();
    core.stop();
  }

  @Override
  public void pause() {
    assets.harmonic.pause();
    core.pause();
  }

  @Override
  public void resume() {
    assets.harmonic.play();
    core.resume();
  }

  @Override
  public void resize(final int width, final int height) {
  }

  @Override
  public void dispose() {
  }

  @Override
  public void render(float delta) {
    super.render(delta);

    GameSituation gameSituation = core.getGameSituation();
    if (gameSituation == null) {
      return;
    }

    // Rendering each panels
    final PanelSituation panelSituation = gameSituation.getPlayerIdToPanelSituation().get(tutorialPlayer.getId());

    int quakeDelta = 0;
    final PanelState panelState = panelSituation.getState();
    switch (panelState) {
      case QUAKING:
      case GAMEOVER_PHASE1:
        quakeDelta = QuakeEffect.TABLE[FuriousBlocksCoreDefaults.PANEL_QUAKINGTIME - panelSituation.getStateTick()];
        break;
      case GAMEOVER_PHASE2:
        quakeDelta = QuakeEffect.TABLE[panelSituation.getStateTick()];
        break;
      default:
        break;
    }

    batcher.enableBlending();
    batcher.begin();
    {
      // Render background center region
      batcher.draw(assets.backgroundRegionMiddle, 0, 20, 480, 704);

      // Render the panel
      renderBlocks(panelSituation, batcher, 16, -44, stateTime, quakeDelta);

      // Render faces
      renderFaces(panelSituation, batcher, 16, -44, stateTime, quakeDelta);

      // Render combos
      renderCombos(panelSituation, batcher, 16, -44, stateTime, quakeDelta);

      // Render background top & bottom region
      batcher.setColor(Color.WHITE);
      batcher.draw(assets.backgroundRegionTop, 0, 724, 480, 76);
      batcher.draw(assets.backgroundRegionBottom, 0, 0, 480, 20);

      // Render cursor
      //      renderCursor(panelSituation, batcher, 16, -44, stateTime, quakeDelta);

      // Render 9p
      final NinePatch nine = MenuNinePatch.getInstance();
      nine.draw(batcher, 30, 522, 360, 160);

      // Render Text
      assets.coop32.draw(batcher, textToType.getText(), 30 + 8, 522 + 160 - 8, 360 - 8, Align.right, true);

      script.execute(stateTime);
    }
    batcher.end();

    fpsLogger.log();
  }
}
