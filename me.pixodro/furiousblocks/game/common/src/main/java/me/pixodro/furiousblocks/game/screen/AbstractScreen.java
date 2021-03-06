package me.pixodro.furiousblocks.game.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.pixodro.furiousblocks.game.Assets;

abstract class AbstractScreen implements Screen {
  final FPSLogger fpsLogger = new FPSLogger();
  final Assets assets;
  final SpriteBatch batcher;
  final Camera camera = new OrthographicCamera(480, 800);
  final Game game;
  float stateTime;
  final GlyphLayout layout = new GlyphLayout();

  AbstractScreen(final Game game, final Assets assets) {
    this.game = game;
    this.assets = assets;
    this.batcher = new SpriteBatch();
    this.camera.position.set(480 / 2, 800 / 2, 0);
  }

  /**
   * Called when the screen should render itself.
   *
   * @param delta The time in seconds since the last render.
   */
  @Override
  public void render(final float delta) {
    stateTime += Gdx.graphics.getRawDeltaTime();
    // Update camera
    camera.update();
    batcher.setProjectionMatrix(camera.combined);
  }

  @Override
  public void resize(final int width, final int height) {
  }

  final void drawStringCentered(BitmapFont font, final String str, final int screenWidth, final int y) {
    layout.setText(font, str);
    font.draw(batcher, str, (screenWidth - layout.width) / 2, y);
  }

  final void drawStringScreenCentered(BitmapFont font, final String str, final int screenWidth, final int screenHeight) {
    layout.setText(font, str);
    font.draw(batcher, str, (screenWidth - layout.width) / 2, (screenHeight + layout.height) / 2);
  }

  //  private void prioritizedPlay(final int source, final Sound sound) {
  //    if (SoundStoreEx.get().isPlaying(source) && (currentSample[source] != null) && (sfx.priority <= currentSample[source].priority)) {
  //      return;
  //    }
  //    sound.stop();
  //    sound.play();
  //    currentSample[source] = sfx;
  //    sfx.sample.playAsSoundEffect(1, 1, false, source);
  //  }

  // private void prioritizedPlayOver(final int source, final Sfxs sfx) {
  // if (SoundStoreEx.get().isPlaying(source) && (currentSample[source] != null) && (sfx.priority < currentSample[source].priority)) {
  // return;
  // }
  // SoundStoreEx.get().stopSource(source);
  // currentSample[source] = sfx;
  // sfx.sample.playAsSoundEffect(1, 1, false, source);
  // }
}
