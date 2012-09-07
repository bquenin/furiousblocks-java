package me.pixodro.furiousblocks.game.screen;

import me.pixodro.furiousblocks.game.Assets;

class PanelRenderer {
  private final Assets assets;

  public PanelRenderer(final Assets assets) {
    this.assets = assets;
  }


  //  private void renderFaces(final SpriteBatch batcher, final int xpos, final int ypos, final long tick, final int delta) {
  //    for (GarbageSituation garbageSituation : panelSituation.getGarbageSituation()) {
  //      if (garbageSituation.getHeight() == 1 || garbageSituation.getOrigin().y - garbageSituation.getHeight() + 1 > heightInBlocks) {
  //        continue;
  //      }
  //
  //      final int x = xpos + (TILE_SIZE * garbageSituation.getOrigin().x);
  //      final int x2 = xpos + (TILE_SIZE * (garbageSituation.getOrigin().x + garbageSituation.getWidth()));
  //      final int y = ypos + (TILE_SIZE * (garbageSituation.getOrigin().y - garbageSituation.getHeight() + 1)) + ((panelSituation.getScrollingOffset() * /*MasterSet.HEIGHT*/32) / FuriousBlocksCoreDefaults.BLOCK_LOGICALHEIGHT) + delta;
  //
  //      BlockSituation origin = panelSituation.getBlockSituations()[garbageSituation.getOrigin().x][garbageSituation.getOrigin().y > heightInBlocks ? heightInBlocks : garbageSituation.getOrigin().y];
  //      batcher.setColor(Color.WHITE);
  //      batcher.draw(origin.getState() == BlockState.REVEALING || origin.getState() == BlockState.BLINKING ? Assets.EYE_SURPRISE : Assets.EYE, //
  //          x, y, //
  //          TILE_SIZE * garbageSituation.getHeight() >= 3 * TILE_SIZE ? 3 * TILE_SIZE : TILE_SIZE * garbageSituation.getHeight(), //
  //          TILE_SIZE * garbageSituation.getHeight() >= 3 * TILE_SIZE ? 3 * TILE_SIZE : TILE_SIZE * garbageSituation.getHeight());
  //
  //      batcher.draw(origin.getState() == BlockState.REVEALING || origin.getState() == BlockState.BLINKING ? Assets.EYE_SURPRISE : Assets.EYE, //
  //          x2, y, //
  //          TILE_SIZE * garbageSituation.getHeight() >= 3 * TILE_SIZE ? -3 * TILE_SIZE : -TILE_SIZE * garbageSituation.getHeight(), //
  //          TILE_SIZE * garbageSituation.getHeight() >= 3 * TILE_SIZE ? 3 * TILE_SIZE : TILE_SIZE * garbageSituation.getHeight());
  //      //      batcher.draw(origin.getState() == BlockState.REVEALING || origin.getState() == BlockState.BLINKING ? Assets.MOUTH_SURPRISE : Assets.MOUTH, //
  //      //          (x + x2) / 2, y - TILE_SIZE / 2, //
  //      //          TILE_SIZE * garbageSituation.getHeight() >= 3 * TILE_SIZE ? 3 * TILE_SIZE / 2 : TILE_SIZE * garbageSituation.getHeight() / 2, //
  //      //          TILE_SIZE * garbageSituation.getHeight() >= 3 * TILE_SIZE ? 3 * TILE_SIZE : TILE_SIZE * garbageSituation.getHeight());
  //    }
  //  }

  //    private void renderPlayerId(@SuppressWarnings("unused") final long tick) {
  //        final int x = OUTER_WIDTH;
  //        final int y = 0;
  //
  //        // // Avatar
  //        // CharacterSet.avatarSheet.startUse();
  //        // CharacterSet.avatarSheet.renderInUse(x, y, CharacterSet.getCharacterAvatar(player.getCharacter()).x,
  //        // CharacterSet.getCharacterAvatar(player.getCharacter()).y);
  //        // CharacterSet.avatarSheet.endUse();
  //
  //        // Name
  //        backBuffer.setColor(color.getColor().darker());
  //        backBuffer.setFont(Fonts.BANDLESS_36_ITALIC.coop32);
  //        backBuffer.drawString(player.getName(), x + (MasterSet.WIDTH / 4), ((MasterSet.HEIGHT * 2) / 3));
  //    }

