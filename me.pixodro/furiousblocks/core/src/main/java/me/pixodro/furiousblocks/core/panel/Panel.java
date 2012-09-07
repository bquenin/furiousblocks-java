package me.pixodro.furiousblocks.core.panel;

import java.util.*;

import me.pixodro.furiousblocks.core.situations.BlockSituation;
import me.pixodro.furiousblocks.core.situations.ComboSituation;
import me.pixodro.furiousblocks.core.situations.GarbageSituation;
import me.pixodro.furiousblocks.core.situations.PanelSituation;
import me.pixodro.furiousblocks.core.tools.Point;
import me.pixodro.furiousblocks.core.tools.SimpleRNG;

import static me.pixodro.furiousblocks.core.FuriousBlocksCoreDefaults.*;
import static me.pixodro.furiousblocks.core.panel.BlockState.IDLE;
import static me.pixodro.furiousblocks.core.panel.BlockType.*;

public class Panel {
  protected static final int X = PANEL_WIDTH;
  private static final int Y_DISPLAY = PANEL_HEIGHT;
  protected static final int Y = Y_DISPLAY + (Y_DISPLAY * 4);

  private static final int INITIAL_SCROLLING_SPEED = (int) CORE_FREQUENCY;
  private static final long NEXT_LEVEL = (long) (CORE_FREQUENCY * 1000); // Next level every minute

  // 5 is the number of "regular" blocks type
  public static final int numberOfRegularBlocks = 5;
  private transient int lastIndex = -1;
  private transient final SimpleRNG random;

  //  private transient final Set<PanelEvent> events = new HashSet<PanelEvent>();
  private transient long localTick = 0;

  protected final Block[][] blocks = new Block[X][Y];
  private final int playerId;
  private final Point cursor = new Point();

  private final List<Combo> combos = new ArrayList<Combo>();
  private final List<Garbage> garbages = new ArrayList<Garbage>();
  private final Set<Clearing> clearings = new HashSet<Clearing>();
  private final List<Garbage> garbageStack = new LinkedList<Garbage>();

  private PanelState state = PanelState.IDLE;
  private int stateTick = 0;

  private int levelScrollingSpeed;
  private int scrollingSpeed;
  private int scrollingDelta;
  private int freezingTime = 0;
  private int bonusFreezingTime = 0;
  private int skillChainLevel = 1;
  private Move move = null;
  private boolean locked = false;
  private boolean lifting = false;
  private boolean gracing = false;
  private boolean gameOver = false;
  private int wallOffset = 0;

  private int score = 0;
  private final PanelListener panelListener;
  public boolean scrollingEnabled = true;

  public Panel(final int seed, final int playerId, final BlockType[][] initialBlockTypes) {
    this(seed, playerId, initialBlockTypes, null);
  }

  public Panel(final int seed, final int playerId, final BlockType[][] initialBlockTypes, final PanelListener panelListener) {
    this.playerId = playerId;
    this.panelListener = panelListener;

    random = new SimpleRNG(seed);
    scrollingSpeed = levelScrollingSpeed = INITIAL_SCROLLING_SPEED;

    // cursor
    cursor.x = (X / 2) - 1;
    cursor.y = (Y_DISPLAY / 2) - 1;

    if (initialBlockTypes != null) {
      for (int y = 1; y < PANEL_HEIGHT; y++) {
        for (int x = 0; x < PANEL_WIDTH; x++) {
          blocks[x][y] = initialBlockTypes[x][y] == null ? null : newBlock(initialBlockTypes[x][y]);
        }
      }
    }

    for (int x = 0; x < X; x++) {
      blocks[x][0] = blocks[x][1] == null ? newRandom(null) : newRandom(blocks[x][1].getType());
    }
  }

  public void reset() {
    freezingTime = 0;
    bonusFreezingTime = 0;
    skillChainLevel = 1;
    move = null;
    locked = false;
    lifting = false;
    gracing = false;
    gameOver = false;
    wallOffset = 0;

    combos.clear();
    garbages.clear();
    clearings.clear();
    garbageStack.clear();

    stateTick = 0;
    state = PanelState.IDLE;

    for (int y = 1; y < Y; y++) {
      for (int x = 0; x < X; x++) {
        blocks[x][y] = null;
      }
    }
  }

  public void setTransposedBlocks(final BlockType[][] initialBlockTypes) {
    for (int y = 0; y < initialBlockTypes[0].length; y++) {
      for (int x = 0; x < initialBlockTypes.length; x++) {
        blocks[y][x] = initialBlockTypes[x][y] == null ? null : newBlock(initialBlockTypes[x][y]);
      }
    }
    for (int x = 0; x < X; x++) {
      blocks[x][0] = blocks[x][1] == null ? newRandom(null) : newRandom(blocks[x][1].getType());
    }
  }

