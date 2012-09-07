package me.pixodro.furiousblocks.core.network.codec.messages;

import java.util.LinkedList;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import me.pixodro.furiousblocks.core.panel.StackGarbageEvent;

public class GameGarbageEventList extends FuriousBlocksMessage {
  public int playerId;
  public long tick;
  public LinkedList<StackGarbageEvent> garbageEvents;

  public GameGarbageEventList() {
    super(FuriousBlocksMessageType.GAME_GARBAGEEVENTLIST);
  }

  public GameGarbageEventList(final int playerId, final long tick, final LinkedList<StackGarbageEvent> garbageEvents) {
    super(FuriousBlocksMessageType.GAME_GARBAGEEVENTLIST);
    this.playerId = playerId;
    this.tick = tick;
    this.garbageEvents = garbageEvents;
  }

  @Override
  public byte[] encode() {
    return ProtostuffIOUtil.toByteArray(this, RuntimeSchema.getSchema(GameGarbageEventList.class), LinkedBuffer.allocate(bufferLength));
  }
}
