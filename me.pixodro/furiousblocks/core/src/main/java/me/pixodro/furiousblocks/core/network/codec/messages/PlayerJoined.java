package me.pixodro.furiousblocks.core.network.codec.messages;


import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import me.pixodro.furiousblocks.core.network.lobby.LobbyPlayer;

public class PlayerJoined extends FuriousBlocksMessage {
  public LobbyPlayer player;
  public int roomId;
  public boolean log;

  public PlayerJoined() {
    super(FuriousBlocksMessageType.PLAYER_JOINED);
  }

  public PlayerJoined(final LobbyPlayer player, final int roomId, final boolean log) {
    super(FuriousBlocksMessageType.PLAYER_JOINED);
    this.player = player;
    this.roomId = roomId;
    this.log = log;
  }

  @Override
  public byte[] encode() {
    return ProtostuffIOUtil.toByteArray(this, RuntimeSchema.getSchema(PlayerJoined.class), LinkedBuffer.allocate(bufferLength));
  }
}
