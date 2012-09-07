package me.pixodro.furiousblocks.game.panels;

import me.pixodro.furiousblocks.core.panel.BlockType;
import me.pixodro.furiousblocks.core.panel.Panel;

public class X2Chain1ChainsPanel extends Panel {

  public X2Chain1ChainsPanel(final int seed, final int playerId) {
    super(seed, playerId, null);

    // Stops scrolling
    scrollingEnabled = false;

    // Erase blocks
    for (int y = 0; y < Y; y++) {
      for (int x = 0; x < X; x++) {
        // We fill the hidden line
        blocks[x][y] = y == 0 ? newRandom(null) : null;
      }
    }

    // Setup specific panel
    blocks[0][1] = newBlock(BlockType.TUTORIAL);
    blocks[1][1] = newBlock(BlockType.TUTORIAL);
    blocks[2][1] = newBlock(BlockType.YELLOW);
    blocks[3][1] = newBlock(BlockType.TUTORIAL);
    blocks[4][1] = newBlock(BlockType.TUTORIAL);
    blocks[5][1] = newBlock(BlockType.TUTORIAL);

    blocks[0][2] = newBlock(BlockType.TUTORIAL);
    blocks[1][2] = newBlock(BlockType.RED);
    blocks[2][2] = newBlock(BlockType.RED);
    blocks[3][2] = newBlock(BlockType.TUTORIAL);
    blocks[4][2] = newBlock(BlockType.RED);
    blocks[5][2] = newBlock(BlockType.TUTORIAL);

    blocks[0][3] = null;
    blocks[1][3] = null;
    blocks[2][3] = newBlock(BlockType.YELLOW);
    blocks[3][3] = null;
    blocks[4][3] = null;
    blocks[5][3] = null;

    blocks[0][4] = null;
    blocks[1][4] = null;
    blocks[2][4] = newBlock(BlockType.YELLOW);
    blocks[3][4] = null;
    blocks[4][4] = null;
    blocks[5][4] = null;
  }
}
