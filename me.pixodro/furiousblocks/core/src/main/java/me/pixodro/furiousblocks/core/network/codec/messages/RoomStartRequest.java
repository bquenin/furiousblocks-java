package me.pixodro.furiousblocks.core.network.codec.messages;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class RoomStartRequest extends FuriousBlocksMessage {
  public int roomId;

  public RoomStartRequest() {
    super(FuriousBlocksMessageType.ROOM_STARTREQUEST);
  }

  public RoomStartRequest(final int roomId) {
    super(FuriousBlocksMessageType.ROOM_STARTREQUEST);
    this.roomId = roomId;
  }

  @Override
  public byte[] encode() {
    return ProtostuffIOUtil.toByteArray(this, RuntimeSchema.getSchema(RoomStartRequest.class), LinkedBuffer.allocate(bufferLength));
  }
}
