/**
 *
 */
package me.pixodro.furiousblocks.core.situations;

import java.util.List;

/**
 * @author tsug
 */
public class GarbageSituation {
  private final int width;
  private final int height;
  private final int owner;
  private final List<Integer> blocks;

  public GarbageSituation(final int width, final int height, final int owner, final List<Integer> blocks) {
    this.width = width;
    this.height = height;
    this.owner = owner;
    this.blocks = blocks;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getOwner() {
    return owner;
  }

  public boolean contains(final int blockId) {
    return blocks.contains(blockId);
  }
}
