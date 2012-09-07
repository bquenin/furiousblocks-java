package me.pixodro.furiousblocks.ai.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.pixodro.furiousblocks.core.panel.BlockState;
import me.pixodro.furiousblocks.core.situations.BlockSituation;
import me.pixodro.furiousblocks.core.situations.PanelSituation;
import me.pixodro.furiousblocks.core.situations.PanelSituationHelper;
import me.pixodro.furiousblocks.core.tools.Point;

class ComboProcessor {
  static final int INFINITY = 128;
  // private static final Logger LOG = Logger.getLogger(ChainComboProcessor.class.getName());
  private PanelSituation panelSituation;
  private PanelSituationHelper helper;
  private BlockSituationEx[][] blockSituations;
  private Point[][] replacedBy;
  private HorizontalCombo selectedCombo;

  public ComboProcessor() {
  }

  public void update(final PanelSituation panelSituation) {
    blockSituations = new BlockSituationEx[panelSituation.getBlockSituations().length][panelSituation.getBlockSituations()[0].length];
    this.panelSituation = panelSituation;
    helper = new PanelSituationHelper(panelSituation);
  }

  public Point where() {
    blockSituations = new BlockSituationEx[panelSituation.getBlockSituations().length][panelSituation.getBlockSituations()[0].length];
    for (int y = 0; y < blockSituations[0].length; y++) {
      for (int x = 0; x < blockSituations.length; x++) {
        blockSituations[x][y] = panelSituation.getBlockSituations()[x][y] == null ? null : new BlockSituationEx(panelSituation.getBlockSituations()[x][y]);
      }
    }

    int line;
    // Clearing in progress, align blocks
    if (helper.isClearingGarbage()) {
      final Point target = alignBlocks();
      if (target == ComputerPlayer.DONOTMOVE) {
        return null;
      }
      if (target != null) {
        return target;
      }

      // Align blocks
      return new LevelingProcessor(panelSituation).where(1);
    } else
      // There's a garbage in the panel, we have to clear it !
      if (((line = helper.isGarbaged()) > 0) && (helper.isGarbaged() <= ((blockSituations[0].length / 2) + 1))) {
        selectedCombo = null;

        Point target = new HorizontalComboProcessor(panelSituation).where(line, line + 1);
        if (target != null) {
          return target;
        }

        target = new VerticalComboProcessor(panelSituation).where(Math.max(1, line - 3), Math.max(1, line - 3) + 2);
        if (target != null) {
          return target;
        }

        // Can't ? Try to level
        return new LevelingProcessor(panelSituation).where(1);
      } else {
        computeReplacedBy();
        return checkHorizontalChainCombos();
        // if (target != null) {
        // return target;
        // }

        // return new LevelingProcessor(panelSituation).where(1);
      }
  }

