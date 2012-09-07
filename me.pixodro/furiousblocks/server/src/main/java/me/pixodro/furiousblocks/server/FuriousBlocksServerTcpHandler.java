package me.pixodro.furiousblocks.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.pixodro.furiousblocks.core.network.NetworkPlayer;
import me.pixodro.furiousblocks.core.network.codec.FuriousBlocksCodec;
import me.pixodro.furiousblocks.core.network.codec.FuriousBlocksCodecHandler;
import me.pixodro.furiousblocks.core.network.codec.messages.*;
import me.pixodro.furiousblocks.core.network.lobby.LobbyPlayer;
import me.pixodro.furiousblocks.core.network.lobby.Room;
import me.pixodro.furiousblocks.core.panel.GameType;
import me.pixodro.furiousblocks.core.panel.Player;
import me.pixodro.furiousblocks.server.core.FuriousBlocksTCPServerCore;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.ChannelHandler.Sharable;

@Sharable
public class FuriousBlocksServerTcpHandler extends FuriousBlocksCodecHandler {
  private static final Logger LOG = Logger.getLogger(FuriousBlocksServerTcpHandler.class.getName());
  private static FuriousBlocksServerTcpHandler INSTANCE;
  private final FuriousBlocksServer server;

  private FuriousBlocksServerTcpHandler() throws IOException {
    super();
    server = FuriousBlocksServer.getInstance();
  }

  public static FuriousBlocksServerTcpHandler getInstance() throws IOException {
    if (INSTANCE == null) {
      INSTANCE = new FuriousBlocksServerTcpHandler();
    }
    return INSTANCE;
  }

  @Override
  public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
    // Remove the player
    final LobbyPlayer removedPlayer = server.tcpChannelToLobbyPlayer.get(e.getChannel());
    server.tcpChannelToLobbyPlayer.remove(e.getChannel());
    server.playerIdToTcpChannel.remove(removedPlayer.id);

    final Room room = server.hostTcpChannelToRoom.get(e.getChannel());
    // Room host has left, remove the room
    if (room != null) {
      server.hostTcpChannelToRoom.remove(e.getChannel());
      server.roomIdToRoom.remove(room.id);

      // Notify all player that the room has been removed
      for (final Channel channel : server.tcpChannelToLobbyPlayer.keySet()) {
        channel.write(new RoomRemoved(room.id), channel.getRemoteAddress());
      }
    }