  protected Block newRandom(final BlockType excludedType) {
    return newRandom(excludedType, 0, 0);
  }

  Block newRandom(final BlockType excludedType, final int poppingIndex, final int skillChainLevel) {
    final int randomIndex = random.nextInt() % numberOfRegularBlocks;
    int index = randomIndex == lastIndex ? (randomIndex + 1) % numberOfRegularBlocks : randomIndex;
    if (values()[index] == excludedType) {
      index = (index + 1) % numberOfRegularBlocks;
    }
    return newBlock(values()[lastIndex = index], poppingIndex, skillChainLevel);
  }

  protected Block newBlock(final BlockType blockType) {
    return newBlock(blockType, 0, 0);
  }

  Block newBlock(final BlockType blockType, final int index, final int skillChainLevel) {
    return new Block(random.nextInt(), blockType, index, skillChainLevel);
  }

  public PanelSituation onTick(final long tick) {
    if (stateTick > 0) {
      stateTick--;
    }

    if (stateTick == 0) {
      switch (state) {
        case QUAKING:
          state = PanelState.IDLE;
          break;

        case GAMEOVER_PHASE1:
          state = PanelState.GAMEOVER_PHASE2;
          stateTick = PANEL_QUAKINGTIME - 1;
          break;

        case GAMEOVER_PHASE2:
          state = PanelState.GAMEOVER_PHASE3;
          stateTick = (int) (2 * CORE_FREQUENCY);
          break;

        case GAMEOVER_PHASE3:
          break;

        default:
          break;
      }
    }

    switch (state) {
      case QUAKING:
      case IDLE:
        processMove();

        mechanics(tick);
        final Combo currentCombo = detectCombo();
        if (currentCombo.size() > 0) {
          processCombo(currentCombo);
        } // else delete combo

        scrolling(tick);

        // Drop garbage if required
        //        if ((getSkillChainLevel() == 1)) {
        dropGarbages();
        //        }
        break;

      case GAMEOVER_PHASE1:
        if ((stateTick % 4) == 0) {
          // Game over phase 1: making compressed blocks "explode"
          for (int i = 0; i < X; i++) {
            // Column is compressed if a block has reach Y_DISPLAY height
            if (blocks[i][Y_DISPLAY] == null) {
              continue;
            }

            for (int j = Y_DISPLAY; j > 0; j--) {
              final Block current = blocks[i][j];
              if (!Block.isComputable(current)) {
                continue;
              }
              current.explode(-1);
              break;
            }
          }
        }
        break;

      case GAMEOVER_PHASE2:
        break;

      case GAMEOVER_PHASE3:
        if ((stateTick % 2) == 0) {
          if (wallOffset != Y_DISPLAY) {
            wallOffset++;
          }
        }
        break;

      default:
        throw new IllegalStateException("Undefined panel state: " + state);
    }
    return getSituation();
  }

  private void processMove() {
    if (move == null) {
      return;
    }
    final int type = move.getType();
    switch (type) {
      case MoveType.BLOCK_SWITCH:
        Block src = blocks[cursor.x][cursor.y];
        Block dst = blocks[cursor.x + 1][cursor.y];
        final Block aboveSrc = blocks[cursor.x][cursor.y + 1];
        final Block aboveDst = blocks[cursor.x + 1][cursor.y + 1];
        if ((src == null) && (dst == null)) {
          break;
        }
        if (src != null) {
          if (!Block.isComputable(src)) {
            break;
          }
        }
        if (dst != null) {
          if (!Block.isComputable(dst)) {
            break;
          }
        }

        // Swap column fix: prevent switch if the above block is switching or suspending
        if ((dst == null) && (aboveDst != null)) {
          if ((aboveDst.getState() == BlockState.SWITCHING_BACK) || (aboveDst.getState() == BlockState.SWITCHING_FORTH) || (aboveDst.getState() == BlockState.HOVERING)) {
            break;
          }
        }
        if ((src == null) && (aboveSrc != null)) {
          if ((aboveSrc.getState() == BlockState.SWITCHING_BACK) || (aboveSrc.getState() == BlockState.SWITCHING_FORTH) || (aboveSrc.getState() == BlockState.HOVERING)) {
            break;
          }
        }

        if (src == null) {
          src = blocks[cursor.x][cursor.y] = newBlock(INVISIBLE);
        }
        if (dst == null) {
          dst = blocks[cursor.x + 1][cursor.y] = newBlock(INVISIBLE);
        }
        src.switchForth();
        dst.switchBack();
        if (panelListener != null) {
          panelListener.onEvent(playerId, new PanelEvent(PanelEventType.CURSOR_SWAP));
        }
        break;

      case MoveType.CURSOR_DOWN:
        if (cursor.y != 1) {
          cursor.y--;
          if (panelListener != null) {
            panelListener.onEvent(playerId, new PanelEvent(PanelEventType.CURSOR_MOVE));
          }
        }
        break;
      case MoveType.CURSOR_LEFT:
        if (cursor.x != 0) {
          cursor.x--;
          if (panelListener != null) {
            panelListener.onEvent(playerId, new PanelEvent(PanelEventType.CURSOR_MOVE));
          }
        }
        break;
      case MoveType.CURSOR_RIGHT:
        if (cursor.x != (X - 2)) {
          cursor.x++;
          if (panelListener != null) {
            panelListener.onEvent(playerId, new PanelEvent(PanelEventType.CURSOR_MOVE));
          }
        }
        break;
      case MoveType.CURSOR_UP:
        if (cursor.y != (gracing ? Y_DISPLAY : Y_DISPLAY - 1)) {
          cursor.y++;
          if (panelListener != null) {
            panelListener.onEvent(playerId, new PanelEvent(PanelEventType.CURSOR_MOVE));
          }
        }
        break;
      case MoveType.LIFT:
        freeze(0);
        if (!lifting && !locked) {
          lifting = true;
          skillChainLevel = 1;
        }
        break;
    }
    move = null;
  }

