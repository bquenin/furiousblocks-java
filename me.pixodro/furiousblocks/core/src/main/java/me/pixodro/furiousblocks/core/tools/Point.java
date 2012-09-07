package me.pixodro.furiousblocks.core.tools;

public class Point {
  public int x;
  public int y;

  public Point() {
    this(0, 0);
  }

  public Point(Point p) {
    this(p.x, p.y);
  }

  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public boolean equals(Object obj) {
    if (obj instanceof Point) {
      Point pt = (Point) obj;
      return (x == pt.x) && (y == pt.y);
    }
    return super.equals(obj);
  }

  public String toString() {
    return getClass().getName() + "[x=" + x + ",y=" + y + "]";
  }
}