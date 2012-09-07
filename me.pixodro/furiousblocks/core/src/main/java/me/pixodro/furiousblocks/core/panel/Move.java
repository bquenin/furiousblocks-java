package me.pixodro.furiousblocks.core.panel;

public class Move {
  private final byte type;

  public Move(final byte type) {
    this.type = type;
  }

  public Move(final Move move) {
    type = move.type;
  }

  public byte getType() {
    return type;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + type;
    return result;
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
    final Move other = (Move) obj;
    return type == other.type;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("Move [type=");
    builder.append(type);
    builder.append("]");
    return builder.toString();
  }
}