  // private void renderBackground(@SuppressWarnings("unused") final long tick) {
  // final int x = OUTER_WIDTH;
  // final int y = playerIDRendererHeight + garbageStackRendererHeight;
  //
  // CharacterSet.bgSheet.startUse();
  // CharacterSet.bgSheet.renderInUse(x, y, CharacterSet.getCharacterBackground(player.getCharacter()).x,
  // CharacterSet.getCharacterBackground(player.getCharacter()).y);
  // CharacterSet.bgSheet.endUse();
  // }

  //    private void renderGarbageStack(@SuppressWarnings("unused") final long tick) {
  //        final int offset = playerIDRendererHeight;
  //        backBuffer.setColor(Color.white);
  //        backBuffer.fillRect(0, offset, totalWidthInPixels, 2 * MasterSet.HEIGHT);
  //        int x = PanelRenderer.OUTER_WIDTH;
  //        final int y = offset + ((2 * MasterSet.HEIGHT) / 4);
  //        int width;
  //        for (final GarbageSituation garbageSituation : panelSituation.getGarbageSituation()) {
  //            if (garbageSituation == null) {
  //                continue;
  //            }
  //
  //            if (garbageSituation.getHeight() > 1) {
  //                final Assets template = GarbageStackSet.fromPalette(FuriousBlocksRenderer.getPlayerColor(garbageSituation.getOwner())).CHAIN();
  //                width = template.getWidth();
  //                backBuffer.drawImage(template, x, y);
  //
  //                backBuffer.setColor(Color.white);
  //                backBuffer.setFont(Fonts.BANDLESS_18_ITALIC.coop32);
  //                backBuffer.drawString("x" + garbageSituation.getHeight(), x + (MasterSet.WIDTH / 4), y + (MasterSet.HEIGHT / 4));
  //            } else {
  //                if (garbageSituation.getWidth() == widthInBlocks) {
  //                    final Assets template = GarbageStackSet.fromPalette(FuriousBlocksRenderer.getPlayerColor(garbageSituation.getOwner())).CHAIN();
  //                    width = template.getWidth();
  //                    backBuffer.drawImage(template, x, y);
  //                } else {
  //                    final Assets template = GarbageStackSet.fromPalette(FuriousBlocksRenderer.getPlayerColor(garbageSituation.getOwner())).COMBO();
  //                    width = template.getWidth() * ((garbageSituation.getWidth() / 2) + (garbageSituation.getWidth() % 2));
  //
  //                    for (int w = 0; w < width; w += template.getWidth()) {
  //                        backBuffer.drawImage(template, x + w, y);
  //                        final int tmp = (garbageSituation.getWidth() % 2) == 1 ? w + (template.getWidth() / 2) : w;
  //                        if ((tmp + template.getWidth()) <= width) {
  //                            backBuffer.drawImage(template, x + tmp, y + template.getHeight());
  //                        }
  //                    }
  //                }
  //            }
  //            x += width + 4;
  //        }
  //    }

