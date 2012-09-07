package me.pixodro.furiousblocks.game.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import me.pixodro.furiousblocks.game.Assets;
import me.pixodro.furiousblocks.game.MenuNinePatch;

public class TitleScreen extends AbstractScreen {
  MenuNinePatch nine;

  public TitleScreen(final Game game, final Assets assets) {
    super(game, assets);
  }

  /**
   * Called when this screen becomes the current screen for a {@link com.badlogic.gdx.Game}.
   */
  @Override
  public void show() {
    nine = MenuNinePatch.getInstance();
    assets.voices.setLooping(true);
    //    assets.voices.play();
  }

  @Override
  public void render(float delta) {
    if (Gdx.input.justTouched()) {
      //      guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
      //
      //      if (OverlapTester.pointInRectangle(playBounds, touchPoint.x, touchPoint.y)) {
      //        Assets.playSound(Assets.clickSound);
      game.setScreen(new GameScreen(game, assets));
      return;
      //        return;
      //      }
      //      if (OverlapTester.pointInRectangle(highscoresBounds, touchPoint.x, touchPoint.y)) {
      //        Assets.playSound(Assets.clickSound);
      //        game.setScreen(new HighscoresScreen(game));
      //        return;
      //      }
      //      if (OverlapTester.pointInRectangle(helpBounds, touchPoint.x, touchPoint.y)) {
      //        Assets.playSound(Assets.clickSound);
      //        game.setScreen(new HelpScreen(game));
      //        return;
      //      }
      //      if (OverlapTester.pointInRectangle(soundBounds, touchPoint.x, touchPoint.y)) {
      //        Assets.playSound(Assets.clickSound);
      //        Settings.soundEnabled = !Settings.soundEnabled;
      //        if (Settings.soundEnabled)
      //          Assets.music.play();
      //        else
      //          Assets.music.pause();
      //      }
    }

    // Update camera
    camera.update();
    batcher.setProjectionMatrix(camera.combined);

    // Render title screen background
    batcher.disableBlending();
    batcher.begin();
    batcher.draw(assets.titleScreenRegion, 0, 0, 480, 800);

    // Render options
    batcher.enableBlending();
    {
      int y = 400;

      nine.draw(batcher, 60, 350, 360, 180);

      drawStringCentered(assets.coop46, "Credits", 480, y);
      drawStringCentered(assets.coop46, "Versus CPU", 480, y += 60);
      drawStringCentered(assets.coop46, "Endless", 480, y += 60);

      drawStringCentered(assets.joy32, "©2012 pixodrome", 480, 48);
    }
    batcher.end();

    fpsLogger.log();
  }

  /**
   * @see com.badlogic.gdx.ApplicationListener#resize(int, int)
   */
  @Override
  public void resize(final int width, final int height) {
  }

  /**
   * Called when this screen is no longer the current screen for a {@link com.badlogic.gdx.Game}.
   */
  @Override
  public void hide() {
    //    assets.voices.stop();
  }

  /**
   * @see com.badlogic.gdx.ApplicationListener#pause()
   */
  @Override
  public void pause() {
  }

  /**
   * @see com.badlogic.gdx.ApplicationListener#resume()
   */
  @Override
  public void resume() {
  }

  /**
   * Called when this screen should release all resources.
   */
  @Override
  public void dispose() {
  }
}