  private Point alignBlocks() {
    // Column algorithm
    for (int i = 0; i < blockSituations.length; i++) {
      for (int j = 1; j < blockSituations[0].length; j++) {
        // Check each block of the panel
        final BlockSituation current = blockSituations[i][j];
        if (current == null) {
          continue;
        }

        final BlockState state = current.getState();
        // First find the revealing block height in the column
        if ((state != BlockState.REVEALING) && (state != BlockState.DONE_REVEALING) && (state != BlockState.AIRBOUNCING) && (state != BlockState.DONE_AIRBOUNCING) && (state != BlockState.HOVERING)) {
          continue;
        }

        int k;
        // Skip empty lines
        for (k = j - 1; k > 2; k--) {
          if (!helper.isLineEmpty(k)) {
            break;
          }
        }

        // get candidate blocks
        List<BlockSituationEx> candidates = getCandidates(i, k, false, panelSituation);

        // get the cheapest blocks of the right type with non INFINITY costs
        BlockSituationEx chosenBlock1 = null;
        int cheapest1 = INFINITY;
        for (final BlockSituationEx candidate : candidates) {
          if (candidate.getType() != current.getType()) {
            continue;
          }
          final int cost = candidate.getTargetColumnCost(i);
          if (cost < cheapest1) {
            cheapest1 = cost;
            chosenBlock1 = candidate;
          }
        }

        // No luck
        if (chosenBlock1 == null) {
          break;
        }

        final PanelSituation simulation = simulateMove(chosenBlock1.getOrigin().x, i, k, panelSituation.getSimpleClone());
        // We got a block and it's already at the good spot, continue on the line below
        // get candidate blocks, however, it's a simulation from the first move
        candidates = getCandidates(i, k - 1, false, simulation);

        // get the cheapest blocks of the right type with non INFINITY costs
        BlockSituationEx chosenBlock2 = null;
        int cheapest2 = INFINITY;
        for (final BlockSituationEx candidate : candidates) {
          if (candidate.getType() != current.getType()) {
            continue;
          }
          final int cost = candidate.getTargetColumnCost(i);
          if (cost < cheapest2) {
            cheapest2 = cost;
            chosenBlock2 = candidate;
          }
        }

        // No luck
        if (chosenBlock2 == null) {
          break;
        }

        // We got the 2 blocks we need, simulate their both move

        if (cheapest1 > 0) {
          final Point target = new Point(chosenBlock1.getOrigin());
          if (i < target.x) {
            target.x--;
          }
          return target;
        }

        // We got a block, move it
        if (cheapest2 > 0) {
          final Point target = new Point(chosenBlock2.getOrigin());
          if (i < target.x) {
            target.x--;
          }
          return target;
        }

        // We got a block and it's already at the good spot. just wait
        return ComputerPlayer.DONOTMOVE;
      }
    }
    return null;
  }

  /**
   * Identify, for each block part of the combo, which block location is gonna
   * replace the combo block when it disappear.
   */
  private void computeReplacedBy() {
    // Reset values
    replacedBy = new Point[blockSituations.length][blockSituations[0].length];
    for (int i = 0; i < blockSituations.length; i++) {
      for (int j = 1; j < blockSituations[0].length; j++) {
        // Check each block of the panel
        final BlockSituation current = blockSituations[i][j];

        // null, go on
        if (current == null) {
          continue;
        }

        // Not a combo, go on
        if (!current.isCombo()) {
          continue;
        }

        int comboHeight = 0;
        boolean comboBreak = false;
        int aboveLine;
        for (aboveLine = j; aboveLine < blockSituations[0].length; aboveLine++) {
          final BlockSituation aboveBlock = blockSituations[i][aboveLine];
          if (aboveBlock == null) {
            comboBreak = true;
            replacedBy[i][aboveLine - comboHeight] = new Point(i, aboveLine);
            continue;
          }
          if (aboveBlock.isCombo()) {
            if (comboBreak) {
              break;
            }
            comboHeight++;
            continue;
          }
          if (!aboveBlock.getType().movable) {
            break;
          }
          if (aboveBlock.getState() != BlockState.IDLE) {
            break;
          }
          comboBreak = true;
          replacedBy[i][aboveLine - comboHeight] = new Point(i, aboveLine);
        }
        j = aboveLine - 1;
      }
    }
  }

  // Evaluate a move cost by simulating it in a cloned panel
  int evaluateMoveCost(final int sourceColumn, final int destinationColumn, final int line, final PanelSituation panelSituation) {
    if (!BlockSituation.isMovable(panelSituation.getBlockSituations()[sourceColumn][line])) {
      // If source position is not computable, infinite cost
      return INFINITY;
    }
    if (!BlockSituation.isMovable(panelSituation.getBlockSituations()[destinationColumn][line])) {
      // If destination position is not computable, infinite cost
      return INFINITY;
    }
    if (sourceColumn < destinationColumn) {
      final PanelSituationHelper simulation = new PanelSituationHelper(panelSituation.getSimpleClone());
      if (!simulation.blockSwitchAndDetectFalling(sourceColumn, line)) {
        return INFINITY;
      }
      if (simulation.detectCombo()) {
        return INFINITY;
      }
      return 1 + evaluateMoveCost(sourceColumn + 1, destinationColumn, line, simulation.getPanelSituation());
    }
    if (sourceColumn > destinationColumn) {
      final PanelSituationHelper simulation = new PanelSituationHelper(panelSituation.getSimpleClone());
      if (!simulation.blockSwitchAndDetectFalling(sourceColumn - 1, line)) {
        return INFINITY;
      }
      if (simulation.detectCombo()) {
        return INFINITY;
      }
      return 1 + evaluateMoveCost(sourceColumn - 1, destinationColumn, line, simulation.getPanelSituation());
    }
    // if (sourceColumn == destinationColumn)
    return 0;
  }

