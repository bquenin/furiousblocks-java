package me.pixodro.furiousblocks.core.network.codec.messages;


import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import me.pixodro.furiousblocks.core.network.TickStatus;

public class GameDeltaUpdate extends FuriousBlocksMessage {
  public int playerId;
  public long tick;
  public TickStatus tickStatus;

  public GameDeltaUpdate() {
    super(FuriousBlocksMessageType.GAME_DELTAUPDATE);
  }

  public GameDeltaUpdate(final int playerId, final long tick, final TickStatus tickStatus) {
    super(FuriousBlocksMessageType.GAME_DELTAUPDATE);
    this.playerId = playerId;
    this.tick = tick;
    this.tickStatus = tickStatus;
  }

  @Override
  public byte[] encode() {
    return ProtostuffIOUtil.toByteArray(this, RuntimeSchema.getSchema(GameDeltaUpdate.class), LinkedBuffer.allocate(bufferLength));
  }
}
