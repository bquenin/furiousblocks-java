/**
 *
 */
package me.pixodro.furiousblocks.core.panel;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import me.pixodro.furiousblocks.core.situations.ComboSituation;

/**
 * @author tsug
 */
public class Combo {
  private final transient Set<Block> blocks = new LinkedHashSet<Block>();
  private final int owner;
  int skillChainLevel = 1;

  public Combo(final int owner) {
    this.owner = owner;
  }

  public void addBlock(final Block block) {
    blocks.add(block);
  }

  public Set<Block> getBlocks() {
    return blocks;
  }

  public int size() {
    return blocks.size();
  }

  public int getOwner() {
    return owner;
  }

  public int getSkillChainLevel() {
    return skillChainLevel;
  }

  public ComboSituation getSituation() {
    final List<Integer> blockIds = new ArrayList<Integer>();
    for (Block block : blocks) {
      blockIds.add(block.getId());
    }
    return new ComboSituation(blocks.size(), skillChainLevel, blockIds);
  }

  public boolean contains(final Block block) {
    return blocks.contains(block);
  }

  @Override
  public final int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((blocks == null) ? 0 : blocks.hashCode());
    // result = (prime * result) + ((origin == null) ? 0 : origin.hashCode());
    // result = (prime * result) + owner;
    // result = (prime * result) + skillChainLevel;
    return result;
  }

  @Override
  public final boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Combo other = (Combo) obj;
    if (blocks == null) {
      if (other.blocks != null) {
        return false;
      }
    } else if (!blocks.equals(other.blocks)) {
      return false;
    }
    // if (origin == null) {
    // if (other.origin != null) {
    // return false;
    // }
    // } else if (!origin.equals(other.origin)) {
    // return false;
    // }
    // if (owner != other.owner) {
    // return false;
    // }
    // if (skillChainLevel != other.skillChainLevel) {
    // return false;
    // }
    return true;
  }
}
