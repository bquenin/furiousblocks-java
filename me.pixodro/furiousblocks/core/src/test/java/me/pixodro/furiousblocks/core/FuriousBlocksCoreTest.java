package me.pixodro.furiousblocks.core;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import me.pixodro.furiousblocks.core.network.NetworkPlayer;
import me.pixodro.furiousblocks.core.network.lobby.LobbyPlayer;
import me.pixodro.furiousblocks.core.panel.Player;
import me.pixodro.furiousblocks.core.situations.PanelSituation;
import org.junit.Assert;
import org.junit.Test;

public class FuriousBlocksCoreTest {
  @Test
  public void coreEqualsAndHashCodeTest() throws SecurityException, IllegalArgumentException {
    final int seed = (int) System.nanoTime();
    final Player player1 = new NetworkPlayer(new LobbyPlayer("player1", "127.0.0.1", "fr"));
    final Player player2 = new NetworkPlayer(new LobbyPlayer("player2", "127.0.0.1", "fr"));

    final FuriousBlocksCore core1 = new FuriousBlocksCore(seed);
    core1.addPlayer(player1);
    core1.addPlayer(player2);

    final FuriousBlocksCore core2 = new FuriousBlocksCore(seed);
    core2.addPlayer(player1);
    core2.addPlayer(player2);

    for (int i = 0; i < 1000; i++) {
      core1.onTick(i);
      core2.onTick(i);
      Assert.assertEquals(core1.hashCode(), core2.hashCode());
      Assert.assertEquals(core1, core2);
    }
  }

  @Test
  public void randomHackTest() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    final long initialSeed = System.nanoTime();
    final Random random = new Random(initialSeed);
    Random random2 = new Random(initialSeed);
    System.out.println("initialSeed = " + initialSeed);

    for (int i = 0; i < 100; i++) {
      Assert.assertEquals(random.nextInt(), random2.nextInt());
    }

    // Get the seed of the random for this tick
    final Field field = Random.class.getDeclaredField("seed");
    field.setAccessible(true);
    final AtomicLong atomicLong = (AtomicLong) field.get(random);
    final long tickSeed = atomicLong.get();
    System.out.println("tickSeed = " + tickSeed);

    random.setSeed(tickSeed);

    random2 = new Random(tickSeed);
    for (int i = 0; i < 100; i++) {
      Assert.assertEquals(random.nextInt(), random2.nextInt());
    }
  }

  @Test
  public void randomNoHackTest() throws IOException, ClassNotFoundException {
    final long initialSeed = System.nanoTime();
    final Random random = new Random(initialSeed);
    Random random2 = new Random(initialSeed);
    System.out.println("initialSeed = " + initialSeed);

    for (int i = 0; i < 100; i++) {
      Assert.assertEquals(random.nextInt(), random2.nextInt());
    }

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(random);
    oos.close();
    baos.close();

    System.out.println("baos.toByteArray() = " + baos.toByteArray().length);
    random2 = (Random) new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())).readObject();
    for (int i = 0; i < 100; i++) {
      Assert.assertEquals(random.nextInt(), random2.nextInt());
    }
  }

  @Test
  public void panelHashcodeTest() {
    final int seed = (int) System.nanoTime();
    final Player player1 = new NetworkPlayer(new LobbyPlayer("player1", "127.0.0.1", "fr"));
    final Player player2 = new NetworkPlayer(new LobbyPlayer("player2", "127.0.0.1", "fr"));

    final FuriousBlocksCore core1 = new FuriousBlocksCore(seed);
    core1.addPlayer(player1);
    core1.addPlayer(player2);

    final FuriousBlocksCore core2 = new FuriousBlocksCore(seed);
    core2.addPlayer(player1);
    core2.addPlayer(player2);

    Assert.assertEquals(core1.hashCode(), core2.hashCode());
    Assert.assertEquals(core1, core2);
  }

  @Test
  public void coreLifeCycleTest() {
    final Player player = new Player(123, "123") {
      @Override
      protected void playerTick(final PanelSituation panelSituation, final boolean isMostRecentData) {
      }
    };

    final FuriousBlocksCore core = new FuriousBlocksCore(0);
    core.addPlayer(player);

    for (int i = 0; i < 1000; i++) {
      core.onTick(i);
      System.out.println("hashCode = " + core.hashCode());
    }
  }
}
