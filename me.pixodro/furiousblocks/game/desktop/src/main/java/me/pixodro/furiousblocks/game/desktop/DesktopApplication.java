package me.pixodro.furiousblocks.game.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import me.pixodro.furiousblocks.game.FuriousBlocksGame;

/**
 * @author TsuG
 */

public class DesktopApplication {
  public static void main(final String[] args) {
    try {
      final Lwjgl3ApplicationConfiguration applicationConfiguration = new Lwjgl3ApplicationConfiguration();
      applicationConfiguration.useVsync(true);
//      applicationConfiguration.useCPUSynch = false;
//      applicationConfiguration.fullscreen = false;
      applicationConfiguration.setTitle("Furious Blocks");
      applicationConfiguration.setWindowedMode(480, 800);
      new Lwjgl3Application(new FuriousBlocksGame(), applicationConfiguration);
    } catch (final Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
