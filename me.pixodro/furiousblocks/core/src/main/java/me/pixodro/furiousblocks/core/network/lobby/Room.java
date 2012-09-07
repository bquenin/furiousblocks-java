package me.pixodro.furiousblocks.core.network.lobby;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.pixodro.furiousblocks.core.panel.GameType;

public class Room {
  private transient static final Random RANDOM = new Random(System.nanoTime());
  public static final int lobby = 0;
  public int id;
  public String name;
  public int hostId;
  public GameType gameType;
  public boolean sendGarbages;
  public int maxPlayer;
  public List<LobbyPlayer> players = new ArrayList<LobbyPlayer>();
  public boolean gameInProgress;

  public Room() {
    super();
  }

  public Room(final String name, final int hostId, final GameType type, final boolean sendGarbages, final int maxPlayer, final List<LobbyPlayer> players) {
    super();
    id = RANDOM.nextInt();
    this.name = name;
    this.hostId = hostId;
    gameType = type;
    this.sendGarbages = sendGarbages;
    this.maxPlayer = maxPlayer;
    this.players = players;
  }

  public LobbyPlayer getById(final int playerId) {
    for (final LobbyPlayer player : players) {
      if (player.id == playerId) {
        return player;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("Room [id=");
    builder.append(id);
    builder.append(", name=");
    builder.append(name);
    builder.append(", hostId=");
    builder.append(hostId);
    builder.append(", gameType=");
    builder.append(gameType);
    builder.append(", sendGarbages=");
    builder.append(sendGarbages);
    builder.append(", maxPlayer=");
    builder.append(maxPlayer);
    builder.append(", players=");
    builder.append(players);
    builder.append(", gameInProgress=");
    builder.append(gameInProgress);
    builder.append("]");
    return builder.toString();
  }
}