    for (final Channel channel : server.tcpChannelToLobbyPlayer.keySet()) {
      // Notify all player that a player has left
      channel.write(new PlayerLeft(removedPlayer, Room.lobby), channel.getRemoteAddress());
    }
    super.channelClosed(ctx, e);
  }

  @Override
  protected void onPlayerHello(final MessageEvent e, final PlayerHello playerHello) throws FuriousBlocksServerException {
    LOG.info("onPlayerHello = " + playerHello);
    if (playerHello.version != FuriousBlocksCodec.version) {
      throw new FuriousBlocksServerException("Unsupported client version : " + playerHello.version);
    }

    // Add the new player
    final InetAddress address = ((InetSocketAddress) e.getRemoteAddress()).getAddress();
    final LobbyPlayer newPlayer = new LobbyPlayer(playerHello.name, address.getHostAddress(), server.cl.getCountry(address.getHostAddress()).getName());
    LOG.info("Adding new player = " + newPlayer);

    server.tcpChannelToLobbyPlayer.put(e.getChannel(), newPlayer);
    server.playerIdToTcpChannel.put(newPlayer.id, e.getChannel());
    LOG.info("player count on the server = " + server.tcpChannelToLobbyPlayer.size());

    // Give the new player its id
    e.getChannel().write(new ServerHello(newPlayer), e.getRemoteAddress());

    for (final Map.Entry<Channel, LobbyPlayer> entry : server.tcpChannelToLobbyPlayer.entrySet()) {
      final Channel channel = entry.getKey();
      final LobbyPlayer client = entry.getValue();
      if (!channel.equals(e.getChannel())) {
        // Notify others clients that there's a new client
        channel.write(new PlayerJoined(newPlayer, 0, true), channel.getRemoteAddress());
      }
      // Notify the new client of all clients
      e.getChannel().write(new PlayerJoined(client, 0, false), e.getRemoteAddress());
    }

    // Notify the new client of all rooms having been created before he joined
    for (final Room room : server.roomIdToRoom.values()) {
      e.getChannel().write(new RoomCreated(room), e.getRemoteAddress());
    }
  }

  @Override
  protected void onChatMessage(final MessageEvent e, final ChatMessage msg) {
    LOG.info("onChatMessage = " + msg);
    msg.senderName = server.tcpChannelToLobbyPlayer.get(e.getChannel()).name;

    // Lobby chat message
    if (msg.roomId == Room.lobby) {
      for (final Channel channel : server.tcpChannelToLobbyPlayer.keySet()) {
        channel.write(msg, channel.getRemoteAddress());
      }
    } // Room chat message
    else {
      final Room room = server.roomIdToRoom.get(msg.roomId);
      if (room == null) {
        throw new IllegalStateException("Received a chat message for an unkown room, roomId = " + msg.roomId + ", msg = " + msg.message);
      }

      // Send the message to the whole room except the sender
      for (final LobbyPlayer player : room.players) {
        final Channel channel = server.playerIdToTcpChannel.get(player.id);
        channel.write(msg, channel.getRemoteAddress());
      }
    }
  }

  @Override
  protected void onRoomCreationRequest(final MessageEvent e, @SuppressWarnings("unused") final RoomCreationRequest msg) {
    LOG.info("onRoomCreationRequest");
    if (server.hostTcpChannelToRoom.get(e.getChannel()) != null) {
      throw new IllegalStateException("Player is not supposed to be able to create more than one room. Channel.getRemoteAddress() = " + e.getChannel().getRemoteAddress());
    }
    final LobbyPlayer requester = server.tcpChannelToLobbyPlayer.get(e.getChannel());

    // Add the host to the room
    final List<LobbyPlayer> players = new ArrayList<LobbyPlayer>();
    players.add(requester);

    // Create the room
    final Room newRoom = new Room(requester.name + "'s room", requester.id, GameType.VERSUS_ENDLESS, true, 8, players);
    LOG.info("Adding new room = " + newRoom);

    // Add the new room to room collections
    server.hostTcpChannelToRoom.put(e.getChannel(), newRoom);
    server.roomIdToRoom.put(newRoom.id, newRoom);
    LOG.info("room count on the server = " + server.hostTcpChannelToRoom.size());

    // Notify all player that a room has been created
    for (final Channel channel : server.tcpChannelToLobbyPlayer.keySet()) {
      channel.write(new RoomCreated(newRoom), channel.getRemoteAddress());
    }
  }

  @Override
  protected void onRoomJoinRequest(final MessageEvent e, final RoomJoinRequest msg) throws Exception {
    LOG.info("onRoomJoinRequest = " + msg);
    final Room room = server.roomIdToRoom.get(msg.roomId);
    if (room == null) {
      throw new IllegalStateException("Received a join request for an unkown room, roomId = " + msg.roomId);
    }
    if (room.gameInProgress) {
      throw new IllegalStateException("Received a join request for a room where a game is in progress, roomId = " + msg.roomId);
    }

    final LobbyPlayer requester = server.tcpChannelToLobbyPlayer.get(e.getChannel());
    if (requester.id == room.hostId) {
      throw new IllegalStateException("Received a join request from the room owner, roomId = " + msg.roomId);
    }

    // Add the player to the room
    room.players.add(requester);
    LOG.info("Adding player = " + requester + " to room = " + room);

    // Notify room players that a player has joined
    for (final LobbyPlayer player : room.players) {
      final Channel channel = server.playerIdToTcpChannel.get(player.id);
      channel.write(new PlayerJoined(requester, room.id, true), channel.getRemoteAddress());
    }

    // Notify all player that a room has been updated
    for (final Channel channel : server.tcpChannelToLobbyPlayer.keySet()) {
      channel.write(new RoomUpdated(room), channel.getRemoteAddress());
    }
  }

  @Override
  protected void onRoomLeaveRequest(final MessageEvent e, final RoomLeaveRequest msg) throws Exception {
    LOG.info("onRoomLeaveRequest = " + msg);
    final Room room = server.roomIdToRoom.get(msg.roomId);
    if (room == null) {
      throw new IllegalStateException("Received a leave request for an unkown room, roomId = " + msg.roomId);
    }
    if (room.gameInProgress) {
      throw new IllegalStateException("Received a leave request for a room where a game is in progress, roomId = " + msg.roomId);
    }

    final LobbyPlayer requester = server.tcpChannelToLobbyPlayer.get(e.getChannel());
    // If the requester is the room host, we need to remove the room
    if (requester.id == room.hostId) {
      server.hostTcpChannelToRoom.remove(e.getChannel());
      server.roomIdToRoom.remove(room.id);
      LOG.info("removing room = " + room);
      LOG.info("room count on the server = " + server.hostTcpChannelToRoom.size());

      // Notify all player that the room has been removed
      for (final Channel channel : server.tcpChannelToLobbyPlayer.keySet()) {
        channel.write(new RoomRemoved(room.id), channel.getRemoteAddress());
      }
    } else {
      room.players.remove(requester);
      LOG.info("removing player = " + requester + " from room = " + room);

      // Notify room players that a player has left
      for (final LobbyPlayer player : room.players) {
        final Channel channel = server.playerIdToTcpChannel.get(player.id);
        channel.write(new PlayerLeft(requester, room.id), channel.getRemoteAddress());
      }

      // Notify all player that a room has been updated
      for (final Channel channel : server.tcpChannelToLobbyPlayer.keySet()) {
        channel.write(new RoomUpdated(room), channel.getRemoteAddress());
      }
    }
  }

  @Override
  protected void onRoomUpdateRequest(final MessageEvent e, final RoomUpdateRequest msg) throws Exception {
    LOG.info("onRoomUpdateRequest = " + msg);
    final Room room = server.roomIdToRoom.get(msg.room.id);
    if (room == null) {
      throw new IllegalStateException("Received an update request for an unkown room, roomId = " + msg.room.id);
    }
    if (room.gameInProgress) {
      throw new IllegalStateException("Received an update request for a room where a game is in progress, roomId = " + msg.room.id);
    }

    final LobbyPlayer requester = server.tcpChannelToLobbyPlayer.get(e.getChannel());
    room.players = msg.room.players;
    if (requester.id == room.hostId) {
      room.maxPlayer = msg.room.maxPlayer;
      room.gameType = msg.room.gameType;
      room.sendGarbages = msg.room.sendGarbages;
    }
    LOG.info("updated room to " + room);

    // Notify all player that a room has been updated
    for (final Channel channel : server.tcpChannelToLobbyPlayer.keySet()) {
      // // except sender
      // if (channel.equals(e.getChannel())) {
      // continue;
      // }
      channel.write(new RoomUpdated(room), channel.getRemoteAddress());
    }
  }

  @Override
  protected void onRoomStartRequest(final MessageEvent e, final RoomStartRequest msg) throws Exception {
    final Room room = server.roomIdToRoom.get(msg.roomId);
    if (room == null) {
      throw new IllegalStateException("Received a start request for an unkown room, roomId = " + msg.roomId);
    }
    if (room.gameInProgress) {
      throw new IllegalStateException("Received a start request for a room where a game is in progress, roomId = " + msg.roomId);
    }
    final LobbyPlayer requester = server.tcpChannelToLobbyPlayer.get(e.getChannel());
    if (requester.id != room.hostId) {
      throw new IllegalStateException("Received a start request coming from a player who's not the room host, requester id = " + requester.id);
    }

    // Checking preconditions for game start
    if (room.players.size() > room.maxPlayer) {
      // Notify room players that the game cannot start
      for (final LobbyPlayer player : room.players) {
        final Channel channel = server.playerIdToTcpChannel.get(player.id);
        channel.write(new ChatMessage(room.id, "Game cannot start because there are too many players."), channel.getRemoteAddress());
      }
    } else {
      room.gameInProgress = true;

      final int seed = (int) System.nanoTime();

      // Create the server core
      final FuriousBlocksTCPServerCore core = new FuriousBlocksTCPServerCore(seed, FuriousBlocksServer.getInstance());
      server.roomIdToCore.put(room.id, core);

      // final FuriousBlocksUDPServerCore core = new FuriousBlocksUDPServerCore(configuration, seed, FuriousBlocksServer.getInstance());
      // server.roomIdToCore.put(room.id, core);

      // Player setup
      for (final LobbyPlayer lobbyPlayer : room.players) {
        final Channel channel = server.playerIdToTcpChannel.get(lobbyPlayer.id);

        // Create the network player
        final NetworkPlayer networkPlayer = new NetworkPlayer(lobbyPlayer, channel);
        server.networkPlayerIdToNetworkPlayer.put(networkPlayer.id, networkPlayer);
        server.networkPlayerIdToCore.put(networkPlayer.id, core);
        core.addPlayer(networkPlayer);

        // Ask room players to get ready
        channel.write(new RoomGetReady(room, seed), channel.getRemoteAddress());
      }
    }

    // Notify all player that a room has been updated
    for (final Channel channel : server.tcpChannelToLobbyPlayer.keySet()) {
      channel.write(new RoomUpdated(room), channel.getRemoteAddress());
    }
  }

  @Override
  protected void onRoomPlayerReady(final MessageEvent e, final RoomPlayerReady msg) throws Exception {
    final Room room = server.roomIdToRoom.get(msg.roomId);
    if (room == null) {
      throw new IllegalStateException("Received a player ready notification for an unkown room, roomId = " + msg.roomId);
    }

    final FuriousBlocksTCPServerCore core = server.roomIdToCore.get(msg.roomId);
    // final FuriousBlocksUDPServerCore core = server.roomIdToCore.get(msg.roomId);
    if (core == null) {
      throw new IllegalStateException("Received a player ready notification for a room wih no core, roomId = " + msg.roomId);
    }

    final LobbyPlayer requester = server.tcpChannelToLobbyPlayer.get(e.getChannel());
    boolean ready = true;
    for (final Player player : core.getPlayers()) {
      final NetworkPlayer networkPlayer = (NetworkPlayer) player;
      if (networkPlayer.id == requester.id) {
        LOG.info(player.name + " is ready");
        networkPlayer.ready = true;
      }
      if (!networkPlayer.ready) {
        LOG.info(player.name + " is not ready");
        ready = false;
      }
    }

    // Wait for all players to be ready
    if (!ready) {
      LOG.info("Waiting for all players to be ready");
      return;
    }

    LOG.info("All players are ready, start the game");

    // Finally, start the game core
    new Thread(core, "FuriousBlocksCore").start();

    // Ask room players to start the game
    for (final Player player : core.getPlayers()) {
      server.sendGameStart((NetworkPlayer) player);
    }
  }

  @Override
  protected void onPing(final Ping msg) throws FuriousBlocksServerException {
    throw new FuriousBlocksServerException("TCP server is not supposed to receive: " + msg);
  }

  @Override
  protected void onServerHello(@SuppressWarnings("unused") final MessageEvent e, final ServerHello msg) throws FuriousBlocksServerException {
    throw new FuriousBlocksServerException("TCP server is not supposed to receive: " + msg);
  }

  @Override
  protected void onPong(final Pong msg) throws FuriousBlocksServerException {
    throw new FuriousBlocksServerException("TCP server is not supposed to receive: " + msg);
  }

  @Override
  protected void onPlayerJoined(@SuppressWarnings("unused") final MessageEvent e, final PlayerJoined msg) throws Exception {
    throw new FuriousBlocksServerException("TCP server is not supposed to receive: " + msg);
  }

  @Override
  protected void onPlayerLeft(@SuppressWarnings("unused") final MessageEvent e, final PlayerLeft msg) throws Exception {
    throw new FuriousBlocksServerException("TCP server is not supposed to receive: " + msg);
  }

  @Override
  protected void onRoomCreated(@SuppressWarnings("unused") final MessageEvent e, final RoomCreated msg) throws Exception {
    throw new FuriousBlocksServerException("TCP server is not supposed to receive: " + msg);
  }

  @Override
  protected void onRoomUpdated(@SuppressWarnings("unused") final MessageEvent e, final RoomUpdated msg) throws Exception {
    throw new FuriousBlocksServerException("TCP server is not supposed to receive: " + msg);
  }

  @Override
  protected void onRoomRemoved(@SuppressWarnings("unused") final MessageEvent e, final RoomRemoved msg) throws Exception {
    throw new FuriousBlocksServerException("TCP server is not supposed to receive: " + msg);
  }

  @Override
  protected void onRoomGetReady(@SuppressWarnings("unused") final MessageEvent e, final RoomGetReady msg) throws Exception {
    throw new FuriousBlocksServerException("TCP server is not supposed to receive: " + msg);
  }

  @Override
  protected void onGameStart(@SuppressWarnings("unused") final MessageEvent e, final GameStart msg) throws Exception {
    throw new FuriousBlocksServerException("TCP server is not supposed to receive: " + msg);
  }

  @Override
  protected void onGameDeltaUpdate(@SuppressWarnings("unused") final MessageEvent e, final GameDeltaUpdate msg) {
    server.networkPlayerIdToCore.get(msg.playerId).onGameDeltaUpdate(server.networkPlayerIdToNetworkPlayer.get(msg.playerId), msg);
  }

  @Override
  protected void onGameGarbageEventList(@SuppressWarnings("unused") final MessageEvent e, final GameGarbageEventList msg) throws Exception {
    throw new FuriousBlocksServerException("TCP server is not supposed to receive: " + msg);
  }

  @Override
  public void handleUpstream(final ChannelHandlerContext ctx, final ChannelEvent e) throws Exception {
    if (e instanceof ChannelStateEvent) {
      LOG.info(e.toString());
    }
    super.handleUpstream(ctx, e);
  }

  @Override
  public void exceptionCaught(@SuppressWarnings("unused") final ChannelHandlerContext ctx, final ExceptionEvent e) {
    LOG.log(Level.WARNING, "Unexpected exception from downstream.", e.getCause());
    e.getChannel().close();
  }
}
