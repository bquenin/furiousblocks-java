package me.pixodro.furiousblocks.core.network.lobby;

import java.util.Random;

public class LobbyPlayer {
  private transient static final Random RANDOM = new Random(System.nanoTime());
  public final int id;
  public final String name;
  public final String ip;
  public final String country;
  public int ping;

  public LobbyPlayer(final String name, final String ip, final String country) {
    id = RANDOM.nextInt();
    this.name = name;
    this.ip = ip;
    this.country = country;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("LobbyPlayer [id=");
    builder.append(id);
    builder.append(", name=");
    builder.append(name);
    builder.append(", ip=");
    builder.append(ip);
    builder.append(", country=");
    builder.append(country);
    builder.append(", ping=");
    builder.append(ping);
    builder.append("]");
    return builder.toString();
  }
}
