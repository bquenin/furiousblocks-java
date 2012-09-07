package me.pixodro.furiousblocks.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.maxmind.geoip.LookupService;
import me.pixodro.furiousblocks.core.log.CustomFormatter;
import me.pixodro.furiousblocks.core.network.NetworkPlayer;
import me.pixodro.furiousblocks.core.network.codec.messages.GameDeltaUpdate;
import me.pixodro.furiousblocks.core.network.codec.messages.GameGarbageEventList;
import me.pixodro.furiousblocks.core.network.codec.messages.GameStart;
import me.pixodro.furiousblocks.core.network.lobby.LobbyPlayer;
import me.pixodro.furiousblocks.core.network.lobby.Room;
import me.pixodro.furiousblocks.server.core.FuriousBlocksTCPServerCore;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class FuriousBlocksServer {
  private static FuriousBlocksServer INSTANCE;
  final LookupService cl;
  private final ServerBootstrap tcpBootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

  final Map<Channel, LobbyPlayer> tcpChannelToLobbyPlayer = new ConcurrentHashMap<Channel, LobbyPlayer>();
  final Map<Integer, Channel> playerIdToTcpChannel = new HashMap<Integer, Channel>();
  final Map<Channel, Room> hostTcpChannelToRoom = new ConcurrentHashMap<Channel, Room>();
  final Map<Integer, Room> roomIdToRoom = new ConcurrentHashMap<Integer, Room>();
  final Map<Integer, FuriousBlocksTCPServerCore> roomIdToCore = new ConcurrentHashMap<Integer, FuriousBlocksTCPServerCore>();
  final Map<Integer, NetworkPlayer> networkPlayerIdToNetworkPlayer = new ConcurrentHashMap<Integer, NetworkPlayer>();
  final Map<Integer, FuriousBlocksTCPServerCore> networkPlayerIdToCore = new ConcurrentHashMap<Integer, FuriousBlocksTCPServerCore>();

  private static void setupLogger() {
    // The root logger's handlers default to INFO. We have to
    // crank them up. We could crank up only some of them
    // if we wanted, but we will turn them all up.
    final Formatter formatter = new CustomFormatter();
    final Handler[] handlers = Logger.getLogger("").getHandlers();
    for (final Handler handler : handlers) {
      handler.setLevel(Level.ALL);
      handler.setFormatter(formatter);
    }
    Logger.getLogger("org.tsug").setLevel(Level.ALL);
  }

  private static File extractFile() throws IOException {
    Logger.getLogger(FuriousBlocksServer.class.getName()).log(Level.INFO, "Extracting file = " + "GeoIP.dat" + " from " + "/" + "GeoIP.dat" + " to user.dir = " + System.getProperty("user.dir"));
    final InputStream in = FuriousBlocksServer.class.getResourceAsStream("/" + "GeoIP.dat");
    if (in == null) {
      throw new IllegalStateException("Unable to get resource as stream = " + "/" + "GeoIP.dat");
    }
    final byte[] buffer = new byte[8192];

    final File temp = new File("GeoIP.dat");
    try {
      final FileOutputStream fos = new FileOutputStream(temp);

      for (int read; (read = in.read(buffer)) != -1; ) {
        fos.write(buffer, 0, read);
      }
      fos.close();
    } catch (final Exception e) {
      Logger.getLogger(FuriousBlocksServer.class.getName()).log(Level.SEVERE, "Error while extracting file", e);
    } finally {
      in.close();
    }
    return temp;
  }

  private FuriousBlocksServer() throws IOException {
    final File ipDatabase = extractFile();

    cl = new LookupService(ipDatabase, LookupService.GEOIP_MEMORY_CACHE);
    // Configure the event pipeline factory.
    tcpBootstrap.setPipelineFactory(new FuriousBlocksServerTcpPipelineFactory());
    tcpBootstrap.setOption("child.tcpNoDelay", true);
  }

  public static FuriousBlocksServer getInstance() throws IOException {
    if (INSTANCE == null) {
      INSTANCE = new FuriousBlocksServer();
    }
    return INSTANCE;
  }

  void bind() {
    // Bind and start to accept incoming connections.
    tcpBootstrap.bind(new InetSocketAddress(12345));
  }

  public void sendGameStart(final NetworkPlayer player) {
    playerIdToTcpChannel.get(player.id).write(new GameStart());
  }

  public void sendGameDeltaUpdate(final NetworkPlayer player, final GameDeltaUpdate gameDeltaUpdate) {
    playerIdToTcpChannel.get(player.id).write(gameDeltaUpdate);
  }

  public void sendGameGarbageEventList(final NetworkPlayer player, final GameGarbageEventList gameGarbageEventList) {
    playerIdToTcpChannel.get(player.id).write(gameGarbageEventList);
  }

  public static void main(final String[] args) throws IOException {
    setupLogger();
    getInstance().bind();
  }
}
