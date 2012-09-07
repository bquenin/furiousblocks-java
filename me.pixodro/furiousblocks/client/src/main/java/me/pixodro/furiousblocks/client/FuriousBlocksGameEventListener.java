package me.pixodro.furiousblocks.client;

import me.pixodro.furiousblocks.core.network.codec.messages.GameDeltaUpdate;
import me.pixodro.furiousblocks.core.network.codec.messages.GameGarbageEventList;

public interface FuriousBlocksGameEventListener {
  void onGameStart();

  void onGameDeltaUpdate(GameDeltaUpdate msg);

  void onGameGarbageEventList(GameGarbageEventList msg);
}
