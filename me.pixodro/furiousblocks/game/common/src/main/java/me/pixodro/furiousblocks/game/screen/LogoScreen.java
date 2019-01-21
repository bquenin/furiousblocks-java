package me.pixodro.furiousblocks.game.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import me.pixodro.furiousblocks.game.Assets;

public class LogoScreen extends AbstractScreen {
  private final int width = 16;
  private final int height = 32;

  Texture texture = new Texture(width, height, Pixmap.Format.RGBA8888);
  Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

  int num_blobs = 4;

  int[] blob_px = new int[num_blobs];
  int[] blob_py = new int[num_blobs];

  // movement vector for each blob
  int[] blob_dx = new int[num_blobs];
  int[] blob_dy = new int[num_blobs];

  int[][] vx = new int[num_blobs][width];
  int[][] vy = new int[num_blobs][height];


  public LogoScreen(final Game game, final Assets assets) {
    super(game, assets);
    for (int i = 0; i < num_blobs; i++) {
      blob_px[i] = (int) (Math.random() * width % width);
      blob_py[i] = (int) (Math.random() * height % height);
      blob_dx[i] = blob_dy[i] = 1;
    }

  }

  class RGBColor extends Color {
    RGBColor(int r, int g, int b) {
      super();
      this.r = (float) r / 256;
      this.g = (float) g / 256;
      this.b = (float) b / 256;
      this.a = 0.5f;
      clamp();
    }
  }

  /**
   * Called when the screen should render itself.
   *
   * @param delta The time in seconds since the last render.
   */
  @Override
  public void render(final float delta) {

    if (Gdx.input.justTouched()) {
      //      guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
      //
      //      if (OverlapTester.pointInRectangle(playBounds, touchPoint.x, touchPoint.y)) {
      //        Assets.playSound(Assets.clickSound);
      game.setScreen(new TitleScreen(game, assets));
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

    for (int i = 0; i < num_blobs; ++i) {
      blob_px[i] += blob_dx[i];
      blob_py[i] += blob_dy[i];

      // bounce across screen
      if (blob_px[i] < 0) {
        blob_dx[i] = 1;
      }
      if (blob_px[i] > width) {
        blob_dx[i] = -1;
      }
      if (blob_py[i] < 0) {
        blob_dy[i] = 1;
      }
      if (blob_py[i] > height) {
        blob_dy[i] = -1;
      }

      for (int x = 0; x < width; x++) {
        vx[i][x] = (blob_px[i] - x) * (blob_px[i] - x);
      }

      for (int y = 0; y < height; y++) {
        vy[i][y] = (blob_py[i] - y) * (blob_py[i] - y);
      }
    }

    int m = 1;
    int x = 0, y = 0;
    for (y = 0; y < height; y++) {
      for (x = 0; x < width; x++) {
        m = 1;
        for (int i = 0; i < num_blobs; ++i) {
          // increase this number to make your blobs bigger
          m += 6000 / (vy[i][y] + vx[i][x] + 1);
        }
        pixmap.drawPixel(x, y, Color.rgba8888(new RGBColor(m + y, m + x, m + x + y)));
      }
    }
    texture.draw(pixmap, 0, 0);

    batcher.begin();
    batcher.draw(texture, 0, 0, 480, 800);
//    assets.joy32.setScale(2);
    drawStringScreenCentered(assets.joy32, "pixodrome", 480, 800);
//    assets.joy32.setScale(1);
    batcher.end();
  }

  @Override
  public void resize(final int width, final int height) {
  }

  @Override
  public void show() {
  }

  @Override
  public void hide() {
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void dispose() {
  }
}
