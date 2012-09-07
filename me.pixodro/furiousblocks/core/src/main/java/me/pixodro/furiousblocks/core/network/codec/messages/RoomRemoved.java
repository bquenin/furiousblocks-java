package me.pixodro.furiousblocks.core.network.codec.messages;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class RoomRemoved extends FuriousBlocksMessage {
  public int roomId;

  public RoomRemoved() {
    super(FuriousBlocksMessageType.ROOM_REMOVED);
  }

  public RoomRemoved(final int roomId) {
    super(FuriousBlocksMessageType.ROOM_REMOVED);
    this.roomId = roomId;
  }

  @Override
  public byte[] encode() {
    return ProtostuffIOUtil.toByteArray(this, RuntimeSchema.getSchema(RoomRemoved.class), LinkedBuffer.allocate(bufferLength));
  }
}