  void dropGarbages() {
    for (final Garbage garbage : new LinkedList<Garbage>(garbageStack)) {
      final int y = (Y_DISPLAY + garbage.height) - 1;
      for (int h = 0, j = y; h < garbage.height; h++, j--) {
        for (int i = 0, w = 0; w < garbage.width; i++, w++) {
          if (blocks[i][j] != null) {
            return;
          }
        }
      }
      garbageStack.remove(garbage);
      final int xPos = garbage.width < X ? random.nextInt() % (X - garbage.width) : 0;
      garbage.inject(xPos, y);
    }
  }

  private void scrolling(final long tick) {
    // Update level scrolling speed
    if (((tick % NEXT_LEVEL) == 0) && (levelScrollingSpeed > 1) && (tick > 0)) {
      levelScrollingSpeed /= 2;
    }

    if (locked) {
      return;
    }

    if (freezingTime > 0) {
      freezingTime--;
      return;
    }

    // Starting from here, we need to scroll
    gracePeriod();
    if (gracing) {
      return;
    }

    scrollingSpeed = lifting ? 1 : scrollingEnabled ? levelScrollingSpeed : Integer.MAX_VALUE;

    boolean newLine = false;
    if (tick % scrollingSpeed == 0) {
      scrollingDelta++;
      if (scrollingDelta >= BLOCK_LOGICALHEIGHT) {
        newLine = true;
      }
      scrollingDelta %= BLOCK_LOGICALHEIGHT;
    }

    // new line
    if (newLine) {
      newLine();
      gracePeriod();
      // scroll the cursor
      if (cursor.y != (gracing ? Y_DISPLAY : Y_DISPLAY - 1)) {
        cursor.y++;
      }
    }
  }

  private void gracePeriod() {
    // Grace period
    boolean topLineEmpty = true;
    for (final Block[] block : blocks) {
      if (block[Y_DISPLAY] != null) {
        topLineEmpty = false;
        break;
      }
    }

    if (!topLineEmpty) {
      // 1/ we need to scroll,
      // 2/ the top line of the panel is not empty
      // 3/ we are already in the grace period
      // Game Over !
      if (gracing) {
        garbageStack.clear();
        gameOver = true;
        state = PanelState.GAMEOVER_PHASE1;
        stateTick = PANEL_QUAKINGTIME - 1;
      } else {
        lifting = false;
        gracing = true;
        freeze((int) (CORE_FREQUENCY * 2));
      }
      return;
    }
    gracing = false;
  }

  void freeze(final int freezingTime) {
    this.freezingTime = freezingTime;
  }

  void newLine() {
    if (lifting) {
      freeze((int) CORE_FREQUENCY);
      scrollingDelta = 0;
    }
    lifting = false;

    // scroll the panel lines up
    for (int y = Y - 1; y > 0; y--) {
      for (int x = 0; x < X; x++) {
        blocks[x][y] = blocks[x][y - 1];
      }
    }

    // create the new line
    for (int x = 0; x < X; x++) {
      blocks[x][0] = blocks[x][1] == null ? newRandom(null) : newRandom(blocks[x][1].getType());
    }
  }