  // Evaluate a move cost by simulating it in a cloned panel
  static PanelSituation simulateMove(final int sourceColumn, final int destinationColumn, final int line, final PanelSituation panelSituation) {
    if (!BlockSituation.isMovable(panelSituation.getBlockSituations()[sourceColumn][line])) {
      // If source position is not computable, infinite cost
      return null;
    }
    if (!BlockSituation.isMovable(panelSituation.getBlockSituations()[destinationColumn][line])) {
      // If destination position is not computable, infinite cost
      return null;
    }
    if (sourceColumn < destinationColumn) {
      final PanelSituationHelper simulation = new PanelSituationHelper(panelSituation.getSimpleClone());
      if (!simulation.blockSwitchAndDetectFalling(sourceColumn, line)) {
        return null;
      }
      if (simulation.detectCombo()) {
        return null;
      }
      return simulateMove(sourceColumn + 1, destinationColumn, line, simulation.getPanelSituation());
    }
    if (sourceColumn > destinationColumn) {
      final PanelSituationHelper simulation = new PanelSituationHelper(panelSituation.getSimpleClone());
      if (!simulation.blockSwitchAndDetectFalling(sourceColumn - 1, line)) {
        return null;
      }
      if (simulation.detectCombo()) {
        return null;
      }
      return simulateMove(sourceColumn - 1, destinationColumn, line, simulation.getPanelSituation());
    }
    // if (sourceColumn == destinationColumn)
    return panelSituation;
  }

  // Evaluate all blocks on line "line" and check if they can reach the specific target column on this line
  // Results are stored in the BlockSituationEx attributes (origin and cost) if applicable
  // Otherwise, the block is left unchanged
  List<BlockSituationEx> getCandidates(final int targetColumn, final int line, final boolean comboReplacement, final PanelSituation panelSituation) {
    final List<BlockSituationEx> candidates = new ArrayList<BlockSituationEx>();
    for (int sourceColumn = 0; sourceColumn < blockSituations.length; sourceColumn++) {
      final BlockSituationEx current = blockSituations[sourceColumn][line] == null ? BlockSituationEx.newInvisibleBlock() : blockSituations[sourceColumn][line];
      final int cost = evaluateMoveCost(sourceColumn, targetColumn, line, panelSituation);
      if (cost < INFINITY) {
        current.setOrigin(new Point(sourceColumn, line));
        current.addTargetCost(targetColumn, cost);
        current.setChainReplacement(comboReplacement);
        candidates.add(current);
      }
    }
    return candidates;
  }

  private static HorizontalCombo detectHorizontalChainComboInCombination(final BlockSituationEx[] combination) {
    for (int x = 0; x < combination.length; x++) {
      // Check each block of the panel
      final BlockSituationEx current = combination[x];

      // null, go on
      if (current == null || current.getState() != BlockState.IDLE || !current.getType().movable || !current.getType().combinable) {
        continue;
      }

      // Check right
      int xidem = 1;
      for (int right = x + 1; right < combination.length; right++) {
        final BlockSituation rightBlock = combination[right];

        // null, just break
        if (rightBlock == null || rightBlock.getState() != BlockState.IDLE || rightBlock.getType() != current.getType()) {
          break;
        }
        xidem++;
      }

      if (xidem >= 3) {
        final HorizontalCombo combo = new HorizontalCombo(combination);
        // A combo is a chain combo if it contains at least 1 and at most 2 replacements to be valid
        // Less replacement means it's not a combo, and more than 3 is would trigger a new combo
        int chainReplacementCount = 0;
        for (int i = 0; i < combination.length; i++) {
          final BlockSituationEx block = combo.getBlocks()[i];
          if (block == null) {
            continue;
          }
          if ((i < x) || (i >= (x + xidem))) {
            // Tag the blocks that are not part of the combo as "static" blocks
            // By setting their origin to null (i.e. not gonna be replaced)
            block.setOrigin(null);
          } else if (block.isChainReplacement()) {
            chainReplacementCount++;
          }
        }
        if ((chainReplacementCount == 1) || (chainReplacementCount == 2)) {
          combo.setSize(xidem);
          return combo;
        }
      }
    }
    return null;
  }

