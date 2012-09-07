package me.pixodro.furiousblocks.client;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import me.pixodro.furiousblocks.core.network.TickStatus;
import me.pixodro.furiousblocks.core.network.codec.messages.*;
import me.pixodro.furiousblocks.core.network.lobby.Room;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

public class FuriousBlocksClient {
  private final ClientBootstrap tcpBootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
  private Channel tcpChannel;

  public FuriousBlocksClient() {
    // Configure the event pipeline factory.
    tcpBootstrap.setPipelineFactory(new FuriousBlocksTcpPipelineFactory());
    tcpBootstrap.setOption("tcpNoDelay", true);
  }

  public void connect(final String hostname, final int port, final FuriousBlocksLobbyListener lobbyListener) throws NotConnectedException {
    // Make a new connection.
    final ChannelFuture connectFuture = tcpBootstrap.connect(new InetSocketAddress(hostname, port)).awaitUninterruptibly();

    // Now we are sure the future is completed.
    assert connectFuture.isDone();

    if (connectFuture.isCancelled() || !connectFuture.isSuccess()) {
      throw new NotConnectedException(connectFuture.getCause());
    }

    tcpChannel = connectFuture.getChannel();

    // Get the handler instance
    final FuriousBlocksClientTcpHandler tcpHandler = tcpChannel.getPipeline().get(FuriousBlocksClientTcpHandler.class);

    // Set the listener
    tcpHandler.setLobbyListener(lobbyListener);

    // udpChannel.connect(new InetSocketAddress(hostname, port)).awaitUninterruptibly();
  }

  public void setInGameListener(final FuriousBlocksGameEventListener inGameListener) {
    // Get the handler instance
    final FuriousBlocksClientTcpHandler tcpHandler = tcpChannel.getPipeline().get(FuriousBlocksClientTcpHandler.class);

    // Set the listener
    tcpHandler.setInGameListener(inGameListener);

    // // Get the handler instance
    // final FuriousBlocksClientUdpHandler udpHandler = udpChannel.getPipeline().get(FuriousBlocksClientUdpHandler.class);
    // udpHandler.setInGameListener(inGameListener);
  }

  public void sendClientHello(final String playerName) {
    tcpChannel.write(new PlayerHello(playerName));
  }

  public void sendChatMessage(final int roomId, final String message) {
    tcpChannel.write(new ChatMessage(roomId, message));
  }

  public void createRoom() {
    tcpChannel.write(new RoomCreationRequest());
  }

  public void joinRoom(final int roomId) {
    tcpChannel.write(new RoomJoinRequest(roomId));
  }

  public void leaveRoom(final int roomId) {
    tcpChannel.write(new RoomLeaveRequest(roomId));
  }

  public void updateRoom(final Room room) {
    tcpChannel.write(new RoomUpdateRequest(room));
  }

  public void requestGameStart(final int roomId) {
    tcpChannel.write(new RoomStartRequest(roomId));
  }

  public void readyToStart(final int roomId) {
    tcpChannel.write(new RoomPlayerReady(roomId));
  }

  public void sendGameDeltaUpdate(final int playerId, final long tick, final TickStatus tickStatus) {
    tcpChannel.write(new GameDeltaUpdate(playerId, tick, tickStatus));
  }

  public void disconnect() {
    tcpChannel.disconnect();
  }

  public void close() {
    if (tcpChannel != null) {
      tcpChannel.close().awaitUninterruptibly();
    }

    // Shut down all thread pools to exit.
    tcpBootstrap.releaseExternalResources();
  }
}
