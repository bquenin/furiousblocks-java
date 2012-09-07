package me.pixodro.furiousblocks.core.network.codec.messages;


import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import me.pixodro.furiousblocks.core.network.lobby.Room;

public class RoomUpdated extends FuriousBlocksMessage {
  public Room room;

  public RoomUpdated() {
    super(FuriousBlocksMessageType.ROOM_UPDATED);
  }

  public RoomUpdated(final Room room) {
    super(FuriousBlocksMessageType.ROOM_UPDATED);
    this.room = room;
  }

  @Override
  public byte[] encode() {
    return ProtostuffIOUtil.toByteArray(this, RuntimeSchema.getSchema(RoomUpdated.class), LinkedBuffer.allocate(bufferLength));
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("RoomUpdated [room=");
    builder.append(room);
    builder.append("]");
    return builder.toString();
  }
}
