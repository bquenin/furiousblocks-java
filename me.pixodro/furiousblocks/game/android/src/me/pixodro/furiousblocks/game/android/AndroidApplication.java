package me.pixodro.furiousblocks.game.android;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import me.pixodro.furiousblocks.game.FuriousBlocksGame;

public class AndroidApplication extends com.badlogic.gdx.backends.android.AndroidApplication {


  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final AndroidApplicationConfiguration applicationConfiguration = new AndroidApplicationConfiguration();
    applicationConfiguration.useAccelerometer = false;
    applicationConfiguration.useCompass = false;
    applicationConfiguration.useGL20 = true;
    applicationConfiguration.useWakelock = false;
    initialize(new FuriousBlocksGame(), applicationConfiguration);
  }
}
