package me.pixodro.furiousblocks.core.network.codec.messages;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class RoomJoinRequest extends FuriousBlocksMessage {
  public int roomId;

  public RoomJoinRequest() {
    super(FuriousBlocksMessageType.ROOM_JOINREQUEST);
  }

  public RoomJoinRequest(final int roomId) {
    super(FuriousBlocksMessageType.ROOM_JOINREQUEST);
    this.roomId = roomId;
  }

  @Override
  public byte[] encode() {
    return ProtostuffIOUtil.toByteArray(this, RuntimeSchema.getSchema(RoomJoinRequest.class), LinkedBuffer.allocate(bufferLength));
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("RoomJoinRequest [roomId=");
    builder.append(roomId);
    builder.append("]");
    return builder.toString();
  }
}
