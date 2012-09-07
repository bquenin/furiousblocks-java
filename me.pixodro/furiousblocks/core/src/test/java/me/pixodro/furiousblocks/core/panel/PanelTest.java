package me.pixodro.furiousblocks.core.panel;

import me.pixodro.furiousblocks.core.FuriousBlocksCoreDefaults;
import me.pixodro.furiousblocks.core.tools.SimpleRNG;
import org.junit.Test;

/**
 * User: bquenin
 * Date: 4/24/12
 * Time: 8:45 PM
 */
public class PanelTest {

  @Test
  public void panelLifeCycleTest() {
    final SimpleRNG random = new SimpleRNG(0);

    // Initial panel
    BlockType[][] initialBlockTypes = new BlockType[FuriousBlocksCoreDefaults.PANEL_WIDTH][FuriousBlocksCoreDefaults.PANEL_HEIGHT];
    for (int j = 0; j < 4; j++) {
      for (int i = 0; i < FuriousBlocksCoreDefaults.PANEL_WIDTH; i++) {
        initialBlockTypes[i][j] = BlockType.values()[random.nextInt() % Panel.numberOfRegularBlocks];
      }
    }

    Panel panel = new Panel(2, 1234, initialBlockTypes);
    for (long i = 0; i < 1000; i++) {
      panel.onTick(i);
    }
  }
}
