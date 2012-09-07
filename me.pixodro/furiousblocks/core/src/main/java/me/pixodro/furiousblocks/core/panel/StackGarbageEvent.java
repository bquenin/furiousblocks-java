/**
 *
 */
package me.pixodro.furiousblocks.core.panel;

/**
 * @author tsug
 */
public class StackGarbageEvent {
  public final byte width;
  public final byte height;
  public final int owner;
  public final boolean skill;

  public StackGarbageEvent(final byte width, final byte height, final int owner, final boolean skill) {
    super();
    this.width = width;
    this.height = height;
    this.owner = owner;
    this.skill = skill;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("StackGarbageEvent [width=");
    builder.append(width);
    builder.append(", height=");
    builder.append(height);
    builder.append(", owner=");
    builder.append(owner);
    builder.append(", skill=");
    builder.append(skill);
    builder.append("]");
    return builder.toString();
  }

}
