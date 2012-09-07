package me.pixodro.furiousblocks.core.network.codec.messages;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class RoomPlayerReady extends FuriousBlocksMessage {
  public int roomId;

  public RoomPlayerReady() {
    super(FuriousBlocksMessageType.ROOM_PLAYERREADY);
  }

  public RoomPlayerReady(final int roomId) {
    super(FuriousBlocksMessageType.ROOM_PLAYERREADY);
    this.roomId = roomId;
  }

  @Override
  public byte[] encode() {
    return ProtostuffIOUtil.toByteArray(this, RuntimeSchema.getSchema(RoomPlayerReady.class), LinkedBuffer.allocate(bufferLength));
  }
}