  //    private void renderFrameAndHealthBar(@SuppressWarnings("unused") final long tick) {
  //        final int offset = playerIDRendererHeight + garbageStackRendererHeight;
  //        final int maxWidth = OUTER_WIDTH + panelWidthInPixels;
  //        final int maxHealth = totalHeightInPixels - (offset + OUTER_WIDTH);
  //
  //        int level;
  //        // Health bar content
  //        if (!panelSituation.isGracing()) {
  //            level = maxHealth;
  //        } else {
  //            level = (panelSituation.getFreezingTime() * maxHealth) / (2 * FuriousBlocksCoreDefaults.CORE_FREQUENCY);
  //            if (level > maxHealth) {
  //                level = maxHealth;
  //            }
  //        }
  //
  //        backBuffer.setColor(color.getColor().darker());
  //        backBuffer.fillRect(maxWidth, totalHeightInPixels, PanelSet.WIDTH, -level);
  //
  //        final SpriteSheet sheet = PanelSet.fromPalette(color).sheet;
  //        sheet.startUse();
  //
  //        // Score frame
  //        sheet.renderInUse(maxWidth + (OUTER_WIDTH), offset + OUTER_HEIGHT, PanelIndex.OUTER_UP.index.x, PanelIndex.OUTER_UP.index.y);
  //        sheet.renderInUse(maxWidth + (OUTER_WIDTH * 2), offset + OUTER_HEIGHT, PanelIndex.OUTER_UP.index.x, PanelIndex.OUTER_UP.index.y);
  //        sheet.renderInUse(maxWidth + (OUTER_WIDTH * 3), offset + OUTER_HEIGHT, PanelIndex.OUTER_UP.index.x, PanelIndex.OUTER_UP.index.y);
  //        sheet.renderInUse(maxWidth + (OUTER_WIDTH * 4), offset + OUTER_HEIGHT, PanelIndex.OUTER_UP.index.x, PanelIndex.OUTER_UP.index.y);
  //        sheet.renderInUse(maxWidth + (OUTER_WIDTH * 5), offset + OUTER_HEIGHT, PanelIndex.OUTER_UP.index.x, PanelIndex.OUTER_UP.index.y);
  //        sheet.renderInUse(maxWidth + (OUTER_WIDTH * 6), offset + OUTER_HEIGHT, PanelIndex.OUTER_UP.index.x, PanelIndex.OUTER_UP.index.y);
  //        sheet.renderInUse(maxWidth + (OUTER_WIDTH * 7), offset + OUTER_HEIGHT, PanelIndex.OUTER_UP.index.x, PanelIndex.OUTER_UP.index.y);
  //        sheet.renderInUse(maxWidth + (OUTER_WIDTH * 8), offset + OUTER_HEIGHT, PanelIndex.OUTER_UP.index.x, PanelIndex.OUTER_UP.index.y);
  //        sheet.renderInUse(maxWidth + (OUTER_WIDTH * 9), offset + OUTER_HEIGHT, PanelIndex.OUTER_UPRIGHT.index.x, PanelIndex.OUTER_UPRIGHT.index.y);
  //        sheet.renderInUse(maxWidth + (OUTER_WIDTH * 9), offset + (OUTER_HEIGHT * 2), PanelIndex.OUTER_RIGHT.index.x, PanelIndex.OUTER_RIGHT.index.y);
  //        sheet.renderInUse(maxWidth + (OUTER_WIDTH * 9), offset + (OUTER_HEIGHT * 3), PanelIndex.OUTER_RIGHT.index.x, PanelIndex.OUTER_RIGHT.index.y);
  //        // sheet.renderInUse(maxWidth + OUTER_WIDTH * 9, offset + OUTER_HEIGHT * 4, PanelIndex.OUTER_RIGHT.index.x, PanelIndex.OUTER_RIGHT.index.y);
  //        sheet.renderInUse(maxWidth + (OUTER_WIDTH * 9), offset + (OUTER_HEIGHT * 4), PanelIndex.OUTER_DOWNRIGHT.index.x, PanelIndex.OUTER_DOWNRIGHT.index.y);
  //        sheet.renderInUse(maxWidth + (OUTER_WIDTH), offset + (OUTER_HEIGHT * 4), PanelIndex.OUTER_DOWN.index.x, PanelIndex.OUTER_DOWN.index.y);
  //        sheet.renderInUse(maxWidth + (OUTER_WIDTH * 2), offset + (OUTER_HEIGHT * 4), PanelIndex.OUTER_DOWN.index.x, PanelIndex.OUTER_DOWN.index.y);
  //        sheet.renderInUse(maxWidth + (OUTER_WIDTH * 3), offset + (OUTER_HEIGHT * 4), PanelIndex.OUTER_DOWN.index.x, PanelIndex.OUTER_DOWN.index.y);
  //        sheet.renderInUse(maxWidth + (OUTER_WIDTH * 4), offset + (OUTER_HEIGHT * 4), PanelIndex.OUTER_DOWN.index.x, PanelIndex.OUTER_DOWN.index.y);
  //        sheet.renderInUse(maxWidth + (OUTER_WIDTH * 5), offset + (OUTER_HEIGHT * 4), PanelIndex.OUTER_DOWN.index.x, PanelIndex.OUTER_DOWN.index.y);
  //        sheet.renderInUse(maxWidth + (OUTER_WIDTH * 6), offset + (OUTER_HEIGHT * 4), PanelIndex.OUTER_DOWN.index.x, PanelIndex.OUTER_DOWN.index.y);
  //        sheet.renderInUse(maxWidth + (OUTER_WIDTH * 7), offset + (OUTER_HEIGHT * 4), PanelIndex.OUTER_DOWN.index.x, PanelIndex.OUTER_DOWN.index.y);
  //        sheet.renderInUse(maxWidth + (OUTER_WIDTH * 8), offset + (OUTER_HEIGHT * 4), PanelIndex.OUTER_DOWN.index.x, PanelIndex.OUTER_DOWN.index.y);
  //
  //        for (int y = offset; y < totalHeightInPixels; y += OUTER_HEIGHT) {
  //            // Right health bar
  //            if (y >= (offset + OUTER_HEIGHT)) {
  //                sheet.renderInUse(maxWidth + OUTER_WIDTH, y, PanelIndex.OUTER_RIGHT.index.x, PanelIndex.OUTER_RIGHT.index.y);
  //            } else {
  //                // Top health bar
  //                sheet.renderInUse(maxWidth + OUTER_WIDTH, y, PanelIndex.OUTER_UPRIGHT.index.x, PanelIndex.OUTER_UPRIGHT.index.y);
  //                sheet.renderInUse(maxWidth, y, PanelIndex.OUTER_UP.index.x, PanelIndex.OUTER_UP.index.y);
  //            }
  //
  //            // Left border
  //            sheet.renderInUse(0, y, PanelIndex.OUTER_LEFT.index.x, PanelIndex.OUTER_LEFT.index.y);
  //
  //            // Right border
  //            sheet.renderInUse(maxWidth, y, PanelIndex.OUTER_RIGHT.index.x, PanelIndex.OUTER_RIGHT.index.y);
  //        }
  //
  //        for (int x = OUTER_WIDTH; x < maxWidth; x += OUTER_WIDTH) {
  //            // Top border
  //            sheet.renderInUse(x, offset - OUTER_HEIGHT, PanelIndex.OUTER_UP.index.x, PanelIndex.OUTER_UP.index.y);
  //        }
  //
  //        // Top left border
  //        sheet.renderInUse(0, offset - OUTER_HEIGHT, PanelIndex.OUTER_UPLEFT.index.x, PanelIndex.OUTER_UPLEFT.index.y);
  //
  //        // Top right border
  //        sheet.renderInUse(maxWidth, offset - OUTER_HEIGHT, PanelIndex.OUTER_UPRIGHT.index.x, PanelIndex.OUTER_UPRIGHT.index.y);
  //
  //        sheet.endUse();
  //
  //        backBuffer.setFont(Fonts.BUBBLEGUM_18_ITALIC.coop32);
  //        backBuffer.setColor(color.getColor());
  //        backBuffer.drawString("score", (maxWidth + (OUTER_WIDTH * 2)) - 5, (offset + OUTER_HEIGHT) - 4);
  //
  //        backBuffer.setFont(Fonts.BUBBLEGUM_24_ITALIC.coop32);
  //        backBuffer.setColor(Color.white);
  //        backBuffer.drawString(String.valueOf(panelSituation.getScore()), (maxWidth + (OUTER_WIDTH * 2)) - 5, offset + (OUTER_HEIGHT * 2) + 4);
  //
  //        backBuffer.setFont(Fonts.BANDLESS_128_ITALIC.coop32);
  //        backBuffer.setDrawMode(Graphics.MODE_NORMAL);
  //        backBuffer.setColor(Color.black);
  //        backBuffer.drawString(Integer.toString(panelSituation.getRank()), maxWidth + (OUTER_WIDTH * 5), offset + (OUTER_HEIGHT * 18));
  //
  //        backBuffer.setDrawMode(Graphics.MODE_SCREEN);
  //        backBuffer.setColor(Color.lightGray);
  //        backBuffer.drawString(Integer.toString(panelSituation.getRank()), maxWidth + (OUTER_WIDTH * 5), offset + (OUTER_HEIGHT * 18));
  //
  //        backBuffer.setDrawMode(Graphics.MODE_SCREEN);
  //        backBuffer.setColor(color.getColor());
  //        backBuffer.drawString(Integer.toString(panelSituation.getRank()), maxWidth + (OUTER_WIDTH * 5), offset + (OUTER_HEIGHT * 18));
  //
  //        backBuffer.setDrawMode(Graphics.MODE_NORMAL);
  //    }