  public void stackGarbage(final Garbage garbage) {
    if (garbage.isSkill()) {
      for (final ListIterator<Garbage> iterator = garbageStack.listIterator(garbageStack.size()); iterator.hasPrevious(); ) {
        final Garbage stackedGarbage = iterator.previous();
        if (stackedGarbage.isSkill() && stackedGarbage.height == garbage.height - 1 && stackedGarbage.getOwner() == garbage.getOwner()) {
          iterator.set(garbage);
          return;
        }
      }
    }
    garbageStack.add(garbage);
  }

  final void quake() {
    state = PanelState.QUAKING;
    stateTick = PANEL_QUAKINGTIME - 1;
  }

  private void mechanics(final long tick) {
    locked = false;
    int revealingTime = BLOCK_REVEALINGTIMEBASE;
    for (int y = 1; y < Y; y++) {
      for (int x = 0; x < X; x++) {
        // Check each block of the panel
        final Block current = blocks[x][y];

        // null, go on
        if (current == null) {
          continue;
        }

        // Clear the flag
        if (current.hasJustLand()) {
          current.setJustLand();
          current.setFallingFromClearing(false);
        }

        final PanelEvent event = current.update();
        if (event != null) {
          event.data1 = current.getPoppingSkillChainLevel();
          event.data2 = current.getPoppingIndex();
          event.data3 = tick;
          if (panelListener != null) {
            panelListener.onEvent(playerId, event);
          }
        }

        final BlockType type = current.getType();
        final BlockState state = current.getState();

        switch (state) {
          case DONE_SWITCHING_FORTH:
            blocks[x][y] = blocks[x + 1][y];
            blocks[x + 1][y] = current;

            blocks[x][y].idle();
            blocks[x + 1][y].idle();

            if (blocks[x][y].getType() == INVISIBLE) {
              blocks[x][y].delete();
            }
            if (blocks[x + 1][y].getType() == INVISIBLE) {
              blocks[x + 1][y].delete();
            }
            break;

          case TO_DELETE:
            // Skill chain management:
            // A block has to be deleted (a clearing has been completed), set the above blocks (that are gonna fall) eligible for
            // a skill chain
            if (type != INVISIBLE) {
              for (int k = y + 1; k < Y; k++) {
                // If the block is not computable just exit the loop
                if (!Block.isComputable(blocks[x][k])) {
                  break;
                }
                // else, flag it as falling from clearing
                blocks[x][k].setFallingFromClearing(true);
              }
            }
            // delete the block
            blocks[x][y] = null;
            break;

          case IDLE:
            switch (type) {
              case BLUE:
              case GREEN:
              case PURPLE:
              case RED:
              case YELLOW:
              case TUTORIAL:
                if (blocks[x][y - 1] == null) {  // Does the block have to hover ?
                  current.hover();
                }
                break;
              case GARBAGE:
                final Garbage garbage = getGarbageByBlock(current);
                if (garbage.hasToFall(x, y)) { // Does the garbage have to fall ?
                  garbage.fall(x, y);
                }
                x += garbage.width - 1; // Necessary trick
                break;
            }
            break;

          case HOVERING:
            break;

          case DONE_AIRBOUNCING:
          case DONE_HOVERING:
            if (blocks[x][y - 1] == null) {
              for (int k = y + 1; k < Y; k++) {
                if (blocks[x][k] == null || !blocks[x][k].getType().movable || blocks[x][k].getType() == GARBAGE || blocks[x][k].getState() != IDLE) {
                  break;
                }
                blocks[x][k].fall();
              }
              blocks[x][y - 1] = current;
              blocks[x][y] = null;
            } else {
              current.land();
            }
            break;

          case FALLING:
            switch (type) {
              case BLUE:
              case GREEN:
              case PURPLE:
              case RED:
              case YELLOW:
              case TUTORIAL:
                if (blocks[x][y - 1] == null) {
                  blocks[x][y - 1] = current;
                  blocks[x][y] = null;
                } else {
                  current.land();
                }
                break;
              case GARBAGE:
                final Garbage garbage = getGarbageByBlock(current); // Does the garbage have to fall ?
                if (garbage.hasToFall(x, y)) {
                  garbage.fall(x, y);
                } else {
                  garbage.idle();
                  quake();
                }
                // Necessary trick
                x += garbage.width - 1;
                break;
            }
            break;

          case DONE_BLINKING:
            switch (type) {
              case BLUE:
              case GREEN:
              case PURPLE:
              case RED:
              case YELLOW:
                final Combo combo = getComboByBlock(current);
                for (final Block block : combo.getBlocks()) {
                  if (block.getState() == BlockState.EXPLODING) {
                    break;
                  }
                  block.explode(revealingTime += BLOCK_REVEALINGTIMEINCREMENT);
                }
                break;
              case GARBAGE:
                final Clearing clearing = current.getClearing();
                final Garbage garbage = getGarbageByBlock(current);
                revealingTime = garbage.reveal(x, y, revealingTime, clearing);
                clearing.setRevealingTime(tick + revealingTime);
                clearing.removeBar(garbage);
                x += garbage.width - 1;
                break;
            }
            break;

          case DONE_EXPLODING:
            switch (type) {
              case BLUE:
              case GREEN:
              case PURPLE:
              case RED:
              case YELLOW:
                final Combo combo = getComboByBlock(current);
                boolean doneExploding = true;
                for (final Block block : combo.getBlocks()) {
                  if (block.getState() != BlockState.DONE_EXPLODING) {
                    doneExploding = false;
                    break;
                  }
                }

                if (doneExploding) {
                  combos.remove(combo);
                  for (final Block block : combo.getBlocks()) {
                    block.delete();
                  }
                  combo.getBlocks().clear();
                }
                break;
              case GARBAGE:
            }
            break;

          case DONE_REVEALING:
            final Clearing clearing = current.getClearing();
            if (clearing.isDoneRevealing(tick)) {
              clearing.onDoneRevealing();
              clearings.remove(clearing);
            }
            break;

          case EXPLODING:
          case BLINKING:
          case REVEALING:
          case AIRBOUNCING:
            locked = true;
            break;

          case SWITCHING_BACK:
          case SWITCHING_FORTH:
            break;

          default:
            throw new IllegalStateException("Unsupported state = " + state);
        }
      }
    }
  }

