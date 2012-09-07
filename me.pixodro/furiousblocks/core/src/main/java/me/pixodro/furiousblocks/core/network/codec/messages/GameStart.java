package me.pixodro.furiousblocks.core.network.codec.messages;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class GameStart extends FuriousBlocksMessage {
  public GameStart() {
    super(FuriousBlocksMessageType.GAME_START);
  }

  @Override
  public byte[] encode() {
    return ProtostuffIOUtil.toByteArray(this, RuntimeSchema.getSchema(GameStart.class), LinkedBuffer.allocate(bufferLength));
  }
}