  //    private void renderWall(@SuppressWarnings("unused") final long tick, final int panelDelta) {
  //        final SpriteSheet sheet = PanelSet.fromPalette(color).sheet;
  //
  //        final int offset = (playerIDRendererHeight + (garbageStackRendererHeight + ((heightInBlocks - panelSituation.getWallOffset()) * MasterSet.HEIGHT))) - panelDelta;
  //        final int maxWidth = panelWidthInPixels;
  //
  //        sheet.startUse();
  //        for (int y = offset + PanelSet.HEIGHT, j = 0; y < totalHeightInPixels; y += PanelSet.HEIGHT, j = (j + 1) % wallPattern.length) {
  //            // Left border
  //            sheet.renderInUse(OUTER_WIDTH, y, PanelIndex.INNER_LEFT.index.x, PanelIndex.INNER_LEFT.index.y);
  //
  //            // Right border
  //            sheet.renderInUse(maxWidth, y, PanelIndex.INNER_RIGHT.index.x, PanelIndex.INNER_RIGHT.index.y);
  //
  //            // Wall pattern
  //            for (int x = 2 * OUTER_WIDTH, i = 0; x < maxWidth; x += OUTER_WIDTH, i = (i + 1) % wallPattern[0].length) {
  //                sheet.renderInUse(x, y, wallPattern[j][i].index.x, wallPattern[j][i].index.y);
  //
  //            }
  //        }
  //        for (int x = 2 * OUTER_WIDTH; x < maxWidth; x += OUTER_WIDTH) {
  //            // Top border
  //            sheet.renderInUse(x, offset, PanelIndex.INNER_UP.index.x, PanelIndex.INNER_UP.index.y);
  //        }
  //
  //        // Top left border
  //        sheet.renderInUse(OUTER_WIDTH, offset, PanelIndex.INNER_UPLEFT.index.x, PanelIndex.INNER_UPLEFT.index.y);
  //
  //        // Top right border
  //        sheet.renderInUse(maxWidth, offset, PanelIndex.INNER_UPRIGHT.index.x, PanelIndex.INNER_UPRIGHT.index.y);
  //        sheet.endUse();
  //
  //    }