  private Combo getComboByBlock(final Block block) {
    for (final Combo combo : combos) {
      if (combo.contains(block)) {
        return combo;
      }
    }
    return null;
  }

  private Garbage getGarbageByBlock(final Block block) {
    for (final Garbage garbage : garbages) {
      if (garbage.contains(block)) {
        return garbage;
      }
    }
    return null;
  }

  Combo detectCombo() {
    final Combo currentCombo = new Combo(playerId);
    final boolean[][] comboMask = new boolean[X][Y];

    // First build the combo mask because we need to insert the blocks in a specific order to respect original game clearing order
    for (int y = 1; y < Y; y++) {
      for (int x = 0; x < X; x++) {
        // Check each block of the panel
        final Block current = blocks[x][y];

        // null, go on
        if (current == null || current.getState() != IDLE || !current.getType().movable || !current.getType().combinable) {
          continue;
        }

        // Check right
        int xIdem = 1;
        for (int right = x + 1; right < X; right++) {
          final Block rightBlock = blocks[right][y];
          if (rightBlock == null || rightBlock.getState() != IDLE || rightBlock.getType() != current.getType()) {
            break;
          }
          xIdem++;
        }

        // Check above
        int yIdem = 1;
        for (int above = y + 1; above < Y; above++) {
          final Block aboveBlock = blocks[x][above];
          if (aboveBlock == null || aboveBlock.getState() != IDLE || aboveBlock.getType() != current.getType()) {
            break;
          }
          yIdem++;
        }

        if (xIdem >= 3) {
          for (int k = x; k < (x + xIdem); k++) {
            comboMask[k][y] = true;
          }
        }

        if (yIdem >= 3) {
          for (int k = y; k < (y + yIdem); k++) {
            comboMask[x][k] = true;
          }
        }
      }
    }

    // Then insert the block following the original game clearing order: from top to bottom, and from left to right
    int poppingIndex = 0;
    for (int y = Y - 1; y > 0; y--) {
      for (int x = 0; x < X; x++) {
        if (!comboMask[x][y]) {
          continue;
        }
        currentCombo.addBlock(blocks[x][y]);
        blocks[x][y].setCombo();
        blocks[x][y].setPoppingIndex(poppingIndex++);
      }
    }
    return currentCombo;
  }

