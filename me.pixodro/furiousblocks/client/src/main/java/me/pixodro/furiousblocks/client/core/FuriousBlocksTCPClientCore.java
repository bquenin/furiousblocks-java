package me.pixodro.furiousblocks.client.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import me.pixodro.furiousblocks.client.FuriousBlocksClient;
import me.pixodro.furiousblocks.client.FuriousBlocksGameEventListener;
import me.pixodro.furiousblocks.core.FuriousBlocksCore;
import me.pixodro.furiousblocks.core.network.TickStatus;
import me.pixodro.furiousblocks.core.network.codec.messages.GameDeltaUpdate;
import me.pixodro.furiousblocks.core.network.codec.messages.GameGarbageEventList;
import me.pixodro.furiousblocks.core.network.lobby.Room;
import me.pixodro.furiousblocks.core.panel.*;
import me.pixodro.furiousblocks.core.situations.GameSituation;
import me.pixodro.furiousblocks.core.situations.PanelSituation;

/**
 * @author tsug
 */
public class FuriousBlocksTCPClientCore extends FuriousBlocksCore implements FuriousBlocksGameEventListener {
  private static final Logger LOG = Logger.getLogger(FuriousBlocksTCPClientCore.class.getName());
  private final FuriousBlocksClient client;
  private final Room room;
  private final CountDownLatch startLatch = new CountDownLatch(1);
  private final int myId;

  public FuriousBlocksTCPClientCore(final int seed, final FuriousBlocksClient client, final Room room, final int myId) {
    super(seed);
    this.client = client;
    this.room = room;
    this.myId = myId;
  }

  public void before() throws Exception {
    // Notify we're ready to start
    client.readyToStart(room.id);

    // And wait for the server go
    LOG.info("Waiting for other players to be ready.");
    startLatch.await();

    LOG.info("All players are ready, game is starting @ " + new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss:SS").format(new Date()));
  }

  @Override
  public void onGameStart() {
    startLatch.countDown();
  }

  public void onTick(final long tick) {
    // Panel Loop
    final Map<Integer, PanelSituation> panelSituations = new LinkedHashMap<Integer, PanelSituation>();
    for (final Map.Entry<Player, me.pixodro.furiousblocks.core.panel.Panel> entry : playerToPanel.entrySet()) {
      final Player player = entry.getKey();
      final Panel panel = entry.getValue();

      final PanelSituation panelSituation;

      // We are about to process our own panel
      if (player.id == myId) {

        final List<StackGarbageEvent> garbageEvents = networkContexts.get(player.id).getGarbageEventsBacklog().remove(tick);
        if (garbageEvents != null) {
          for (final StackGarbageEvent event : garbageEvents) {
            // LOG.info("Stacking garbage @ tick " + tick + ", on player " + player.getName() +", event = " + event);
            panel.stackGarbage(panel.newGarbage(event.width, event.height, event.owner, event.skill));
          }
        }

        final Move localMove = player.onMoveRequest();
        panel.submitMove(localMove);

        // 2/ process the tick on the panel
        panelSituation = panel.onTick(tick);

        final TickStatus tickStatus = new TickStatus();
        // We add an entry to the backlog only when a move is submitted to not overflow the network
        // - the tick move
        tickStatus.eventType = localMove != null ? localMove.getType() : MoveType.HEARTBEAT;

        // - Get the hashCode
        // Note: We calculate the hash after the tick. The server will do the same on its side to check for consistency.
        tickStatus.hashCode = panel.hashCode();

        // - Then, only send the remaining ones
        client.sendGameDeltaUpdate(myId, tick, tickStatus);
      } else {
        final long localTick = panel.getLocalTick();
        if (localTick > networkContexts.get(player.id).getLastACKedTick()) {
          continue;
        }

        final List<StackGarbageEvent> garbageEvents = networkContexts.get(player.id).getGarbageEventsBacklog().remove(localTick);
        if (garbageEvents != null) {
          for (final StackGarbageEvent event : garbageEvents) {
            // LOG.info("Stacking garbage @ tick " + localTick + ", on player " + player.getName() +", event = " + event);
            panel.stackGarbage(panel.newGarbage(event.width, event.height, event.owner, event.skill));
          }
        }

        final TickStatus tickStatus = networkContexts.get(player.id).getBacklog().remove(localTick);
        if (tickStatus == null) {
          panelSituation = panel.onTick(localTick);
        } else {
          if (tickStatus.eventType != MoveType.HEARTBEAT) {
            final Move move = new Move(tickStatus.eventType);
            panel.submitMove(move);
          }
          panelSituation = panel.onTick(localTick);

          if (panel.hashCode() != tickStatus.hashCode) {
            System.err.println("desynch @ tick " + localTick);
          }
        }

        // Increment panel local tick
        panel.setLocalTick(localTick + 1);
      }

      panelSituations.put(player.getId(), panelSituation);
      if (panel.isGameOver()) {
        continue;
      }
      player.onSituationUpdate(panelSituation);
    }

    // Update game situation
    gameSituation.set(new GameSituation(panelSituations));
  }

  @Override
  public final void onCombo(@SuppressWarnings("unused") final Combo combo) {
    // Do nothing !
  }

  @Override
  public void onGameDeltaUpdate(final GameDeltaUpdate msg) {
    // Store all tick statuses of the remote player in its backlog
    final long tick = msg.tick;
    final TickStatus tickStatus = msg.tickStatus;
    if (tick > networkContexts.get(msg.playerId).getLastACKedTick()) {
      networkContexts.get(msg.playerId).getBacklog().put(tick, tickStatus);
      networkContexts.get(msg.playerId).setLastACKedTick(msg.tick);
    }
  }

  @Override
  public void onGameGarbageEventList(final GameGarbageEventList msg) {
    // LOG.info("Combo event received for tick " + msg.tick + " and we are on tick " + super.getTick());
    networkContexts.get(msg.playerId).getGarbageEventsBacklog().put(msg.tick, msg.garbageEvents);
    // backlog.put(msg.tick, msg.comboEvent);
  }
}