  //    private void renderCombos(@SuppressWarnings("unused") final long tick) {
  //        for (final ComboSituation comboSituation : panelSituation.getComboSituations()) {
  //            if (comboSituation.getOrigin().y >= (heightInBlocks - 1)) {
  //                continue;
  //            }
  //            final BlockSituation comboBlockSituation = panelSituation.getBlockSituations()[comboSituation.getOrigin().x][comboSituation.getOrigin().y];
  //            if (comboBlockSituation == null) {
  //                continue;
  //            }
  //            if (comboBlockSituation.getState() != BlockState.BLINKING) {
  //                continue;
  //            }
  //            if (comboSituation.getSkillChainLevel() < 2) {
  //                if (comboSituation.getHeight() < 4) {
  //                    continue;
  //                }
  //            }
  //
  //            final int comboBlinkingTime = FuriousBlocksCoreDefaults.BLOCK_BLINKINGTIME;
  //            final float delta = ((float) MasterSet.HEIGHT / (float) comboBlinkingTime / 2) * (comboBlinkingTime - comboBlockSituation.getStateTick());
  //
  //            final int x = OUTER_WIDTH + (MasterSet.WIDTH * comboSituation.getOrigin().x);
  //            final int y = (playerIDRendererHeight + (garbageStackRendererHeight + (MasterSet.HEIGHT * (heightInBlocks - comboSituation.getOrigin().y - 1)))) - ((panelSituation.getScrollingOffset() * MasterSet.HEIGHT) / FuriousBlocksCoreDefaults.BLOCK_LOGICALHEIGHT) - (int) delta;
  //
  //            backBuffer.setColor(Color.yellow);
  //            backBuffer.setFont(Fonts.BANDLESS_18_ITALIC.coop32);
  //            if (comboSituation.getSkillChainLevel() > 1) {
  //                MasterSet.masterSheet.startUse();
  //                if (comboSituation.getHeight() > 3) {
  //                    MasterSet.masterSheet.renderInUse(x, y, MasterSet.COMBO_SKILLCHAINLEVEL.index.x, MasterSet.COMBO_SKILLCHAINLEVEL.index.y);
  //                    MasterSet.masterSheet.renderInUse(x, y + MasterSet.HEIGHT, MasterSet.COMBO_SIZE.index.x, MasterSet.COMBO_SIZE.index.y);
  //                    MasterSet.masterSheet.endUse();
  //                    backBuffer.drawString("x" + comboSituation.getSkillChainLevel(), x + (MasterSet.WIDTH / 5), y + ((MasterSet.HEIGHT * 2) / 7));
  //                    backBuffer.drawString(String.valueOf(comboSituation.getHeight()), x + (MasterSet.WIDTH / 4), y + MasterSet.HEIGHT + ((MasterSet.HEIGHT * 2) / 7));
  //                } else {
  //                    MasterSet.masterSheet.renderInUse(x, y + MasterSet.HEIGHT, MasterSet.COMBO_SKILLCHAINLEVEL.index.x, MasterSet.COMBO_SKILLCHAINLEVEL.index.y);
  //                    MasterSet.masterSheet.endUse();
  //                    backBuffer.drawString("x" + comboSituation.getSkillChainLevel(), x + (MasterSet.WIDTH / 5), y + MasterSet.HEIGHT + ((MasterSet.HEIGHT * 2) / 7));
  //                }
  //            } else if (comboSituation.getHeight() > 3) {
  //                MasterSet.masterSheet.startUse();
  //                MasterSet.masterSheet.renderInUse(x, y + MasterSet.HEIGHT, MasterSet.COMBO_SIZE.index.x, MasterSet.COMBO_SIZE.index.y);
  //                MasterSet.masterSheet.endUse();
  //                backBuffer.drawString(String.valueOf(comboSituation.getHeight()), x + (MasterSet.WIDTH / 4), y + MasterSet.HEIGHT + ((MasterSet.HEIGHT * 2) / 7));
  //            }
  //        }
  //    }


