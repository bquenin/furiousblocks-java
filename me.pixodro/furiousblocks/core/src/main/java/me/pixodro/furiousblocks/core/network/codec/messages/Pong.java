package me.pixodro.furiousblocks.core.network.codec.messages;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class Pong extends FuriousBlocksMessage {
  public Pong() {
    super(FuriousBlocksMessageType.PONG);
  }

  @Override
  public byte[] encode() {
    return ProtostuffIOUtil.toByteArray(this, RuntimeSchema.getSchema(Pong.class), LinkedBuffer.allocate(bufferLength));
  }
}
