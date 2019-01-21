package me.pixodro.furiousblocks.game.screen;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class NonLoopingAnimation extends Animation<TextureRegion> {
  private final float creationTime;

  public NonLoopingAnimation(final float creationTime, float frameDuration, TextureRegion... keyFrames) {
    super(frameDuration, keyFrames);
    this.creationTime = creationTime;
  }

  public TextureRegion getKeyFrame(final float stateTime) {
    return super.getKeyFrame(stateTime - creationTime, false);
  }

  @Override
  public boolean isAnimationFinished(final float stateTime) {
    return super.isAnimationFinished(stateTime - creationTime);
  }
}
