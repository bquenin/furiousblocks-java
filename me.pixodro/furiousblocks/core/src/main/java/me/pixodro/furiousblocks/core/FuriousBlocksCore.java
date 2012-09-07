package me.pixodro.furiousblocks.core;

import java.util.*;

import me.pixodro.furiousblocks.core.network.NetworkContext;
import me.pixodro.furiousblocks.core.panel.*;
import me.pixodro.furiousblocks.core.situations.GameSituation;
import me.pixodro.furiousblocks.core.situations.PanelSituation;
import me.pixodro.furiousblocks.core.tools.SimpleRNG;

import static me.pixodro.furiousblocks.core.FuriousBlocksCoreDefaults.*;

/**
 * @author tsug
 */
public class FuriousBlocksCore implements Runnable, PanelListener {
  protected final Map<Player, Panel> playerToPanel = new LinkedHashMap<Player, Panel>();
  //  protected final AtomicReference<GameSituation> gameSituation = new AtomicReference<GameSituation>();
  protected GameSituation gameSituation;
  public final BlockType[][] initialBlockTypes = new BlockType[PANEL_WIDTH][PANEL_HEIGHT];
  private final int seed;

  // Network variables
  protected transient final Map<Integer, NetworkContext> networkContexts = new HashMap<Integer, NetworkContext>();
  // private boolean paused = false;
  // private boolean justATick = false;
  final long nanoPeriod = (long) (1f / CORE_FREQUENCY * 1000000000);
  private long tick = 0;
  private volatile boolean running;
  private volatile boolean paused;
  private volatile boolean singleTick = false;
  private final FuriousBlocksCoreListener listener;

  public FuriousBlocksCore(final int seed) {
    this(seed, null);
  }

  public FuriousBlocksCore(final int seed, final FuriousBlocksCoreListener listener) {
    this.listener = listener;
    this.seed = seed;
    final SimpleRNG random = new SimpleRNG(seed);

    // Initial panel
    for (int y = 0; y < 4; y++) {
      for (int x = 0; x < PANEL_WIDTH; x++) {
        initialBlockTypes[x][y] = BlockType.values()[random.nextInt() % Panel.numberOfRegularBlocks];
      }
    }
  }

  public void addPlayer(final Player newPlayer) {
    addPlayer(newPlayer, null);
  }

  public void addPlayer(final Player newPlayer, Panel panel) {
    playerToPanel.put(newPlayer, panel == null ? new Panel(seed + newPlayer.getId(), newPlayer.getId(), initialBlockTypes, this) : panel);
    for (final Player other : playerToPanel.keySet()) {
      networkContexts.put(other.id, new NetworkContext());
    }
  }

  public final GameSituation getGameSituation() {
    return gameSituation;
  }

  @Override
  final public void run() {
    running = true;
    try {
      for (long tick = 1; running; ) {
        long before = System.nanoTime();
        if (singleTick || !paused) {
          onTick(tick++);
        }
        singleTick = false;
        long nanoSleep = Math.max(0, nanoPeriod - (System.nanoTime() - before));
        Thread.sleep(nanoSleep / 1000000, (int) (nanoSleep % 1000000));
      }
    } catch (final Throwable t) {
      t.printStackTrace();
    }
  }

  public void pause() {
    paused = true;
  }

  public void resume() {
    paused = false;
  }

  public void stop() {
    running = false;
  }

  public void onTick(final long tick) {
    // Request player moves
    for (final Map.Entry<Player, Panel> entry : playerToPanel.entrySet()) {
      final Player player = entry.getKey();
      final Panel panel = entry.getValue();
      if (panel.isGameOver()) {
        continue;
      }
      final Move move = player.onMoveRequest();
      if (move != null) {
        panel.submitMove(move);
      }
    }

    // Panels tick
    final Map<Integer, PanelSituation> panelSituations = new LinkedHashMap<Integer, PanelSituation>();
    for (final Map.Entry<Player, Panel> entry : playerToPanel.entrySet()) {
      final Player player = entry.getKey();
      final Panel panel = entry.getValue();
      final PanelSituation panelSituation = panel.onTick(tick);
      panelSituations.put(player.getId(), panelSituation);
      if (panel.isGameOver()) {
        continue;
      }
      player.onSituationUpdate(panelSituation);
    }

    // Update game situation
    gameSituation = new GameSituation(panelSituations);
  }


  @Override
  public void onCombo(final Combo combo) {
    for (final Map.Entry<Player, Panel> entry : playerToPanel.entrySet()) {
      final Player player = entry.getKey();
      final Panel panel = entry.getValue();
      if ((player.getId() != combo.getOwner()) && !panel.isGameOver()) {
        if (combo.getSkillChainLevel() > 1) {
          panel.stackGarbage(panel.newGarbage(PANEL_WIDTH, (combo.getSkillChainLevel() - 1), combo.getOwner(), true));
        }

        if (combo.size() > 3) {
          // Loop and decrease the size of the combo to generate
          // adequate garbages
          for (int size = (combo.size() - 1); size >= 0; ) {
            if (size >= PANEL_WIDTH) {
              panel.stackGarbage(panel.newGarbage(PANEL_WIDTH, 1, combo.getOwner(), false));
              size -= PANEL_WIDTH;
            } else if (size > 2) {
              panel.stackGarbage(panel.newGarbage(size, 1, combo.getOwner(), false));
              break;
            } else {
              break;
            }
          }
        }
      }
    }
  }

  @Override
  public void onEvent(final long playerId, final PanelEvent panelEvent) {
    if (listener != null) {
      listener.onEvent(playerId, panelEvent);
    }
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof FuriousBlocksCore)) {
      return false;
    }

    final FuriousBlocksCore that = (FuriousBlocksCore) o;

    if (tick != that.tick) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return (int) (tick ^ (tick >>> 32));
  }

  public Set<Player> getPlayers() {
    return Collections.unmodifiableSet(playerToPanel.keySet());
  }

  public void justATick() {
    pause();
    singleTick = true;
  }
}