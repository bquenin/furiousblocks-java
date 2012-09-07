package me.pixodro.furiousblocks.core.network.codec.messages;


import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import me.pixodro.furiousblocks.core.network.lobby.Room;

public class RoomGetReady extends FuriousBlocksMessage {
  public Room room;
  public long seed;

  public RoomGetReady() {
    super(FuriousBlocksMessageType.ROOM_GETREADY);
  }

  public RoomGetReady(final Room room, final long seed) {
    super(FuriousBlocksMessageType.ROOM_GETREADY);
    this.room = room;
    this.seed = seed;
  }

  @Override
  public byte[] encode() {
    return ProtostuffIOUtil.toByteArray(this, RuntimeSchema.getSchema(RoomGetReady.class), LinkedBuffer.allocate(bufferLength));
  }
}
