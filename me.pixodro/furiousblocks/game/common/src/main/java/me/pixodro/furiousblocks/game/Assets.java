package me.pixodro.furiousblocks.game;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import me.pixodro.furiousblocks.core.panel.BlockState;
import me.pixodro.furiousblocks.core.panel.BlockType;
import me.pixodro.furiousblocks.core.panel.GarbageBlockType;
import me.pixodro.furiousblocks.core.situations.BlockSituation;
import me.pixodro.furiousblocks.game.screen.NonLoopingAnimation;

import static me.pixodro.furiousblocks.core.FuriousBlocksCoreDefaults.CORE_FREQUENCY;
import static me.pixodro.furiousblocks.core.panel.BlockState.BLINKING;
import static me.pixodro.furiousblocks.core.panel.BlockState.IDLE;
import static me.pixodro.furiousblocks.core.panel.BlockType.GARBAGE;
import static me.pixodro.furiousblocks.core.panel.BlockType.TUTORIAL;

public class Assets extends AssetManager {
  private final AssetManager manager = new AssetManager();
  public static final int TILE_SIZE = 64;

  public BitmapFont coop32;
  public BitmapFont coop46;
  public BitmapFont joy32;

  public TextureRegion backgroundRegionTop;
  public TextureRegion backgroundRegionMiddle;
  public TextureRegion backgroundRegionBottom;
  public TextureRegion titleScreenRegion;

  public TextureRegion GARBAGE_BLINK;
  private TextureRegion BLOCKS_YELLOW_HAPPY;
  private TextureRegion BLOCKS_BLUE_HAPPY;
  private TextureRegion GARBAGE_TOP;
  private TextureRegion BLOCKS_GREEN_IDLE;
  private TextureRegion GARBAGE_BOTTOMRIGHT;
  private TextureRegion GARBAGE_TOPRIGHTBOTTOM;
  private TextureRegion GARBAGE_TOPBOTTOM;
  private TextureRegion GARBAGE_LEFT;
  private TextureRegion GARBAGE_PLAIN;
  private TextureRegion BLOCKS_PURPLE_IDLE;
  private TextureRegion BLOCKS_RED_IDLE;
  private TextureRegion GARBAGE_TOPLEFT;
  private TextureRegion GARBAGE_TOPLEFTBOTTOM;
  private TextureRegion BLOCKS_YELLOW_IDLE;
  private TextureRegion BLOCKS_PURPLE_HAPPY;
  private TextureRegion GARBAGE_RIGHT;
  private TextureRegion BLOCKS_GREEN_HAPPY;
  private TextureRegion BLOCKS_BLUE_IDLE;
  private TextureRegion BLOCKS_RED_HAPPY;
  private TextureRegion GARBAGE_SINGLE;
  private TextureRegion GARBAGE_BOTTOMLEFT;
  private TextureRegion GARBAGE_BOTTOM;
  private TextureRegion GARBAGE_TOPRIGHT;
  public TextureRegion EYE;
  public TextureRegion EYE_SURPRISE;

  // Animations templates
  private TextureRegion[] YELLOW_LANDING;
  private TextureRegion[] BLUE_LANDING;
  private TextureRegion[] RED_LANDING;
  private TextureRegion[] GREEN_LANDING;
  private TextureRegion[] PURPLE_LANDING;
  private TextureRegion[] YELLOW_AIRBOUNCING;
  private TextureRegion[] BLUE_AIRBOUNCING;
  private TextureRegion[] RED_AIRBOUNCING;
  private TextureRegion[] GREEN_AIRBOUNCING;
  private TextureRegion[] PURPLE_AIRBOUNCING;

  // Endless animations
  private Animation YELLOW_PANICKING;
  private Animation BLUE_PANICKING;
  private Animation RED_PANICKING;
  private Animation GREEN_PANICKING;
  private Animation PURPLE_PANICKING;

  private Animation YELLOW_COMPRESSING;
  private Animation BLUE_COMPRESSING;
  private Animation RED_COMPRESSING;
  private Animation GREEN_COMPRESSING;
  private Animation PURPLE_COMPRESSING;

  private Animation YELLOW_BLINKING;
  private Animation BLUE_BLINKING;
  private Animation RED_BLINKING;
  private Animation GREEN_BLINKING;
  private Animation PURPLE_BLINKING;
  private Animation GARBAGE_BLINKING;

