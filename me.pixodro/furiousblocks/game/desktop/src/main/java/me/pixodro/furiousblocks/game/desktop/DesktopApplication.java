package me.pixodro.furiousblocks.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import me.pixodro.furiousblocks.game.FuriousBlocksGame;

/**
 * @author TsuG
 */

public class DesktopApplication {
  public static void main(final String[] args) {
    try {
      final LwjglApplicationConfiguration applicationConfiguration = new LwjglApplicationConfiguration();
      applicationConfiguration.vSyncEnabled = true;
      applicationConfiguration.useCPUSynch = false;
      applicationConfiguration.fullscreen = false;
      applicationConfiguration.title = "Furious Blocks";
      applicationConfiguration.width = 480;
      applicationConfiguration.height = 800;
      new LwjglApplication(new FuriousBlocksGame(), applicationConfiguration);
    } catch (final Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
