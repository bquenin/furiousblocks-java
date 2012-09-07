package me.pixodro.furiousblocks.game;

import com.badlogic.gdx.Game;
import me.pixodro.furiousblocks.game.screen.GameScreen;
import me.pixodro.furiousblocks.game.screen.LogoScreen;
import me.pixodro.furiousblocks.game.screen.TitleScreen;
import me.pixodro.furiousblocks.game.screen.TutorialScreen;

public class FuriousBlocksGame extends Game {
  public Assets assets;

  @Override
  public void create() {
    assets = new Assets();
    assets.load();

    final LogoScreen logoScreen = new LogoScreen(this, assets);
    final TitleScreen titleScreen = new TitleScreen(this, assets);
    final TutorialScreen tutorialScreen = new TutorialScreen(this, assets);
    final GameScreen gameScreen = new GameScreen(this, assets);

    setScreen(gameScreen);
  }

  @Override
  public void resume() {
    assets.load();
    super.resume();
  }

  @Override
  public void dispose() {
    super.dispose();
    assets.dispose();
  }
}
