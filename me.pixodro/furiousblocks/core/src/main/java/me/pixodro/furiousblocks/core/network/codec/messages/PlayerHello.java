package me.pixodro.furiousblocks.core.network.codec.messages;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class PlayerHello extends FuriousBlocksMessage {
  public int version;
  public String name;

  public PlayerHello() {
    super(FuriousBlocksMessageType.PLAYER_HELLO);
  }

  public PlayerHello(final String name) {
    super(FuriousBlocksMessageType.PLAYER_HELLO);
    this.version = me.pixodro.furiousblocks.core.network.codec.FuriousBlocksCodec.version;
    this.name = name;
  }

  @Override
  public byte[] encode() {
    return ProtostuffIOUtil.toByteArray(this, RuntimeSchema.getSchema(PlayerHello.class), LinkedBuffer.allocate(bufferLength));
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("PlayerHello [version=");
    builder.append(version);
    builder.append(", name=");
    builder.append(name);
    builder.append("]");
    return builder.toString();
  }
}
