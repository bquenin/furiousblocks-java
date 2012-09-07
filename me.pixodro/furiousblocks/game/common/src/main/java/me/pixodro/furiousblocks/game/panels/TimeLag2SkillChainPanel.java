package me.pixodro.furiousblocks.game.panels;

import me.pixodro.furiousblocks.core.panel.BlockType;
import me.pixodro.furiousblocks.core.panel.Panel;

public class TimeLag2SkillChainPanel extends Panel {

  public TimeLag2SkillChainPanel(final int seed, final int playerId) {
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
    blocks[1][2] = newBlock(BlockType.TUTORIAL);
    blocks[2][2] = newBlock(BlockType.YELLOW);
    blocks[3][2] = newBlock(BlockType.RED);
    blocks[4][2] = newBlock(BlockType.YELLOW);
    blocks[5][2] = newBlock(BlockType.TUTORIAL);

    blocks[0][3] = newBlock(BlockType.TUTORIAL);
    blocks[1][3] = newBlock(BlockType.TUTORIAL);
    blocks[2][3] = newBlock(BlockType.GREEN);
    blocks[3][3] = newBlock(BlockType.RED);
    blocks[4][3] = newBlock(BlockType.YELLOW);
    blocks[5][3] = newBlock(BlockType.TUTORIAL);

    blocks[0][4] = newBlock(BlockType.TUTORIAL);
    blocks[1][4] = newBlock(BlockType.TUTORIAL);
    blocks[2][4] = newBlock(BlockType.BLUE);
    blocks[3][4] = newBlock(BlockType.GREEN);
    blocks[4][4] = newBlock(BlockType.BLUE);
    blocks[5][4] = newBlock(BlockType.BLUE);

    blocks[0][5] = newBlock(BlockType.TUTORIAL);
    blocks[1][5] = newBlock(BlockType.TUTORIAL);
    blocks[2][5] = newBlock(BlockType.GREEN);
    blocks[3][5] = newBlock(BlockType.BLUE);
    blocks[4][5] = newBlock(BlockType.YELLOW);
    blocks[5][5] = newBlock(BlockType.TUTORIAL);

    blocks[0][6] = newBlock(BlockType.TUTORIAL);
    blocks[1][6] = newBlock(BlockType.TUTORIAL);
    blocks[2][6] = newBlock(BlockType.GREEN);
    blocks[3][6] = newBlock(BlockType.BLUE);
    blocks[4][6] = newBlock(BlockType.TUTORIAL);
    blocks[5][6] = newBlock(BlockType.TUTORIAL);

    blocks[0][7] = newBlock(BlockType.TUTORIAL);
    blocks[1][7] = newBlock(BlockType.TUTORIAL);
    blocks[2][7] = newBlock(BlockType.YELLOW);
    blocks[3][7] = newBlock(BlockType.RED);
    blocks[4][7] = newBlock(BlockType.TUTORIAL);
    blocks[5][7] = newBlock(BlockType.TUTORIAL);
  }
}