  //    private void renderFrozen() {
  //        final int maxWidth = OUTER_WIDTH + panelWidthInPixels;
  //        final int offset = playerIDRendererHeight + garbageStackRendererHeight;
  //
  //        backBuffer.drawImage( //
  //                ImageSet.FROZEN.getImage(), //
  //                (maxWidth + (OUTER_WIDTH * 2)) - 5, (offset + OUTER_HEIGHT * 6) - 4); //
  //
  //        backBuffer.setFont(Fonts.BUBBLEGUM_24_ITALIC.coop32);
  //        backBuffer.setColor(new Color(0xCC, 255, 255));
  //        backBuffer.drawString(String.format("%02d''%02d", panelSituation.getFreezingTime() / FuriousBlocksCoreDefaults.CORE_FREQUENCY, panelSituation.getFreezingTime() % FuriousBlocksCoreDefaults.CORE_FREQUENCY), (maxWidth + (OUTER_WIDTH * 3) + 5), (offset + OUTER_HEIGHT * 7) + 4);
  //    }


  //    private void renderGarbageSizes(@SuppressWarnings("unused") final long tick, final int panelDelta) {
  //        for (int j = panelSituation.getWallOffset(), k = 0; j < (heightInBlocks + 1); j++, k++) {
  //            for (int i = 0; i < widthInBlocks; i++) {
  //                final BlockSituation current = panelSituation.getBlockSituations()[i][k];
  //
  //                if (current == null) {
  //                    continue;
  //                }
  //
  //                if (current.getType() == BlockType.INVISIBLE) {
  //                    continue;
  //                }
  //
  //                final int x = OUTER_WIDTH + (MasterSet.WIDTH * i);
  //                final int y = (playerIDRendererHeight + (garbageStackRendererHeight + (MasterSet.HEIGHT * (heightInBlocks - j)))) - ((panelSituation.getScrollingOffset() * MasterSet.HEIGHT) / FuriousBlocksCoreDefaults.BLOCK_LOGICALHEIGHT) - panelDelta;
  //
  //                if ((current.getType() == BlockType.GARBAGE) && (current.getGarbageBlockType() == GarbageBlockType.DOWNLEFT) && ((k + current.getGarbageHeight()) > (heightInBlocks + 1))) {
  //                    Fonts.BANDLESS_64_ITALIC.coop32.drawString(x + (MasterSet.WIDTH / 5), y - ((MasterSet.HEIGHT * 4) / 5), "x" + current.getGarbageHeight());
  //                }
  //            }
  //        }
  //    }


