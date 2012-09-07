package me.pixodro.furiousblocks.core.network;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import me.pixodro.furiousblocks.core.panel.StackGarbageEvent;

public class NetworkContext {
  private final Map<Long, TickStatus> backlog = new HashMap<Long, TickStatus>();
  private final Map<Long, LinkedList<StackGarbageEvent>> garbageEventsBacklog = new HashMap<Long, LinkedList<StackGarbageEvent>>();
  private final AtomicLong lastACKedTick = new AtomicLong(-1);

  public NetworkContext() {
    super();
  }

  public long getLastACKedTick() {
    return lastACKedTick.get();
  }

  public void setLastACKedTick(final long lastACKedTick) {
    this.lastACKedTick.set(lastACKedTick);
  }

  public Map<Long, TickStatus> getBacklog() {
    return backlog;
  }

  public Map<Long, LinkedList<StackGarbageEvent>> getGarbageEventsBacklog() {
    return garbageEventsBacklog;
  }
}