  private void processCombo(final Combo combo) {
    // A new combo has been performed, process it
    // Add the combo to the panel
    combos.add(combo);

    // Lock the panel as a combo clearing is starting
    locked = true;

    // A combo is a skill combo when the following 2 conditions are met:
    // - it involves at least a falling (DONE_FALLING) block and,
    // - the falling block is falling from a previous clear
    boolean isSkillCombo = false;
    for (final Block block : combo.getBlocks()) {
      isSkillCombo = block.isFallingFromClearing();
      if (isSkillCombo) {
        break;
      }
    }

    // Compute panel skill chain and assign it to the combo
    int comboSkillChainLevel = 1;
    if (isSkillCombo) {
      skillChainLevel++;
      comboSkillChainLevel = skillChainLevel;
      if (panelListener != null) {
        panelListener.onEvent(playerId, new PanelEvent(PanelEventType.SKILL_COMBO));
      }
      score += (1000 * skillChainLevel) + (combo.size() * 100);
    } else {
      if (clearings.isEmpty()) {
        skillChainLevel = 1;
      }
      score += combo.size() * 100;
      //      events.add(new PanelEvent(PanelEventType.SKILLCHAIN_END, skillChainLevel));
    }

    for (final Block block : combo.getBlocks()) {
      block.setPoppingSkillChainLevel(skillChainLevel);
    }
    combo.skillChainLevel = comboSkillChainLevel;

    for (final Block block : combo.getBlocks()) {
      block.blink();
    }

    // notify listener
    if (panelListener != null) {
      panelListener.onCombo(combo);
    }

    // Bonus freeze : the panel has an additional freezing time if the combo size is at least 4 or if the combo is a skill chain
    // bonus freezing time is expressed in seconds
    if ((combo.size() >= 4) || (combo.getSkillChainLevel() > 1)) {
      if (combo.size() >= 4) {
        bonusFreezingTime = (int) ((combo.size() / 2) * CORE_FREQUENCY);
      }
      if (combo.getSkillChainLevel() > 1) {
        if (combo.getSkillChainLevel() == 2) {
          bonusFreezingTime = (int) (5 * CORE_FREQUENCY);
        } else {
          bonusFreezingTime += (combo.getSkillChainLevel() / 2) + CORE_FREQUENCY;
        }
      }
      freeze(bonusFreezingTime);
    }

    // Check if a combo triggered a garbage blinking, and make it blink accordingly
    // All garbages cleared via the same combo form a "clearing"
    int poppingIndex = combo.size();
    final Clearing clearing = new Clearing();
    for (int y = 1; y < Y; y++) {
      for (int x = 0; x < X; x++) {
        // Check each block of the panel
        final Block current = blocks[x][y];

        // null, go on
        if (current == null || current.getState() != BlockState.BLINKING || current.getStateTick() != BLOCK_BLINKINGTIME) {
          continue;
        }

        // Check for garbage to blink above
        if (y + 1 < Y) {
          final Block aboveBlock = blocks[x][y + 1];
          final Garbage garbage = getGarbageByBlock(aboveBlock);
          if (aboveBlock != null && garbage != null) {
            poppingIndex = garbage.blink(poppingIndex, combo);
            clearing.addBlockBar(garbage);
          }
        }

        // Check for garbage to blink below
        if (y - 1 > 0) {
          final Block belowBlock = blocks[x][y - 1];
          final Garbage garbage = getGarbageByBlock(belowBlock);
          if (belowBlock != null && garbage != null) {
            poppingIndex = garbage.blink(poppingIndex, combo);
            clearing.addBlockBar(garbage);
          }
        }

        // Check for garbage to blink on the right
        if (x + 1 < X) {
          final Block rightBlock = blocks[x + 1][y];
          final Garbage garbage = getGarbageByBlock(rightBlock);
          if (rightBlock != null && garbage != null) {
            poppingIndex = garbage.blink(poppingIndex, combo);
            clearing.addBlockBar(garbage);
          }
        }

        // Check for garbage to blink on the left
        if (x - 1 > 0) {
          final Block leftBlock = blocks[x - 1][y];
          final Garbage garbage = getGarbageByBlock(leftBlock);
          if (leftBlock != null && garbage != null) {
            poppingIndex = garbage.blink(poppingIndex, combo);
            clearing.addBlockBar(garbage);
          }
        }
      }
    }

    if (!clearing.isEmpty()) {
      clearings.add(clearing);
    }
  }

  PanelSituation getSituation() {
    final BlockSituation[][] blockSituations = new BlockSituation[X][Y_DISPLAY + 1]; // Just give the player/renderer the visible blocks
    for (int y = 0; y < (Y_DISPLAY + 1); y++) {
      for (int x = 0; x < X; x++) {
        blockSituations[x][y] = blocks[x][y] == null ? null : blocks[x][y].getSituation();
      }
    }

    final List<ComboSituation> comboSituations = new ArrayList<ComboSituation>();
    for (final Combo combo : combos) {
      comboSituations.add(combo.getSituation());
    }

    final List<GarbageSituation> garbageSituations = new ArrayList<GarbageSituation>();
    for (final Garbage garbage : garbages) {
      garbageSituations.add(garbage.getSituation());
    }

    final List<GarbageSituation> garbageStackSituation = new ArrayList<GarbageSituation>();
    for (final Garbage garbage : garbageStack) {
      garbageStackSituation.add(garbage.getSituation());
    }

    return new PanelSituation(blockSituations, locked, comboSituations, new Point(cursor), scrollingDelta, state, stateTick, garbageSituations, garbageStackSituation, skillChainLevel, freezingTime, gameOver, wallOffset, gracing, score, !clearings.isEmpty());
  }

