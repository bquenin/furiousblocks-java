package me.pixodro.furiousblocks.core.network.codec.messages;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class Ping extends FuriousBlocksMessage {
  public Ping() {
    super(FuriousBlocksMessageType.PING);
  }

  @Override
  public byte[] encode() {
    return ProtostuffIOUtil.toByteArray(this, RuntimeSchema.getSchema(Ping.class), LinkedBuffer.allocate(bufferLength));
  }
}
