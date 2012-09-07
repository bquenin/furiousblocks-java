package me.pixodro.furiousblocks.core.network.codec.messages;


import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import me.pixodro.furiousblocks.core.network.lobby.Room;

public class RoomUpdateRequest extends FuriousBlocksMessage {
  public Room room;

  public RoomUpdateRequest() {
    super(FuriousBlocksMessageType.ROOM_UPDATEREQUEST);
  }

  public RoomUpdateRequest(final Room room) {
    super(FuriousBlocksMessageType.ROOM_UPDATEREQUEST);
    this.room = room;
  }

  @Override
  public byte[] encode() {
    return ProtostuffIOUtil.toByteArray(this, RuntimeSchema.getSchema(RoomUpdateRequest.class), LinkedBuffer.allocate(bufferLength));
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("RoomUpdateRequest [room=");
    builder.append(room);
    builder.append("]");
    return builder.toString();
  }
}