  public Animation CURSOR;

  // Musics
  public Music harmonic;
  public Music voices;

  // Sfxs
  public Sound pop;
  public Sound move;

  private final Map<Integer, NonLoopingAnimation> animations = new HashMap<Integer, NonLoopingAnimation>();


  public Assets() {
    // Atlas
    manager.load("graphics/pack", TextureAtlas.class);

    // Backgrounds
    manager.load("graphics/background.png", Texture.class);
    manager.load("graphics/titlescreen.png", Texture.class);

    // Fonts
    manager.load("fonts/font.fnt", BitmapFont.class); // coop32
    manager.load("fonts/coop46.fnt", BitmapFont.class);
    manager.load("fonts/joy32.fnt", BitmapFont.class);

    // Musics
    manager.load("audio/voices.mp3", Music.class);
    manager.load("audio/harmonic.mp3", Music.class);

    // Sfxs
    manager.load("audio/pop.ogg", Sound.class);
    manager.load("audio/cursor.ogg", Sound.class);
  }

  public void load() {
    // Wait for resources loading
    manager.finishLoading();

    voices = manager.get("audio/voices.mp3", Music.class);
    harmonic = manager.get("audio/harmonic.mp3", Music.class);

    pop = manager.get("audio/pop.ogg", Sound.class);
    move = manager.get("audio/cursor.ogg", Sound.class);

    coop32 = manager.get("fonts/font.fnt", BitmapFont.class); // coop32
    coop46 = manager.get("fonts/coop46.fnt", BitmapFont.class);
    joy32 = manager.get("fonts/joy32.fnt", BitmapFont.class);

    final Texture background = manager.get("graphics/background.png", Texture.class);
    final Texture titleScreen = manager.get("graphics/titlescreen.png", Texture.class);

    backgroundRegionTop = new TextureRegion(background, 0, 0, 480, 76);
    backgroundRegionMiddle = new TextureRegion(background, 0, 76, 480, 704);
    backgroundRegionBottom = new TextureRegion(background, 0, 780, 480, 20);

    titleScreenRegion = new TextureRegion(titleScreen, 0, 0, 480, 800);

    final TextureAtlas atlas = manager.get("graphics/pack", TextureAtlas.class);
    final TextureRegion BLOCKS_RED_PANIC_01 = atlas.findRegion("blocks-red-panic-01");
    final TextureRegion BLOCKS_BLUE_HOVER_01 = atlas.findRegion("blocks-blue-hover-01");
    final TextureRegion BLOCKS_GREEN_COMPRESSED_01 = atlas.findRegion("blocks-green-compressed-01");
    final TextureRegion BLOCKS_BLUE_FLASH = atlas.findRegion("blocks-blue-flash");
    GARBAGE_BLINK = atlas.findRegion("garbage-blink");
    final TextureRegion BLOCKS_PURPLE_PANIC_01 = atlas.findRegion("blocks-purple-panic-01");
    final TextureRegion BLOCKS_RED_LAND_02 = atlas.findRegion("blocks-red-land-02");
    BLOCKS_YELLOW_HAPPY = atlas.findRegion("blocks-yellow-happy");
    final TextureRegion BLOCKS_RED_PANIC_03 = atlas.findRegion("blocks-red-panic-03");
    BLOCKS_BLUE_HAPPY = atlas.findRegion("blocks-blue-happy");
    final TextureRegion BLOCKS_BLUE_PANIC_01 = atlas.findRegion("blocks-blue-panic-01");
    final TextureRegion CURSOR_01 = atlas.findRegion("cursor-01");
    GARBAGE_TOP = atlas.findRegion("garbage-top");
    final TextureRegion CURSOR_03 = atlas.findRegion("cursor-03");
    final TextureRegion BLOCKS_YELLOW_HOVER_03 = atlas.findRegion("blocks-yellow-hover-03");
    final TextureRegion BLOCKS_PURPLE_HOVER_01 = atlas.findRegion("blocks-purple-hover-01");
    final TextureRegion BLOCKS_YELLOW_HOVER_02 = atlas.findRegion("blocks-yellow-hover-02");
    BLOCKS_GREEN_IDLE = atlas.findRegion("blocks-green-idle");
    final TextureRegion ENDPOPUP_DRAW = atlas.findRegion("EndPopUp-Draw");
    final TextureRegion BLOCKS_BLUE_PANIC_02 = atlas.findRegion("blocks-blue-panic-02");
    final TextureRegion ENDPOPUP_WIN = atlas.findRegion("EndPopUp-Win");
    final TextureRegion BLOCKS_GREEN_FLASH = atlas.findRegion("blocks-green-flash");
    final TextureRegion BLOCKS_RED_HOVER_04 = atlas.findRegion("blocks-red-hover-04");
    final TextureRegion BLOCKS_RED_PANIC_04 = atlas.findRegion("blocks-red-panic-04");
    GARBAGE_BOTTOMRIGHT = atlas.findRegion("garbage-bottomright");
    GARBAGE_TOPRIGHTBOTTOM = atlas.findRegion("garbage-toprightbottom");
    final TextureRegion BLOCKS_BLUE_PANIC_04 = atlas.findRegion("blocks-blue-panic-04");
    EYE_SURPRISE = atlas.findRegion("eye-surprise");
    final TextureRegion BLOCKS_GREEN_LAND_02 = atlas.findRegion("blocks-green-land-02");
    GARBAGE_TOPBOTTOM = atlas.findRegion("garbage-topbottom");
    final TextureRegion BLOCKS_GREEN_COMPRESSED_02 = atlas.findRegion("blocks-green-compressed-02");
    final TextureRegion BLOCKS_PURPLE_PANIC_04 = atlas.findRegion("blocks-purple-panic-04");
    GARBAGE_LEFT = atlas.findRegion("garbage-left");
    final TextureRegion CURSOR_02 = atlas.findRegion("cursor-02");
    final TextureRegion BLOCKS_PURPLE_HOVER_03 = atlas.findRegion("blocks-purple-hover-03");
    final TextureRegion BLOCKS_YELLOW_COMPRESSED_03 = atlas.findRegion("blocks-yellow-compressed-03");
    final TextureRegion BLOCKS_YELLOW_PANIC_04 = atlas.findRegion("blocks-yellow-panic-04");
    GARBAGE_PLAIN = atlas.findRegion("garbage-plain");
    final TextureRegion BLOCKS_RED_HOVER_01 = atlas.findRegion("blocks-red-hover-01");
    final TextureRegion BLOCKS_BLUE_COMPRESSED_04 = atlas.findRegion("blocks-blue-compressed-04");
    BLOCKS_PURPLE_IDLE = atlas.findRegion("blocks-purple-idle");
    final TextureRegion BLOCKS_PURPLE_HOVER_04 = atlas.findRegion("blocks-purple-hover-04");
    final TextureRegion BLOCKS_PURPLE_PANIC_02 = atlas.findRegion("blocks-purple-panic-02");
    final TextureRegion BLOCKS_GREEN_COMPRESSED_03 = atlas.findRegion("blocks-green-compressed-03");
    BLOCKS_RED_IDLE = atlas.findRegion("blocks-red-idle");
    final TextureRegion BLOCKS_YELLOW_HOVER_01 = atlas.findRegion("blocks-yellow-hover-01");
    final TextureRegion BLOCKS_PURPLE_LAND_01 = atlas.findRegion("blocks-purple-land-01");
    final TextureRegion BLOCKS_PURPLE_COMPRESSED_04 = atlas.findRegion("blocks-purple-compressed-04");
    final TextureRegion BLOCKS_YELLOW_COMPRESSED_01 = atlas.findRegion("blocks-yellow-compressed-01");
    final TextureRegion BLOCKS_PURPLE_COMPRESSED_02 = atlas.findRegion("blocks-purple-compressed-02");
    final TextureRegion BLOCKS_RED_COMPRESSED_01 = atlas.findRegion("blocks-red-compressed-01");
    final TextureRegion BLOCKS_RED_HOVER_02 = atlas.findRegion("blocks-red-hover-02");
    GARBAGE_TOPLEFT = atlas.findRegion("garbage-topleft");
    final TextureRegion BLOCKS_PURPLE_COMPRESSED_03 = atlas.findRegion("blocks-purple-compressed-03");
    final TextureRegion BLOCKS_RED_COMPRESSED_04 = atlas.findRegion("blocks-red-compressed-04");
    GARBAGE_TOPLEFTBOTTOM = atlas.findRegion("garbage-topleftbottom");
    BLOCKS_YELLOW_IDLE = atlas.findRegion("blocks-yellow-idle");
    final TextureRegion BLOCKS_PURPLE_LAND_02 = atlas.findRegion("blocks-purple-land-02");
    BLOCKS_PURPLE_HAPPY = atlas.findRegion("blocks-purple-happy");
    final TextureRegion BLOCKS_BLUE_COMPRESSED_03 = atlas.findRegion("blocks-blue-compressed-03");
    final TextureRegion BLOCKS_BLUE_HOVER_03 = atlas.findRegion("blocks-blue-hover-03");
    GARBAGE_RIGHT = atlas.findRegion("garbage-right");
    final TextureRegion BLOCKS_RED_PANIC_02 = atlas.findRegion("blocks-red-panic-02");
    BLOCKS_GREEN_HAPPY = atlas.findRegion("blocks-green-happy");
    final TextureRegion BLOCKS_GREEN_PANIC_02 = atlas.findRegion("blocks-green-panic-02");
    final TextureRegion BLOCKS_YELLOW_LAND_02 = atlas.findRegion("blocks-yellow-land-02");
    final TextureRegion BLOCKS_BLUE_HOVER_02 = atlas.findRegion("blocks-blue-hover-02");
    final TextureRegion BLOCKS_RED_HOVER_03 = atlas.findRegion("blocks-red-hover-03");
    final TextureRegion BLOCKS_GREEN_LAND_01 = atlas.findRegion("blocks-green-land-01");
    BLOCKS_BLUE_IDLE = atlas.findRegion("blocks-blue-idle");
    BLOCKS_RED_HAPPY = atlas.findRegion("blocks-red-happy");
    final TextureRegion BLOCKS_YELLOW_HOVER_04 = atlas.findRegion("blocks-yellow-hover-04");
    GARBAGE_SINGLE = atlas.findRegion("garbage-single");
    EYE = atlas.findRegion("eye");
    final TextureRegion BLOCKS_GREEN_PANIC_03 = atlas.findRegion("blocks-green-panic-03");
    GARBAGE_BOTTOMLEFT = atlas.findRegion("garbage-bottomleft");
    final TextureRegion BLOCKS_BLUE_PANIC_03 = atlas.findRegion("blocks-blue-panic-03");
    final TextureRegion ENDPOPUP_LOSE = atlas.findRegion("EndPopUp-Lose");
    final TextureRegion BLOCKS_GREEN_HOVER_02 = atlas.findRegion("blocks-green-hover-02");
    final TextureRegion BLOCKS_YELLOW_PANIC_03 = atlas.findRegion("blocks-yellow-panic-03");
    final TextureRegion BLOCKS_PURPLE_FLASH = atlas.findRegion("blocks-purple-flash");
    final TextureRegion BLOCKS_BLUE_COMPRESSED_01 = atlas.findRegion("blocks-blue-compressed-01");
    final TextureRegion BLOCKS_GREEN_COMPRESSED_04 = atlas.findRegion("blocks-green-compressed-04");
    final TextureRegion BLOCKS_RED_LAND_01 = atlas.findRegion("blocks-red-land-01");
    final TextureRegion BLOCKS_YELLOW_COMPRESSED_04 = atlas.findRegion("blocks-yellow-compressed-04");
    final TextureRegion BLOCKS_RED_COMPRESSED_03 = atlas.findRegion("blocks-red-compressed-03");
    GARBAGE_BOTTOM = atlas.findRegion("garbage-bottom");
    final TextureRegion BLOCKS_GREEN_PANIC_04 = atlas.findRegion("blocks-green-panic-04");
    final TextureRegion BLOCKS_RED_COMPRESSED_02 = atlas.findRegion("blocks-red-compressed-02");
    final TextureRegion BLOCKS_BLUE_HOVER_04 = atlas.findRegion("blocks-blue-hover-04");
    final TextureRegion BLOCKS_YELLOW_PANIC_01 = atlas.findRegion("blocks-yellow-panic-01");
    final TextureRegion BLOCKS_GREEN_HOVER_04 = atlas.findRegion("blocks-green-hover-04");
    final TextureRegion BLOCKS_GREEN_HOVER_01 = atlas.findRegion("blocks-green-hover-01");
    final TextureRegion BLOCKS_BLUE_LAND_02 = atlas.findRegion("blocks-blue-land-02");
    final TextureRegion BLOCKS_YELLOW_FLASH = atlas.findRegion("blocks-yellow-flash");
    final TextureRegion BLOCKS_YELLOW_LAND_01 = atlas.findRegion("blocks-yellow-land-01");
    final TextureRegion BLOCKS_PURPLE_COMPRESSED_01 = atlas.findRegion("blocks-purple-compressed-01");
    GARBAGE_TOPRIGHT = atlas.findRegion("garbage-topright");
    final TextureRegion BLOCKS_GREEN_HOVER_03 = atlas.findRegion("blocks-green-hover-03");
    final TextureRegion BLOCKS_GREEN_PANIC_01 = atlas.findRegion("blocks-green-panic-01");
    final TextureRegion BLOCKS_YELLOW_COMPRESSED_02 = atlas.findRegion("blocks-yellow-compressed-02");
    final TextureRegion BLOCKS_YELLOW_PANIC_02 = atlas.findRegion("blocks-yellow-panic-02");
    final TextureRegion BLOCKS_RED_FLASH = atlas.findRegion("blocks-red-flash");
    final TextureRegion BLOCKS_PURPLE_PANIC_03 = atlas.findRegion("blocks-purple-panic-03");
    final TextureRegion BLOCKS_PURPLE_HOVER_02 = atlas.findRegion("blocks-purple-hover-02");
    final TextureRegion BLOCKS_BLUE_COMPRESSED_02 = atlas.findRegion("blocks-blue-compressed-02");
    final TextureRegion BLOCKS_BLUE_LAND_01 = atlas.findRegion("blocks-blue-land-01");

    YELLOW_LANDING = new TextureRegion[]{BLOCKS_YELLOW_LAND_01, BLOCKS_YELLOW_IDLE, BLOCKS_YELLOW_LAND_02, BLOCKS_YELLOW_IDLE};
    BLUE_LANDING = new TextureRegion[]{BLOCKS_BLUE_LAND_01, BLOCKS_BLUE_IDLE, BLOCKS_BLUE_LAND_02, BLOCKS_BLUE_IDLE};
    RED_LANDING = new TextureRegion[]{BLOCKS_RED_LAND_01, BLOCKS_RED_IDLE, BLOCKS_RED_LAND_02, BLOCKS_RED_IDLE};
    GREEN_LANDING = new TextureRegion[]{BLOCKS_GREEN_LAND_01, BLOCKS_GREEN_IDLE, BLOCKS_GREEN_LAND_02, BLOCKS_GREEN_IDLE};
    PURPLE_LANDING = new TextureRegion[]{BLOCKS_PURPLE_LAND_01, BLOCKS_PURPLE_IDLE, BLOCKS_PURPLE_LAND_02, BLOCKS_PURPLE_IDLE};

    YELLOW_AIRBOUNCING = new TextureRegion[]{BLOCKS_YELLOW_HOVER_01, BLOCKS_YELLOW_HOVER_02, BLOCKS_YELLOW_HOVER_03, BLOCKS_YELLOW_HOVER_04, BLOCKS_YELLOW_HOVER_03, BLOCKS_YELLOW_HOVER_02, BLOCKS_YELLOW_HOVER_01};
    BLUE_AIRBOUNCING = new TextureRegion[]{BLOCKS_BLUE_HOVER_01, BLOCKS_BLUE_HOVER_02, BLOCKS_BLUE_HOVER_03, BLOCKS_BLUE_HOVER_04, BLOCKS_BLUE_HOVER_03, BLOCKS_BLUE_HOVER_02, BLOCKS_BLUE_HOVER_01};
    RED_AIRBOUNCING = new TextureRegion[]{BLOCKS_RED_HOVER_01, BLOCKS_RED_HOVER_02, BLOCKS_RED_HOVER_03, BLOCKS_RED_HOVER_04, BLOCKS_RED_HOVER_03, BLOCKS_RED_HOVER_02, BLOCKS_RED_HOVER_01};
    GREEN_AIRBOUNCING = new TextureRegion[]{BLOCKS_GREEN_HOVER_01, BLOCKS_GREEN_HOVER_02, BLOCKS_GREEN_HOVER_03, BLOCKS_GREEN_HOVER_04, BLOCKS_GREEN_HOVER_03, BLOCKS_GREEN_HOVER_02, BLOCKS_GREEN_HOVER_01};
    PURPLE_AIRBOUNCING = new TextureRegion[]{BLOCKS_PURPLE_HOVER_01, BLOCKS_PURPLE_HOVER_02, BLOCKS_PURPLE_HOVER_03, BLOCKS_PURPLE_HOVER_04, BLOCKS_PURPLE_HOVER_03, BLOCKS_PURPLE_HOVER_02, BLOCKS_PURPLE_HOVER_01};

    YELLOW_PANICKING = new Animation(4f / CORE_FREQUENCY, BLOCKS_YELLOW_PANIC_01, BLOCKS_YELLOW_PANIC_02, BLOCKS_YELLOW_PANIC_03, BLOCKS_YELLOW_PANIC_04, BLOCKS_YELLOW_PANIC_03, BLOCKS_YELLOW_PANIC_02, BLOCKS_YELLOW_PANIC_01);
    BLUE_PANICKING = new Animation(4f / CORE_FREQUENCY, BLOCKS_BLUE_PANIC_01, BLOCKS_BLUE_PANIC_02, BLOCKS_BLUE_PANIC_03, BLOCKS_BLUE_PANIC_04, BLOCKS_BLUE_PANIC_03, BLOCKS_BLUE_PANIC_02, BLOCKS_BLUE_PANIC_01);
    RED_PANICKING = new Animation(4f / CORE_FREQUENCY, BLOCKS_RED_PANIC_01, BLOCKS_RED_PANIC_02, BLOCKS_RED_PANIC_03, BLOCKS_RED_PANIC_04, BLOCKS_RED_PANIC_03, BLOCKS_RED_PANIC_02, BLOCKS_RED_PANIC_01);
    GREEN_PANICKING = new Animation(4f / CORE_FREQUENCY, BLOCKS_GREEN_PANIC_01, BLOCKS_GREEN_PANIC_02, BLOCKS_GREEN_PANIC_03, BLOCKS_GREEN_PANIC_04, BLOCKS_GREEN_PANIC_03, BLOCKS_GREEN_PANIC_02, BLOCKS_GREEN_PANIC_01);
    PURPLE_PANICKING = new Animation(4f / CORE_FREQUENCY, BLOCKS_PURPLE_PANIC_01, BLOCKS_PURPLE_PANIC_02, BLOCKS_PURPLE_PANIC_03, BLOCKS_PURPLE_PANIC_04, BLOCKS_PURPLE_PANIC_03, BLOCKS_PURPLE_PANIC_02, BLOCKS_PURPLE_PANIC_01);

    YELLOW_COMPRESSING = new Animation(4f / CORE_FREQUENCY, BLOCKS_YELLOW_COMPRESSED_01, BLOCKS_YELLOW_COMPRESSED_02, BLOCKS_YELLOW_COMPRESSED_03, BLOCKS_YELLOW_COMPRESSED_04);
    BLUE_COMPRESSING = new Animation(4f / CORE_FREQUENCY, BLOCKS_BLUE_COMPRESSED_01, BLOCKS_BLUE_COMPRESSED_02, BLOCKS_BLUE_COMPRESSED_03, BLOCKS_BLUE_COMPRESSED_04);
    RED_COMPRESSING = new Animation(4f / CORE_FREQUENCY, BLOCKS_RED_COMPRESSED_01, BLOCKS_RED_COMPRESSED_02, BLOCKS_RED_COMPRESSED_03, BLOCKS_RED_COMPRESSED_04);
    GREEN_COMPRESSING = new Animation(4f / CORE_FREQUENCY, BLOCKS_GREEN_COMPRESSED_01, BLOCKS_GREEN_COMPRESSED_02, BLOCKS_GREEN_COMPRESSED_03, BLOCKS_GREEN_COMPRESSED_04);
    PURPLE_COMPRESSING = new Animation(4f / CORE_FREQUENCY, BLOCKS_PURPLE_COMPRESSED_01, BLOCKS_PURPLE_COMPRESSED_02, BLOCKS_PURPLE_COMPRESSED_03, BLOCKS_PURPLE_COMPRESSED_04);

    YELLOW_BLINKING = new Animation(1f / CORE_FREQUENCY, BLOCKS_YELLOW_FLASH, BLOCKS_YELLOW_IDLE);
    BLUE_BLINKING = new Animation(1f / CORE_FREQUENCY, BLOCKS_BLUE_FLASH, BLOCKS_BLUE_IDLE);
    RED_BLINKING = new Animation(1f / CORE_FREQUENCY, BLOCKS_RED_FLASH, BLOCKS_RED_IDLE);
    GREEN_BLINKING = new Animation(1f / CORE_FREQUENCY, BLOCKS_GREEN_FLASH, BLOCKS_GREEN_IDLE);
    PURPLE_BLINKING = new Animation(1f / CORE_FREQUENCY, BLOCKS_PURPLE_FLASH, BLOCKS_PURPLE_IDLE);
    GARBAGE_BLINKING = new Animation(1f / CORE_FREQUENCY, GARBAGE_BLINK, GARBAGE_PLAIN);

    CURSOR = new Animation(16f / CORE_FREQUENCY, CURSOR_01, CURSOR_02, CURSOR_03, CURSOR_02);
  }

