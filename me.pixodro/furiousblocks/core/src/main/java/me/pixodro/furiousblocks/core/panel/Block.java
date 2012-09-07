package me.pixodro.furiousblocks.core.panel;

import me.pixodro.furiousblocks.core.FuriousBlocksCoreDefaults;
import me.pixodro.furiousblocks.core.situations.BlockSituation;

public class Block implements GarbageBlockType {
  private final int id;
  private final BlockType type;
  private BlockState state;
  private int stateTick;

  private int garbageBlockType;
  private int garbageOwner;
  private int poppingIndex;
  private int poppingSkillChainLevel;
  private boolean combo;
  private boolean fallingFromClearing;
  private boolean justLand;
  private Clearing clearing;

  Block(final int id, final BlockType type, final int index, final int skillChainLevel) {
    this.id = id;
    this.type = type;

    idle();
    fallingFromClearing = false;
    justLand = false;
    poppingIndex = index;
    poppingSkillChainLevel = skillChainLevel;
  }

  public int getId() {
    return id;
  }

  public BlockType getType() {
    return type;
  }

  public BlockState getState() {
    return state;
  }

  public int getStateTick() {
    return stateTick;
  }

  public void setGarbageOwner(final int garbageOwner) {
    this.garbageOwner = garbageOwner;
  }

  public boolean isFallingFromClearing() {
    return fallingFromClearing;
  }

  public void setFallingFromClearing(final boolean fallingFromClearing) {
    this.fallingFromClearing = fallingFromClearing;
  }

  public void setCombo() {
    this.combo = true;
  }

  public int getGarbageBlockType() {
    return garbageBlockType;
  }

  public void setGarbageBlockType(final int garbageBlockType) {
    this.garbageBlockType = garbageBlockType;
  }

  public int getPoppingIndex() {
    return poppingIndex;
  }

  public void setPoppingIndex(final int poppingIndex) {
    this.poppingIndex = poppingIndex;
  }

  public int getPoppingSkillChainLevel() {
    return poppingSkillChainLevel;
  }

  public void setPoppingSkillChainLevel(final int poppingSkillChainLevel) {
    this.poppingSkillChainLevel = poppingSkillChainLevel;
  }

  public void setJustLand() {
    this.justLand = false;
  }

  public boolean hasJustLand() {
    return justLand;
  }

  public void idle() {
    state = BlockState.IDLE;
    stateTick = 0;
    poppingIndex = 0;
    poppingSkillChainLevel = 0;
  }

  public void switchBack() {
    state = BlockState.SWITCHING_BACK;
    stateTick = me.pixodro.furiousblocks.core.FuriousBlocksCoreDefaults.BLOCK_SWITCHINGTIME;
  }

  public void switchForth() {
    state = BlockState.SWITCHING_FORTH;
    stateTick = me.pixodro.furiousblocks.core.FuriousBlocksCoreDefaults.BLOCK_SWITCHINGTIME;
  }

  public void hover() {
    state = BlockState.HOVERING;
    stateTick = FuriousBlocksCoreDefaults.BLOCK_HOVERINGTIME;
  }

  public void fall() {
    state = BlockState.FALLING;
    stateTick = 0;
  }

  public void land() {
    justLand = true;
    idle();
  }

  public void blink() {
    state = BlockState.BLINKING;
    stateTick = FuriousBlocksCoreDefaults.BLOCK_BLINKINGTIME;
  }

  public void explode(final int explodingTime) {
    state = BlockState.EXPLODING;
    stateTick = explodingTime;
  }

  public void reveal(final int revealingTime) {
    state = BlockState.REVEALING;
    stateTick = revealingTime;
  }

  public void airBounce() {
    state = BlockState.AIRBOUNCING;
    stateTick = FuriousBlocksCoreDefaults.BLOCK_AIRBOUNCINGTIME;
  }

  public void delete() {
    state = BlockState.TO_DELETE;
  }

  public PanelEvent update() {
    PanelEvent event = null;
    if (stateTick > 0) {
      stateTick--;
    }

    if (stateTick <= 0) {
      switch (state) {
        case BLINKING:
          state = BlockState.DONE_BLINKING;
          break;

        case SWITCHING_FORTH:
          state = BlockState.DONE_SWITCHING_FORTH;
          break;

        case HOVERING:
          state = BlockState.DONE_HOVERING;
          break;

        case EXPLODING:
          event = new PanelEvent(PanelEventType.BLOCK_POP);
          state = BlockState.DONE_EXPLODING;
          break;

        case REVEALING:
          if (stateTick == 0) {
            event = new PanelEvent(PanelEventType.BLOCK_POP);
          }
          state = BlockState.DONE_REVEALING;
          break;

        case AIRBOUNCING:
          state = BlockState.DONE_AIRBOUNCING;
          break;

        case DONE_BLINKING:
        case DONE_EXPLODING:
        case DONE_REVEALING:
        case DONE_SWITCHING_FORTH:
        case DONE_HOVERING:
        case IDLE:
        case TO_DELETE:
        case FALLING:
        case SWITCHING_BACK:
        case DONE_AIRBOUNCING:
          break;
        default:
          throw new IllegalStateException("Undefined block state: " + state);
      }
    }
    return event;
  }

  public BlockSituation getSituation() {
    return new BlockSituation(id, type, state, stateTick, garbageBlockType, garbageOwner, combo, justLand, fallingFromClearing, poppingIndex);
  }

  public static boolean isComputable(final Block block) {
    return block != null && block.getType().movable && block.getState() == BlockState.IDLE;
  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Block other = (Block) obj;
    return id == other.id;
  }

  public void setClearing(final Clearing clearing) {
    this.clearing = clearing;
  }

  public Clearing getClearing() {
    return clearing;
  }
}
