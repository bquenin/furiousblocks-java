package me.pixodro.furiousblocks.game.screen;

import me.pixodro.furiousblocks.core.FuriousBlocksCoreDefaults;

class QuakeEffect {
  private static final byte[] SINTABLE = new byte[]{0, 1, 5, 11, 17, 24, 29, 31, 31, 29, 24, 17, 11, 5, 1, 0};
  public static final byte[] TABLE;

  static {
    TABLE = new byte[FuriousBlocksCoreDefaults.PANEL_QUAKINGTIME];
    for (int j = 0, k = 1; j < TABLE.length; j += SINTABLE.length, k *= 2) {
      for (int i = 0; i < SINTABLE.length; i++) {
        TABLE[i + j] = (byte) (SINTABLE[i] / k);
      }
    }
  }
}
