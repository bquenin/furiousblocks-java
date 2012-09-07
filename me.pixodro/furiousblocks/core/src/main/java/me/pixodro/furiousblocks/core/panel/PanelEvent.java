package me.pixodro.furiousblocks.core.panel;

public class PanelEvent {
  public final PanelEventType type;
  public int data1;
  public int data2;
  public long data3;

  public PanelEvent(final PanelEventType type) {
    this(type, 0, 0);
  }

  public PanelEvent(final PanelEventType type, final int data1) {
    this(type, data1, 0);
  }

  private PanelEvent(final PanelEventType type, final int data1, final int data2) {
    this.type = type;
    this.data1 = data1;
    this.data2 = data2;
  }

  @Override
  public final int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + data1;
    result = (prime * result) + data2;
    result = (prime * result) + ((type == null) ? 0 : type.hashCode());
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
    final PanelEvent other = (PanelEvent) obj;
    if (data1 != other.data1) {
      return false;
    }
    if (data2 != other.data2) {
      return false;
    }
    return type == other.type;
  }
}
