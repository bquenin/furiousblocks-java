package me.pixodro.furiousblocks.core.network.codec;

import me.pixodro.furiousblocks.core.network.codec.messages.*;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

abstract public class FuriousBlocksCodecHandler extends SimpleChannelUpstreamHandler {
  @Override
  public void messageReceived(@SuppressWarnings("unused") final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
    final FuriousBlocksMessage msg = (FuriousBlocksMessage) e.getMessage();

    final FuriousBlocksMessageType type = msg.getType();
    switch (type) {
      case PLAYER_HELLO:
        onPlayerHello(e, (PlayerHello) msg);
        break;
      case SERVER_HELLO:
        onServerHello(e, (ServerHello) msg);
        break;
      case PING:
        onPing((Ping) msg);
        break;
      case PONG:
        onPong((Pong) msg);
        break;
      case PLAYER_JOINED:
        onPlayerJoined(e, (PlayerJoined) msg);
        break;
      case PLAYER_LEFT:
        onPlayerLeft(e, (PlayerLeft) msg);
        break;
      case CHAT_MESSAGE:
        onChatMessage(e, (ChatMessage) msg);
        break;
      case ROOM_CREATIONREQUEST:
        onRoomCreationRequest(e, (RoomCreationRequest) msg);
        break;
      case ROOM_CREATED:
        onRoomCreated(e, (RoomCreated) msg);
        break;
      case ROOM_UPDATED:
        onRoomUpdated(e, (RoomUpdated) msg);
        break;
      case ROOM_JOINREQUEST:
        onRoomJoinRequest(e, (RoomJoinRequest) msg);
        break;
      case ROOM_LEAVEREQUEST:
        onRoomLeaveRequest(e, (RoomLeaveRequest) msg);
        break;
      case ROOM_REMOVED:
        onRoomRemoved(e, (RoomRemoved) msg);
        break;
      case ROOM_UPDATEREQUEST:
        onRoomUpdateRequest(e, (RoomUpdateRequest) msg);
        break;
      case ROOM_STARTREQUEST:
        onRoomStartRequest(e, (RoomStartRequest) msg);
        break;
      case ROOM_GETREADY:
        onRoomGetReady(e, (RoomGetReady) msg);
        break;
      case ROOM_PLAYERREADY:
        onRoomPlayerReady(e, (RoomPlayerReady) msg);
        break;
      case GAME_START:
        onGameStart(e, (GameStart) msg);
        break;
      case GAME_DELTAUPDATE:
        onGameDeltaUpdate(e, (GameDeltaUpdate) msg);
        break;
      case GAME_GARBAGEEVENTLIST:
        onGameGarbageEventList(e, (GameGarbageEventList) msg);
        break;
    }
  }

  abstract protected void onPlayerHello(final MessageEvent e, final PlayerHello msg) throws Exception;

  abstract protected void onServerHello(final MessageEvent e, final ServerHello msg) throws Exception;

  abstract protected void onPing(final Ping msg) throws Exception;

  abstract protected void onPong(final Pong msg) throws Exception;

  abstract protected void onPlayerJoined(final MessageEvent e, final PlayerJoined msg) throws Exception;

  abstract protected void onPlayerLeft(final MessageEvent e, final PlayerLeft msg) throws Exception;

  abstract protected void onChatMessage(final MessageEvent e, final ChatMessage msg);

  abstract protected void onRoomCreationRequest(final MessageEvent e, final RoomCreationRequest msg) throws Exception;

  abstract protected void onRoomCreated(final MessageEvent e, final RoomCreated msg) throws Exception;

  abstract protected void onRoomUpdated(final MessageEvent e, final RoomUpdated msg) throws Exception;

  abstract protected void onRoomJoinRequest(final MessageEvent e, final RoomJoinRequest msg) throws Exception;

  abstract protected void onRoomLeaveRequest(final MessageEvent e, final RoomLeaveRequest msg) throws Exception;

  abstract protected void onRoomRemoved(final MessageEvent e, final RoomRemoved msg) throws Exception;

  abstract protected void onRoomUpdateRequest(final MessageEvent e, final RoomUpdateRequest msg) throws Exception;

  abstract protected void onRoomStartRequest(final MessageEvent e, final RoomStartRequest msg) throws Exception;

  abstract protected void onRoomGetReady(final MessageEvent e, final RoomGetReady msg) throws Exception;

  abstract protected void onRoomPlayerReady(final MessageEvent e, final RoomPlayerReady msg) throws Exception;

  abstract protected void onGameStart(final MessageEvent e, final GameStart msg) throws Exception;

  abstract protected void onGameDeltaUpdate(final MessageEvent e, final GameDeltaUpdate msg);

  abstract protected void onGameGarbageEventList(final MessageEvent e, final GameGarbageEventList msg) throws Exception;
}
