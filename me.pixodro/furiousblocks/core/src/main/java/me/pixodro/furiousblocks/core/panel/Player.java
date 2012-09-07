package me.pixodro.furiousblocks.core.panel;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import me.pixodro.furiousblocks.core.situations.PanelSituation;
import me.pixodro.furiousblocks.core.tools.SimpleRNG;

abstract public class Player implements Runnable {
  private static final SimpleRNG RANDOM = new SimpleRNG((int) System.nanoTime());
  private transient final BlockingQueue<PanelSituation> situations = new LinkedBlockingQueue<PanelSituation>();
  //  protected transient final AtomicReference<Move> move = new AtomicReference<Move>();
  protected transient Move move;

  public final int id;
  public final String name;

  protected Player(final int id, final String name) {
    super();
    this.id = id;
    this.name = name;
  }

  protected Player(final String name) {
    this(RANDOM.nextInt(), name);
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Move onMoveRequest() {
    //    return move.getAndSet(null);
    return move;
  }

  public void onSituationUpdate(final PanelSituation panelSituation) {
    situations.add(panelSituation);
  }

  @Override
  public void run() {
    try {
      while (true) {
        final boolean isMostRecentData = situations.size() <= 1;
        final PanelSituation ps = situations.take();
        playerTick(ps, isMostRecentData);
      }
    } catch (final Throwable t) {
      t.printStackTrace();
    } finally {
      System.out.println("player " + name + "exited");
    }
  }

  abstract protected void playerTick(PanelSituation panelSituation, boolean isMostRecentData);

  @Override
  public final int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + id;
    return result;
  }

  @Override
  public final boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Player other = (Player) obj;
    return id == other.id;
  }
}
