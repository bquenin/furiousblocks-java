package me.pixodro.furiousblocks.core.network.codec.messages;


import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import me.pixodro.furiousblocks.core.network.lobby.Room;

public class RoomCreated extends FuriousBlocksMessage {
  public Room room;

  public RoomCreated() {
    super(FuriousBlocksMessageType.ROOM_CREATED);
  }

  public RoomCreated(final Room room) {
    super(FuriousBlocksMessageType.ROOM_CREATED);
    this.room = room;
  }

  @Override
  public byte[] encode() {
    return ProtostuffIOUtil.toByteArray(this, RuntimeSchema.getSchema(RoomCreated.class), LinkedBuffer.allocate(bufferLength));
  }
}
