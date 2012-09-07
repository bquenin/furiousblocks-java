package me.pixodro.furiousblocks.core.network;

import me.pixodro.furiousblocks.core.network.lobby.LobbyPlayer;
import me.pixodro.furiousblocks.core.panel.Move;
import me.pixodro.furiousblocks.core.panel.Player;
import me.pixodro.furiousblocks.core.situations.PanelSituation;
import org.jboss.netty.channel.Channel;

public class NetworkPlayer extends Player {
  private final Channel channel;
  public boolean ready = false;

  public NetworkPlayer(final LobbyPlayer lobbyPlayer, final Channel channel) {
    super(lobbyPlayer.id, lobbyPlayer.name);
    this.channel = channel;
  }

  public NetworkPlayer(final LobbyPlayer lobbyPlayer) {
    this(lobbyPlayer, null);
  }

  public Channel getChannel() {
    return channel;
  }

  public void submitMove(final Move move) {
    //    this.move.set(move);
    this.move = move;
  }

  @Override
  protected void playerTick(@SuppressWarnings("unused") final PanelSituation panelSituation, @SuppressWarnings("unused") final boolean isMostRecentData) {
  }
}
