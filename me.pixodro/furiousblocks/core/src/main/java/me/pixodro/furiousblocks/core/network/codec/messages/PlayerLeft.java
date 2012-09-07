package me.pixodro.furiousblocks.core.network.codec.messages;


import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import me.pixodro.furiousblocks.core.network.lobby.LobbyPlayer;

public class PlayerLeft extends FuriousBlocksMessage {
  public LobbyPlayer player;
  public int roomId;

  public PlayerLeft() {
    super(FuriousBlocksMessageType.PLAYER_LEFT);
  }

  public PlayerLeft(final LobbyPlayer player, final int roomId) {
    super(FuriousBlocksMessageType.PLAYER_LEFT);
    this.player = player;
    this.roomId = roomId;
  }

  @Override
  public byte[] encode() {
    return ProtostuffIOUtil.toByteArray(this, RuntimeSchema.getSchema(PlayerLeft.class), LinkedBuffer.allocate(bufferLength));
  }
}
