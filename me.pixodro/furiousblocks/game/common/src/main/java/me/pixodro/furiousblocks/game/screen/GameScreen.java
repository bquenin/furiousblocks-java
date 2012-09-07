package me.pixodro.furiousblocks.game.screen;

import java.text.NumberFormat;
import java.util.Locale;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import me.pixodro.furiousblocks.ai.player.ComputerPlayer;
import me.pixodro.furiousblocks.core.FuriousBlocksCore;
import me.pixodro.furiousblocks.core.FuriousBlocksCoreDefaults;
import me.pixodro.furiousblocks.core.FuriousBlocksCoreListener;
import me.pixodro.furiousblocks.core.panel.PanelEvent;
import me.pixodro.furiousblocks.core.panel.PanelState;
import me.pixodro.furiousblocks.core.panel.Player;
import me.pixodro.furiousblocks.core.situations.GameSituation;
import me.pixodro.furiousblocks.core.situations.PanelSituation;
import me.pixodro.furiousblocks.game.Assets;

public class GameScreen extends AbstractPanelScreen implements FuriousBlocksCoreListener {
  private final FuriousBlocksCore core;
  //  private final HumanPlayer mainPlayer;
  private final Player aiMainPlayer;
  private final Player aiPlayer;
  private final Thread coreThread;
  private final Thread firstPlayerThread;
  private final Thread secondThread;

  public GameScreen(final Game game, final Assets assets) {
    super(game, assets);

    // Create a core
    core = new FuriousBlocksCore((int) System.nanoTime(), this);

    // Create players
    //    core.addPlayer(mainPlayer = new HumanPlayer("human", camera));
    core.addPlayer(aiMainPlayer = new ComputerPlayer("ai-main"));
    core.addPlayer(aiPlayer = new ComputerPlayer("ai"));

    // Create the core thread
    coreThread = new Thread(core);

    // Create the players thread
    //    firstPlayerThread = new Thread(mainPlayer);
    firstPlayerThread = new Thread(aiMainPlayer);
    secondThread = new Thread(aiPlayer);

    //    Gdx.input.setInputProcessor(mainPlayer);
  }

  @Override
  public void show() {
    assets.harmonic.setLooping(true);
    //    assets.harmonic.play();

    // Start the core thread
    coreThread.start();

    // Start the player thread
    firstPlayerThread.start();
    secondThread.start();
  }

  long soundId;

  @Override
  public void onEvent(final long playerId, final PanelEvent panelEvent) {
    //      if (playerId == mainPlayer.getId()) {
    //        if (playerId == aiMainPlayer.getId()) {
    //          if (panelEvent.type == PanelEventType.BLOCK_POP) {
    //            assets.pop.stop(soundId);
    //            soundId = assets.move.play(1, 1.f + 0.1f * (panelEvent.data2 > 10 ? 10 : panelEvent.data2), 0);
    //          }
    //        }
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
  public void dispose() {
    firstPlayerThread.interrupt();
    secondThread.interrupt();
    coreThread.interrupt();
  }

  @Override
  public void render(float delta) {
    super.render(delta);

    if (Gdx.input.justTouched()) {
      core.justATick();
    }

    GameSituation gameSituation = core.getGameSituation();
    if (gameSituation == null) {
      return;
    }

    // Rendering each panels
    //    final PanelSituation panelSituation = gameSituation.getPlayerIdToPanelSituation().get(mainPlayer.getId());
    final PanelSituation panelSituation = gameSituation.getPlayerIdToPanelSituation().get(aiMainPlayer.getId());

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

      // Render score
      drawStringCentered(assets.coop32, "Score", 280, 800 - 8);
      drawStringCentered(assets.coop32, NumberFormat.getNumberInstance(Locale.US).format(panelSituation.getScore()), 280, 800 - 40);

      // Render time
      drawStringCentered(assets.coop32, "Time", 720, 800 - 8);
      drawStringCentered(assets.coop32, String.format("%02d", (int) stateTime / 60), 720 - 110, 800 - 40);
      drawStringCentered(assets.coop32, "'", 720 - 56, 800 - 40);
      drawStringCentered(assets.coop32, String.format("%02d", (int) stateTime % 60), 720, 800 - 40);
      drawStringCentered(assets.coop32, "''", 720 + 56, 800 - 40);
      drawStringCentered(assets.coop32, String.format("%02d", (int) (stateTime * 100) % 100), 720 + 110, 800 - 40);

    }
    batcher.end();

    fpsLogger.log();
  }
}
