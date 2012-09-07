package me.pixodro.furiousblocks.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import me.pixodro.furiousblocks.core.network.codec.FuriousBlocksCodecHandler;
import me.pixodro.furiousblocks.core.network.codec.messages.*;
import org.jboss.netty.channel.*;

public class FuriousBlocksClientTcpHandler extends FuriousBlocksCodecHandler {
  private static final Logger LOG = Logger.getLogger(FuriousBlocksClientTcpHandler.class.getName());
  private static final FuriousBlocksClientTcpHandler INSTANCE = new FuriousBlocksClientTcpHandler();
  private FuriousBlocksLobbyListener lobbyListener;
  private FuriousBlocksGameEventListener inGameListener;

  private FuriousBlocksClientTcpHandler() {
    super();
  }

  public static FuriousBlocksClientTcpHandler getInstance() {
    return INSTANCE;
  }

  final void setLobbyListener(final FuriousBlocksLobbyListener lobbyListener) {
    this.lobbyListener = lobbyListener;
  }

  public void setInGameListener(final FuriousBlocksGameEventListener inGameListener) {
    this.inGameListener = inGameListener;
  }

  @Override
  protected void onPlayerHello(@SuppressWarnings("unused") final MessageEvent e, final PlayerHello msg) throws Exception {
    throw new FuriousBlocksClientException("TCP client is not supposed to receive: " + msg);
  }

  @Override
  protected void onServerHello(final MessageEvent e, final ServerHello serverHello) {
    lobbyListener.onServerHello(e, serverHello);
  }

  @Override
  protected void onPing(final Ping msg) throws FuriousBlocksClientException {
    throw new FuriousBlocksClientException("TCP client is not supposed to receive: " + msg);
  }

  @Override
  protected void onPong(@SuppressWarnings("unused") final Pong msg) {
  }

  @Override
  protected void onPlayerJoined(final MessageEvent e, final PlayerJoined msg) {
    lobbyListener.onPlayerJoined(e, msg);
  }

  @Override
  protected void onPlayerLeft(final MessageEvent e, final PlayerLeft msg) {
    lobbyListener.onPlayerLeft(e, msg);
  }

  @Override
  protected void onChatMessage(final MessageEvent e, final ChatMessage msg) {
    lobbyListener.onChatMessage(e, msg);
  }

  @Override
  protected void onRoomCreationRequest(@SuppressWarnings("unused") final MessageEvent e, final RoomCreationRequest msg) throws FuriousBlocksClientException {
    throw new FuriousBlocksClientException("TCP client is not supposed to receive: " + msg);
  }

  @Override
  protected void onRoomCreated(final MessageEvent e, final RoomCreated msg) {
    lobbyListener.onRoomCreated(e, msg);
  }

  @Override
  protected void onRoomUpdated(final MessageEvent e, final RoomUpdated msg) {
    lobbyListener.onRoomUpdated(e, msg);
  }

  @Override
  protected void onRoomJoinRequest(@SuppressWarnings("unused") final MessageEvent e, final RoomJoinRequest msg) throws Exception {
    throw new FuriousBlocksClientException("TCP client is not supposed to receive: " + msg);
  }

  @Override
  protected void onRoomLeaveRequest(@SuppressWarnings("unused") final MessageEvent e, final RoomLeaveRequest msg) throws Exception {
    throw new FuriousBlocksClientException("TCP client is not supposed to receive: " + msg);
  }

  @Override
  protected void onRoomRemoved(final MessageEvent e, final RoomRemoved msg) {
    lobbyListener.onRoomRemoved(e, msg);
  }

  @Override
  protected void onRoomGetReady(final MessageEvent e, final RoomGetReady msg) throws Exception {
    lobbyListener.onRoomGetReady(e, msg);
  }

  @Override
  protected void onRoomUpdateRequest(@SuppressWarnings("unused") final MessageEvent e, final RoomUpdateRequest msg) throws Exception {
    throw new FuriousBlocksClientException("TCP client is not supposed to receive: " + msg);
  }

  @Override
  protected void onRoomStartRequest(@SuppressWarnings("unused") final MessageEvent e, final RoomStartRequest msg) throws Exception {
    throw new FuriousBlocksClientException("TCP client is not supposed to receive: " + msg);
  }

  @Override
  protected void onRoomPlayerReady(@SuppressWarnings("unused") final MessageEvent e, final RoomPlayerReady msg) throws Exception {
    throw new FuriousBlocksClientException("TCP client is not supposed to receive: " + msg);
  }

  @Override
  protected void onGameStart(final MessageEvent e, final GameStart msg) {
    inGameListener.onGameStart();
  }

  @Override
  protected void onGameDeltaUpdate(final MessageEvent e, final GameDeltaUpdate msg) {
    inGameListener.onGameDeltaUpdate(msg);
  }

  @Override
  protected void onGameGarbageEventList(final MessageEvent e, final GameGarbageEventList msg) {
    inGameListener.onGameGarbageEventList(msg);
  }

  @Override
  public final void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
    super.channelConnected(ctx, e);
    if (lobbyListener != null) {
      lobbyListener.onChannelConnected(this);
    }
  }

  @Override
  public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
    super.channelClosed(ctx, e);
    if (lobbyListener != null) {
      lobbyListener.onChannelClosed(e);
    }
  }

  @Override
  public final void handleUpstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
    if (e instanceof ChannelStateEvent) {
      LOG.info(e.toString());
    }
    super.handleUpstream(ctx, e);
  }

  @Override
  public final void exceptionCaught(@SuppressWarnings("unused") final ChannelHandlerContext ctx, final ExceptionEvent e) {
    e.getCause().printStackTrace();
    LOG.log(Level.WARNING, "Unexpected exception from downstream.", e.getCause());
    e.getChannel().close();
  }
}
