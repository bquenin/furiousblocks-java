/**
 *
 */
package me.pixodro.furiousblocks.core.situations;

import java.util.List;

/**
 * @author tsug
 */
public class ComboSituation {
  private final int size;
  private final int skillChainLevel;
  private final List<Integer> blockIds;

  public ComboSituation(final int size, final int skillChainLevel, final List<Integer> blockIds) {
    this.size = size;
    this.skillChainLevel = skillChainLevel;
    this.blockIds = blockIds;
  }

  public int getSkillChainLevel() {
    return skillChainLevel;
  }

  public int getSize() {
    return size;
  }

  public boolean contains(final int blockId) {
    return blockIds.contains(blockId);
  }
}
