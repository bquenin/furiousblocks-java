package me.pixodro.furiousblocks.server.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.pixodro.furiousblocks.core.FuriousBlocksCore;
import me.pixodro.furiousblocks.core.FuriousBlocksCoreDefaults;
import me.pixodro.furiousblocks.core.network.NetworkPlayer;
import me.pixodro.furiousblocks.core.network.TickStatus;
import me.pixodro.furiousblocks.core.network.codec.messages.GameDeltaUpdate;
import me.pixodro.furiousblocks.core.network.codec.messages.GameGarbageEventList;
import me.pixodro.furiousblocks.core.panel.*;
import me.pixodro.furiousblocks.server.FuriousBlocksServer;

public class FuriousBlocksTCPServerCore extends FuriousBlocksCore {
  private final FuriousBlocksServer server;

  public FuriousBlocksTCPServerCore(final int seed, final FuriousBlocksServer server) {
    super(seed);
    this.server = server;
  }

  @Override
  public final void onCombo(final Combo combo) {
    long highestTick = -1;
    long lowestTick = 1000000000L;
    for (final Map.Entry<Player, Panel> entry : playerToPanel.entrySet()) {
      if (entry.getValue().getLocalTick() > highestTick) {
        highestTick = entry.getValue().getLocalTick();
      }
      if (entry.getValue().getLocalTick() < lowestTick) {
        lowestTick = entry.getValue().getLocalTick();
      }
    }

    final long eventTick = highestTick + 30 + (8 * (highestTick - lowestTick));

    for (final Map.Entry<Player, Panel> entry : playerToPanel.entrySet()) {
      final Player player = entry.getKey();
      final Panel panel = entry.getValue();
      final LinkedList<StackGarbageEvent> garbageEvents = new LinkedList<StackGarbageEvent>();
      networkContexts.get(player.id).getGarbageEventsBacklog().put(eventTick, garbageEvents);

      if ((player.getId() != combo.getOwner()) && !panel.isGameOver()) {
        if (combo.getSkillChainLevel() > 1) {
          garbageEvents.add(new StackGarbageEvent((byte) FuriousBlocksCoreDefaults.PANEL_WIDTH, (byte) (combo.getSkillChainLevel() - 1), combo.getOwner(), true));
        }

        if (combo.size() > 3) {
          // Loop and decrease the size of the combo to generate
          // adequate garbages
          for (int size = (combo.size() - 1); size >= 0; ) {
            if (size >= FuriousBlocksCoreDefaults.PANEL_WIDTH) {
              garbageEvents.add(new StackGarbageEvent((byte) FuriousBlocksCoreDefaults.PANEL_WIDTH, (byte) 1, combo.getOwner(), false));
              size -= FuriousBlocksCoreDefaults.PANEL_WIDTH;
            } else if (size > 2) {
              garbageEvents.add(new StackGarbageEvent((byte) size, (byte) 1, combo.getOwner(), false));
              break;
            } else {
              break;
            }
          }
        }

        for (final Player networkPlayer : playerToPanel.keySet()) {
          server.sendGameGarbageEventList((NetworkPlayer) networkPlayer, new GameGarbageEventList(player.id, eventTick, garbageEvents));
        }
      }
    }

  }

  public void onGameDeltaUpdate(final NetworkPlayer networkPlayer, final GameDeltaUpdate msg) {
    // When we receive an update from a player, we run his panel simulation locally.
    // The purpose of running each player simulation locally is to have
    // synchronization checkpoints when dropping garbages.
    final Panel panel = playerToPanel.get(networkPlayer);

    // Simulate
    // N.B.: Here we assume that packets might be dropped but not altered.
    // Checking for packet integrity would be handled by computing a hash on the packet itself
    // and rejecting any ill-formed packet, and it's not done as it can only be done by malicious clients.
    long localTick = panel.getLocalTick();

    for (; localTick <= msg.tick; localTick++) {
      final List<StackGarbageEvent> garbageEvents = networkContexts.get(networkPlayer.id).getGarbageEventsBacklog().remove(localTick);
      if (garbageEvents != null) {
        for (final StackGarbageEvent event : garbageEvents) {
          // LOG.info("Stacking garbage @ tick " + localTick + ", on player " + networkPlayer.getName() + ", event = " + event);
          panel.stackGarbage(panel.newGarbage(event.width, event.height, event.owner, event.skill));
        }
      }

      if (localTick != msg.tick) {
        panel.onTick(localTick);
      } else {
        final TickStatus tickStatus = msg.tickStatus;
        if (tickStatus.eventType != MoveType.HEARTBEAT) {
          final Move move = new Move(tickStatus.eventType);
          panel.submitMove(move);
        }

        panel.onTick(localTick);

        if (panel.hashCode() != tickStatus.hashCode) {
          System.err.println("desynch @ tick " + localTick);
          break;
        }

        // We add an entry to the backlog only when a move is submitted
        for (final Player player : playerToPanel.keySet()) {
          // Make sure we don't send the delta back to the originator
          if (player.id == networkPlayer.id) {
            continue;
          }
          server.sendGameDeltaUpdate((NetworkPlayer) player, new GameDeltaUpdate(networkPlayer.id, localTick, tickStatus));
        }
      }

      // Increment panel local tick
      panel.setLocalTick(localTick + 1);
    }
  }
}
