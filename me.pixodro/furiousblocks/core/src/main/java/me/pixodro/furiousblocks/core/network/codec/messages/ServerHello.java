package me.pixodro.furiousblocks.core.network.codec.messages;


import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import me.pixodro.furiousblocks.core.network.lobby.LobbyPlayer;

public class ServerHello extends FuriousBlocksMessage {
  public LobbyPlayer player;

  public ServerHello() {
    super(FuriousBlocksMessageType.SERVER_HELLO);
  }

  public ServerHello(final LobbyPlayer player) {
    super(FuriousBlocksMessageType.SERVER_HELLO);
    this.player = player;
  }

  @Override
  public byte[] encode() {
    return ProtostuffIOUtil.toByteArray(this, RuntimeSchema.getSchema(ServerHello.class), LinkedBuffer.allocate(bufferLength));
  }
}
