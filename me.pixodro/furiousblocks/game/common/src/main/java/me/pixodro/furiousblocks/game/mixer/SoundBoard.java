package me.pixodro.furiousblocks.game.mixer;

/**
 * Created with IntelliJ IDEA.
 * User: tsug
 * Date: 5/9/12
 * Time: 8:24 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SoundBoard {
  public void addSample(OggSample oggSample);

  public void play();

  public void dispose();
}
