package me.pixodro.furiousblocks.ai.player;

import java.util.ArrayList;
import java.util.List;

import me.pixodro.furiousblocks.core.tools.MissingUtils;

class LineCombinator {
  private List<BlockSituationEx[]> combinations = new ArrayList<BlockSituationEx[]>();
  private final int size;
  private int index = 0;

  public LineCombinator(final int size) {
    super();
    this.size = size;
  }

  public void combine(final List<BlockSituationEx> candidates) {
    if (combinations.isEmpty()) {
      for (final BlockSituationEx candidate : candidates) {
        final BlockSituationEx[] newCombination = new BlockSituationEx[size];
        newCombination[index] = candidate;
        combinations.add(newCombination);
      }
      index++;
    } else {
      final List<BlockSituationEx[]> newCombinations = new ArrayList<BlockSituationEx[]>();
      for (final BlockSituationEx[] combination : combinations) {
        nextElement:
        for (final BlockSituationEx candidate : candidates) {
          for (final BlockSituationEx aCombination : combination) {
            // Ignore blocks already part of this combination
            if (aCombination == candidate) {
              continue nextElement;
            }
          }
          final BlockSituationEx[] newCombination = MissingUtils.copyOf(combination, combination.length);
          newCombination[index] = candidate;
          newCombinations.add(newCombination);
        }
      }
      index++;
      combinations = newCombinations;
    }
  }

  public final List<BlockSituationEx[]> getCombinations() {
    return combinations;
  }

}