  public TextureRegion getBlockRegion(final BlockSituation blockSituation, final float stateTime, final boolean compressed, final boolean panicking) {
    final NonLoopingAnimation currentAnimation = animations.get(blockSituation.getId());
    final BlockState state = blockSituation.getState();
    final BlockType type = blockSituation.getType();

    switch (state) {
      case EXPLODING:
        switch (type) {
          case YELLOW:
            return BLOCKS_YELLOW_HAPPY;
          case GREEN:
            return BLOCKS_GREEN_HAPPY;
          case RED:
            return BLOCKS_RED_HAPPY;
          case PURPLE:
            return BLOCKS_PURPLE_HAPPY;
          case BLUE:
            return BLOCKS_BLUE_HAPPY;
          case GARBAGE:
            throw new IllegalStateException("you should not be there");
        }

      case REVEALING:
        return GARBAGE_SINGLE;

      case AIRBOUNCING:
        if (currentAnimation == null) {
          NonLoopingAnimation animation = null;
          switch (type) {
            case YELLOW:
              animation = new NonLoopingAnimation(stateTime, 4.0f / CORE_FREQUENCY, YELLOW_AIRBOUNCING);
              break;
            case BLUE:
              animation = new NonLoopingAnimation(stateTime, 4.0f / CORE_FREQUENCY, BLUE_AIRBOUNCING);
              break;
            case PURPLE:
              animation = new NonLoopingAnimation(stateTime, 4.0f / CORE_FREQUENCY, PURPLE_AIRBOUNCING);
              break;
            case RED:
              animation = new NonLoopingAnimation(stateTime, 4.0f / CORE_FREQUENCY, RED_AIRBOUNCING);
              break;
            case GREEN:
              animation = new NonLoopingAnimation(stateTime, 4.0f / CORE_FREQUENCY, GREEN_AIRBOUNCING);
              break;
            case GARBAGE:
              throw new IllegalStateException("you should not be there");
          }
          animations.put(blockSituation.getId(), animation);
          return animation.getKeyFrame(stateTime);
        }
        //$FALL-THROUGH$

      case BLINKING:
      case FALLING:
      case SWITCHING_BACK:
      case SWITCHING_FORTH:
      case DONE_BLINKING:
      case DONE_REVEALING:
      case DONE_HOVERING:
      case DONE_SWITCHING_FORTH:
      case HOVERING:
      case DONE_AIRBOUNCING:
      case IDLE:
        if (blockSituation.hasJustLand() && state == IDLE && type != GARBAGE && type != TUTORIAL) {
          NonLoopingAnimation animation = null;
          switch (type) {
            case YELLOW:
              animation = new NonLoopingAnimation(stateTime, 4.0f / CORE_FREQUENCY, YELLOW_LANDING);
              break;
            case BLUE:
              animation = new NonLoopingAnimation(stateTime, 4.0f / CORE_FREQUENCY, BLUE_LANDING);
              break;
            case PURPLE:
              animation = new NonLoopingAnimation(stateTime, 4.0f / CORE_FREQUENCY, PURPLE_LANDING);
              break;
            case RED:
              animation = new NonLoopingAnimation(stateTime, 4.0f / CORE_FREQUENCY, RED_LANDING);
              break;
            case GREEN:
              animation = new NonLoopingAnimation(stateTime, 4.0f / CORE_FREQUENCY, GREEN_LANDING);
              break;
          }
          animations.put(blockSituation.getId(), animation);
          return animation.getKeyFrame(stateTime);
        }
        if (currentAnimation != null) {
          if (currentAnimation.isAnimationFinished(stateTime)) {
            animations.remove(blockSituation.getId());
          } else {
            return currentAnimation.getKeyFrame(stateTime);
          }
        }
        switch (type) {
          case YELLOW:
            if (compressed && state == IDLE) {
              return YELLOW_COMPRESSING.getKeyFrame(stateTime, true);
            }
            if (panicking && state == IDLE) {
              return YELLOW_PANICKING.getKeyFrame(stateTime, true);
            }
            return state == BLINKING ? YELLOW_BLINKING.getKeyFrame(stateTime, true) : BLOCKS_YELLOW_IDLE;
          case GREEN:
            if (compressed && state == IDLE) {
              return GREEN_COMPRESSING.getKeyFrame(stateTime, true);
            }
            if (panicking && state == IDLE) {
              return GREEN_PANICKING.getKeyFrame(stateTime, true);
            }
            return state == BLINKING ? GREEN_BLINKING.getKeyFrame(stateTime, true) : BLOCKS_GREEN_IDLE;
          case RED:
            if (compressed && state == IDLE) {
              return RED_COMPRESSING.getKeyFrame(stateTime, true);
            }
            if (panicking && state == IDLE) {
              return RED_PANICKING.getKeyFrame(stateTime, true);
            }
            return state == BLINKING ? RED_BLINKING.getKeyFrame(stateTime, true) : BLOCKS_RED_IDLE;
          case PURPLE:
            if (compressed && state == IDLE) {
              return PURPLE_COMPRESSING.getKeyFrame(stateTime, true);
            }
            if (panicking && state == IDLE) {
              return PURPLE_PANICKING.getKeyFrame(stateTime, true);
            }
            return state == BLINKING ? PURPLE_BLINKING.getKeyFrame(stateTime, true) : BLOCKS_PURPLE_IDLE;
          case BLUE:
            if (compressed && state == IDLE) {
              return BLUE_COMPRESSING.getKeyFrame(stateTime, true);
            }
            if (panicking && state == IDLE) {
              return BLUE_PANICKING.getKeyFrame(stateTime, true);
            }
            return state == BLINKING ? BLUE_BLINKING.getKeyFrame(stateTime, true) : BLOCKS_BLUE_IDLE;
          case TUTORIAL:
            return GARBAGE_SINGLE;
          case GARBAGE:
            if (state == BLINKING && GARBAGE_BLINKING.getKeyFrame(stateTime, true) == GARBAGE_BLINK) {
              return GARBAGE_BLINK;
            }
            final int garbageBlockType = blockSituation.getGarbageBlockType();
            switch (garbageBlockType) {
              case GarbageBlockType.DOWN:
                return GARBAGE_BOTTOM;
              case GarbageBlockType.DOWNLEFT:
                return GARBAGE_BOTTOMLEFT;
              case GarbageBlockType.DOWNRIGHT:
                return GARBAGE_BOTTOMRIGHT;
              case GarbageBlockType.LEFT:
                return GARBAGE_LEFT;
              case GarbageBlockType.PLAIN:
                return GARBAGE_PLAIN;
              case GarbageBlockType.RIGHT:
                return GARBAGE_RIGHT;
              case GarbageBlockType.UP:
                return GARBAGE_TOP;
              case GarbageBlockType.UPDOWN:
                return GARBAGE_TOPBOTTOM;
              case GarbageBlockType.UPLEFT:
                return GARBAGE_TOPLEFT;
              case GarbageBlockType.UPLEFTDOWN:
                return GARBAGE_TOPLEFTBOTTOM;
              case GarbageBlockType.UPRIGHT:
                return GARBAGE_TOPRIGHT;
              case GarbageBlockType.UPRIGHTDOWN:
                return GARBAGE_TOPRIGHTBOTTOM;
              default:
                throw new IllegalStateException("Undefined garbage block type: " + garbageBlockType);
            }
        }
      case DONE_EXPLODING:
      case TO_DELETE:
        animations.remove(blockSituation.getId());
        return null;
      default:
        throw new IllegalStateException("Undefined block state: " + state);
    }
  }
}