  public Garbage newGarbage(final int width, final int height, final int owner, final boolean skill) {
    return new Garbage(width, height, owner, skill);
  }

  /**
   * @author tsug
   */
  abstract class BlockBar {
    private final int id;
    final int width;
    final int height;
    final int owner;
    final Set<Block> barBlocks = new HashSet<Block>();

    private BlockBar(final int width, final int height, final int owner) {
      this.id = random.nextInt();
      this.width = width;
      this.height = height >= Y - Y_DISPLAY ? Y - Y_DISPLAY - 1 : height;
      this.owner = owner;
    }

    abstract void onDoneRevealing();

    public boolean contains(final Block block) {
      return barBlocks.contains(block);
    }

    public boolean hasToFall(final int xOrigin, final int yOrigin) {
      // Check if the whole garbage can fall
      for (int x = xOrigin; x < xOrigin + width && x < X; x++) {
        if (blocks[x][yOrigin - 1] != null) {
          return false;
        }
      }
      return true;
    }

    public void fall(final int xOrigin, final int yOrigin) {
      for (int y = yOrigin; y < yOrigin + height && y < Y; y++) {
        for (int x = xOrigin; x < xOrigin + width && x < X; x++) {
          //          blocks[x][y].fall();
          blocks[x][y - 1] = blocks[x][y];
          blocks[x][y] = null;
        }
      }
    }

    public void idle() {
      for (final Block block : barBlocks) {
        block.idle();
      }
    }

    public int blink(final int poppingIndex) {
      if (barBlocks.iterator().next().getState() == BlockState.BLINKING) {
        return poppingIndex;
      }
      int index = poppingIndex;
      for (Block block : barBlocks) {
        block.blink();
        block.setPoppingIndex(index++);
        block.setPoppingSkillChainLevel(skillChainLevel);
      }
      return index;
    }

    public boolean isRevealing() {
      for (Block block : barBlocks) {
        if (block.getState() == BlockState.REVEALING) {
          return true;
        }
      }
      return false;
    }

    @Override
    public int hashCode() {
      return id;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      BlockBar other = (BlockBar) obj;
      if (id != other.id) {
        return false;
      }
      return true;
    }
  }

  /**
   * @author tsug
   */
  class Garbage extends BlockBar {
    private final boolean skill;
    private transient Combo triggeringCombo;

    private Garbage(final int width, final int height, final int owner, final boolean skill) {
      super(width, height, owner);
      this.skill = skill;
    }

    public boolean isSkill() {
      return skill;
    }

    public int getOwner() {
      return owner;
    }

    void inject(final int x, final int y) {
      for (int j = y, h = 0; h < height; j--, h++) {
        for (int i = x, w = 0; w < width; i++, w++) {
          if (blocks[i][j] == null) {
            blocks[i][j] = newBlock(GARBAGE);
          } else {
            blocks[i][j] = newBlock(GARBAGE, blocks[i][j].getPoppingIndex(), blocks[i][j].getPoppingSkillChainLevel());
          }
          blocks[i][j].setGarbageOwner(owner);
          barBlocks.add(blocks[i][j]);
        }
      }

      garbages.add(this);
      switch (height) {
        case 1:
          for (int w = 1; w < (width - 1); w++) {
            blocks[x + w][y].setGarbageBlockType(GarbageBlockType.UPDOWN);
          }
          blocks[x][y].setGarbageBlockType(GarbageBlockType.UPLEFTDOWN);
          blocks[(x + width) - 1][y].setGarbageBlockType(GarbageBlockType.UPRIGHTDOWN);
          break;
        default:
          for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
              blocks[x + w][y - h].setGarbageBlockType(GarbageBlockType.PLAIN);
            }
          }

          blocks[x][y].setGarbageBlockType(GarbageBlockType.UPLEFT);
          blocks[x][y - (height - 1)].setGarbageBlockType(GarbageBlockType.DOWNLEFT);
          blocks[(x + width) - 1][y].setGarbageBlockType(GarbageBlockType.UPRIGHT);
          blocks[(x + width) - 1][y - (height - 1)].setGarbageBlockType(GarbageBlockType.DOWNRIGHT);

          if (width > 2) {
            for (int w = 1; w < (width - 1); w++) {
              blocks[x + w][y].setGarbageBlockType(GarbageBlockType.UP);
              blocks[x + w][y - (height - 1)].setGarbageBlockType(GarbageBlockType.DOWN);
            }
          }

