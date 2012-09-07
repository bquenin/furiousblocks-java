package me.pixodro.furiousblocks.client;

import me.pixodro.furiousblocks.core.network.codec.messages.*;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;

public interface FuriousBlocksLobbyListener {
  void onChannelConnected(FuriousBlocksClientTcpHandler FuriousBlocksClientHandler);

  void onChannelClosed(ChannelStateEvent e);

  void onServerHello(MessageEvent e, ServerHello serverHello);

  void onPlayerJoined(MessageEvent e, PlayerJoined msg);

  void onPlayerLeft(MessageEvent e, PlayerLeft msg);

  void onChatMessage(MessageEvent e, ChatMessage msg);

  void onRoomCreated(MessageEvent e, RoomCreated msg);

  void onRoomUpdated(MessageEvent e, RoomUpdated msg);

  void onRoomRemoved(MessageEvent e, RoomRemoved msg);

  void onRoomGetReady(MessageEvent e, RoomGetReady msg) throws Exception;
}
