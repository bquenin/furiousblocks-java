package me.pixodro.furiousblocks.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MenuNinePatch extends NinePatch {
  private static MenuNinePatch instance;

  private MenuNinePatch() {
    super(new TextureRegion(new Texture(Gdx.files.internal("graphics/menuskin.png")), 24, 24), 8, 8, 8, 8);
  }

  public static MenuNinePatch getInstance() {
    if (instance == null) {
      instance = new MenuNinePatch();
    }
    return instance;
  }
}