  //    public void animationDebug(final int x, final int y, final int i, final int k) {
  //        backBuffer.setFont(Fonts.BANDLESS_18_ITALIC.coop32);
  //        backBuffer.setColor(Color.white);
  //        if (blockAnimations[i][k] != null) {
  //            backBuffer.drawString(Integer.toString(blockAnimations[i][k].animation.getFrame()), x, y);
  //            final AnimationFactory type = blockAnimations[i][k].type;
  //            switch (type) {
  //                case CYAN_COMPRESSING:
  //                case GREEN_COMPRESSING:
  //                case PURPLE_COMPRESSING:
  //                case RED_COMPRESSING:
  //                case YELLOW_COMPRESSING:
  //                    backBuffer.drawString("C", x + ((3 * MasterSet.WIDTH) / 5), y + (MasterSet.HEIGHT / 5));
  //                    break;
  //                case BLUE_LANDING:
  //                case GREEN_LANDING:
  //                case PURPLE_LANDING:
  //                case RED_LANDING:
  //                case YELLOW_LANDING:
  //                    backBuffer.drawString("L", x + ((3 * MasterSet.WIDTH) / 5), y + (MasterSet.HEIGHT / 5));
  //                    break;
  //                case CYAN_PANICKING:
  //                case GREEN_PANICKING:
  //                case PURPLE_PANICKING:
  //                case RED_PANICKING:
  //                case YELLOW_PANICKING:
  //                    backBuffer.drawString("P", x + ((3 * MasterSet.WIDTH) / 5), y + (MasterSet.HEIGHT / 5));
  //                    break;
  //            }
  //        }
  //    }

  //    private static int getTotalWidthInPixel(final int panelWidth) {
  //        return OUTER_WIDTH + getPanelWidthInPixel(panelWidth) + OUTER_WIDTH + OUTER_WIDTH + 128;
  //    }

  //    private static int getPanelWidthInPixel(final int panelWidth) {
  //        return panelWidth * 32 /*MasterSet.WIDTH*/;
  //    }
  //
  //    private static int getTotalHeightInPixel(final int panelHeight) {
  //        return playerIDRendererHeight + garbageStackRendererHeight + getPanelHeightInPixel(panelHeight) + OUTER_HEIGHT;
  //    }

  //    private static int getPanelHeightInPixel(final int panelHeight) {
  //        return panelHeight * 32 /*MasterSet.HEIGHT*/;
  //    }

  //    private static final PanelIndex wallPattern[][] = new PanelIndex[][]{
  //            {PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.DARKBRICK_LEFT, PanelIndex.DARKBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT}, //
  //            {PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.DARKBRICK_LEFT, PanelIndex.DARKBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT}, //
  //            {PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT}, //
  //            {PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.DARKBRICK_LEFT, PanelIndex.DARKBRICK_RIGHT}, //
  //            {PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.DARKBRICK_LEFT, PanelIndex.DARKBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT}, //
  //            {PanelIndex.DARKBRICK_LEFT, PanelIndex.DARKBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT}, //
  //            {PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT}, //
  //            {PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.DARKBRICK_LEFT, PanelIndex.DARKBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT}, //
  //            {PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.DARKBRICK_LEFT, PanelIndex.DARKBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT}, //
  //            {PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT, PanelIndex.LIGHTBRICK_LEFT, PanelIndex.LIGHTBRICK_RIGHT}, //
  //    };

  //  public static enum Palette {
  //    PINK(1, 0.68f, 0.68f, 1), //
  //    ORANGE(1, 0.78f, 0, 1), //
  //    MAGENTA(1, 0, 1, 1), //
  //    CYAN(0, 1, 1, 1), //
  //    BLUE(Color.BLUE), //
  //    RED(Color.RED), //
  //    GREEN(Color.GREEN), //
  //    YELLOW(1, 1, 0, 1);
  //
  //    private final Color color;
  //
  //    private Palette(final float R, final float G, final float B, final float A) {
  //      this(new Color(R, G, B, A));
  //    }
  //
  //    private Palette(final Color color) {
  //      this.color = color;
  //    }
  //
  //    public final Color getColor() {
  //      return color;
  //    }
  //  }
}