          if (height > 2) {
            for (int h = 1; h < (height - 1); h++) {
              blocks[x][y - h].setGarbageBlockType(GarbageBlockType.LEFT);
              blocks[(x + width) - 1][y - h].setGarbageBlockType(GarbageBlockType.RIGHT);
            }
          }
          break;
      }
    }

    public int blink(final int poppingIndex, final Combo combo) {
      triggeringCombo = combo;
      return super.blink(poppingIndex);
    }

    public int reveal(final int xOrigin, final int yOrigin, final int revealingTime, final Clearing parentClearing) {
      if (isRevealing()) {
        return revealingTime;
      }
      garbages.remove(this);

      int revealingTimeIncrement = revealingTime;
      // Make the triggering combo explode first
      if (triggeringCombo != null) {
        for (final Block block : triggeringCombo.getBlocks()) {
          if (block.getState() == BlockState.EXPLODING) {
            break;
          }
          block.explode(revealingTimeIncrement += BLOCK_REVEALINGTIMEINCREMENT);
        }
      }

      // Inject the new line
      final BlockLine subLine = new BlockLine(width, owner);
      subLine.inject(xOrigin, yOrigin);
      parentClearing.addBlockBar(subLine);
      revealingTimeIncrement = subLine.reveal(xOrigin, yOrigin, revealingTimeIncrement);

      // Inject sub garbage
      if (height > 1) {
        final Garbage subGarbage = newGarbage(width, height - 1, owner, skill);
        subGarbage.inject(xOrigin, yOrigin + height - 1);
        parentClearing.addBlockBar(subGarbage);

        for (int y = yOrigin + 1; y < yOrigin + height && y < Y; y++) {
          for (int x = xOrigin; x < xOrigin + width && x < X; x++) {
            if (y <= Y_DISPLAY) {
              blocks[x][y].reveal(revealingTimeIncrement += BLOCK_REVEALINGTIMEINCREMENT);
            } else {
              blocks[x][y].reveal(-1);
            }
          }
        }
      }
      return revealingTimeIncrement;
    }

    @Override
    void onDoneRevealing() {
      for (Block block : barBlocks) {
        block.idle();
      }
    }

    public GarbageSituation getSituation() {
      List<Integer> blockIds = new ArrayList<Integer>();
      for (Block barBlock : barBlocks) {
        blockIds.add(barBlock.getId());
      }
      return new GarbageSituation(width, height, owner, blockIds);
    }
  }

  /**
   * @author tsug
   */
  class BlockLine extends BlockBar {
    private BlockLine(final int width, final int owner) {
      super(width, 1, owner);
    }

    void inject(final int x, final int y) {
      for (int j = y, h = 0; h < height; j--, h++) {
        for (int i = x, w = 0; w < width; i++, w++) {
          if (blocks[i][j] == null) {
            blocks[i][j] = newRandom(null);
          } else {
            blocks[i][j] = newRandom(null, blocks[i][j].getPoppingIndex(), blocks[i][j].getPoppingSkillChainLevel());
          }
          blocks[i][j].setGarbageOwner(owner);
          barBlocks.add(blocks[i][j]);
        }
      }
    }

    @Override
    void onDoneRevealing() {
      for (Block block : barBlocks) {
        block.airBounce();
        block.setFallingFromClearing(true);
      }
    }

    public int reveal(final int xOrigin, final int yOrigin, final int revealingTime) {
      if (isRevealing()) {
        return revealingTime;
      }

      int revealingTimeIncrement = revealingTime;
      for (int y = yOrigin; y < yOrigin + height && y < Y; y++) {
        for (int x = xOrigin; x < xOrigin + width && x < X; x++) {
          if (y <= Y_DISPLAY) {
            blocks[x][y].reveal(revealingTimeIncrement += BLOCK_REVEALINGTIMEINCREMENT);
          } else {
            blocks[x][y].reveal(-1);
          }
        }
      }
      return revealingTimeIncrement;
    }
  }

  public long getLocalTick() {
    return localTick;
  }

  public void setLocalTick(final long localTick) {
    this.localTick = localTick;
  }

  public boolean isGameOver() {
    return gameOver;
  }

  public void submitMove(final Move move) {
    this.move = new Move(move);
  }

  @Override
  public final int hashCode() {
    if (blocks == null) {
      return 0;
    }

    int hash = 1;
    for (int x = 0; x < X; x++) {
      for (int y = 0; y < Y; y++) {
        hash = 31 * hash + (blocks[x][y] == null ? 0 : blocks[x][y].getId());
      }
    }
    return hash;
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
    final Panel other = (Panel) obj;
    return blocks.hashCode() == other.hashCode();
  }
}
