package me.pixodro.furiousblocks.core.panel;

public enum BlockType {
  YELLOW(true, true), //
  GREEN(true, true), //
  RED(true, true), //
  PURPLE(true, true), //
  BLUE(true, true), //
  GARBAGE(false, false), //
  INVISIBLE(true, false), //
  TUTORIAL(true, false);

  public final boolean movable;
  public final boolean combinable;

  private BlockType(final boolean movable, final boolean combinable) {
    this.movable = movable;
    this.combinable = combinable;
  }
}
