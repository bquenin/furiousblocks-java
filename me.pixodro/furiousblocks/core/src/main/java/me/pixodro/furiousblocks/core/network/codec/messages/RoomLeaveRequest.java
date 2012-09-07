package me.pixodro.furiousblocks.core.network.codec.messages;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class RoomLeaveRequest extends FuriousBlocksMessage {
  public int roomId;

  public RoomLeaveRequest() {
    super(FuriousBlocksMessageType.ROOM_LEAVEREQUEST);
  }

  public RoomLeaveRequest(final int roomId) {
    super(FuriousBlocksMessageType.ROOM_LEAVEREQUEST);
    this.roomId = roomId;
  }

  @Override
  public byte[] encode() {
    return ProtostuffIOUtil.toByteArray(this, RuntimeSchema.getSchema(RoomLeaveRequest.class), LinkedBuffer.allocate(bufferLength));
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("RoomLeaveRequest [roomId=");
    builder.append(roomId);
    builder.append("]");
    return builder.toString();
  }
}