  private Point checkHorizontalChainCombos() {
    selectedCombo = null;

    // Line based algorithm
    for (int line = 1; line < blockSituations[0].length; line++) {
      if (selectedCombo != null) {
        break;
      }
      // First check if the line contains at least one replacement and at most Panel.X - 1
      // else, just skip it
      int containsReplacement = 0;
      for (int x = 0; x < blockSituations.length; x++) {
        if (replacedBy[x][line] != null) {
          containsReplacement++;
        }
      }
      if ((containsReplacement == 0) || (containsReplacement == blockSituations.length)) {
        continue;
      }

      // Resets all replacement values on this line, as we're gonna compute them,
      // As we're parsing the panel upward line by line, we might have already processed
      // this line for a combo lower in the panel that didn't make it
      for (final BlockSituationEx[] blockSituation : blockSituations) {
        final BlockSituationEx current = blockSituation[line];
        if (current == null) {
          continue;
        }
        current.reset();
      }

      final LineCombinator combinator = new LineCombinator(blockSituations.length);
      for (int x = 0; x < blockSituations.length; x++) {
        final BlockSituationEx current = blockSituations[x][line];
        if (current == null) {
          // Allow the combinator to combine even with empty slots
          combinator.combine(Arrays.asList(BlockSituationEx.newInvisibleBlock()));
          continue;
        }

        // Check if the current block on the line is part of a combo, i.e., has a replacement value
        final Point replacement = replacedBy[x][line];
        if (replacement == null) {
          // This block is not going to be replaced by a falling block, however, get all the candidates on the
          // current line that can replace this block. It makes more combination possible
          combinator.combine(getCandidates(x, line, false, panelSituation));
          // combinator.combine(Arrays.asList(current));
          continue;
        }

        // If yes:
        // - First, list all the candidate blocks that can replace this one,
        // - Then combine all these blocks with the already existing combinations,
        // Note that possible replacement blocks already part of a combination are excluded from that combination
        combinator.combine(getCandidates(replacement.x, replacement.y, true, panelSituation));
      }

      // LOG.info("resulting combinations count = " + combinator.getCombinations().size());
      // for (final BlockSituationEx[] combination : combinator.getCombinations()) {
      // final StringBuilder builder = new StringBuilder();
      // for (int i = 0; i < combination.length; i++) {
      // builder.append(combination[i].getType()).append(" ");
      // }
      // LOG.info(builder.toString());
      // }
      // LOG.info("--- done ---");

      // We finished this line processing.
      // Now simulate all possible block combinations for this line
      final List<HorizontalCombo> chainCombos = new ArrayList<HorizontalCombo>();
      for (final BlockSituationEx[] combination : combinator.getCombinations()) {
        final HorizontalCombo combo = detectHorizontalChainComboInCombination(combination);
        if (combo == null) {
          continue;
        }
        combo.setLine(line); // Only for renderer
        chainCombos.add(combo);
      }

      // If we've found several chain combos for this line, select the biggest one, then the cheapest one
      int biggestCombo = 0;
      int cheapestCost = INFINITY;
      for (final HorizontalCombo chainCombo : chainCombos) {
        if ((chainCombo.getSize() >= biggestCombo) && (chainCombo.cost() < cheapestCost) && chainCombo.isPossible(panelSituation)) {
          cheapestCost = chainCombo.cost();
          biggestCombo = chainCombo.getSize();
          selectedCombo = chainCombo;
        }
      }
    }
    return selectedCombo == null ? null : selectedCombo.perform();
  }

  public final boolean isComboInProgress() {
    return selectedCombo != null;
  }

  public final int getHighestOriginLine() {
    return selectedCombo == null ? 1 : selectedCombo.getHighestOriginLine();
  }
}
