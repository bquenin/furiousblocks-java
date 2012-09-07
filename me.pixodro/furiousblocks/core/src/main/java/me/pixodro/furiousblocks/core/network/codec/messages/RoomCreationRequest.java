package me.pixodro.furiousblocks.core.network.codec.messages;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class RoomCreationRequest extends FuriousBlocksMessage {
  public RoomCreationRequest() {
    super(FuriousBlocksMessageType.ROOM_CREATIONREQUEST);
  }

  @Override
  public byte[] encode() {
    return ProtostuffIOUtil.toByteArray(this, RuntimeSchema.getSchema(RoomCreationRequest.class), LinkedBuffer.allocate(bufferLength));
  }
}
