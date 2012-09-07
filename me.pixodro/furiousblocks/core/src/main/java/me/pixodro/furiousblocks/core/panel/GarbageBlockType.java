package me.pixodro.furiousblocks.core.panel;

public interface GarbageBlockType {
  // Height = 1
  int UPLEFTDOWN = 1;
  int UPDOWN = 2;
  int UPRIGHTDOWN = 3;

  // Height = 2
  int UPLEFT = 4;
  int UP = 5;
  int UPRIGHT = 6;
  int DOWNLEFT = 7;
  int DOWN = 8;
  int DOWNRIGHT = 9;

  // Height > 2
  int LEFT = 10;
  int PLAIN = 11;
  int RIGHT = 12;
}
