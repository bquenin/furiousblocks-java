package me.pixodro.furiousblocks.core;

import me.pixodro.furiousblocks.core.panel.PanelEvent;

/**
 * Created with IntelliJ IDEA.
 * User: tsug
 * Date: 5/10/12
 * Time: 11:36 PM
 * To change this template use File | Settings | File Templates.
 */
public interface FuriousBlocksCoreListener {
  void onEvent(long playerId, PanelEvent panelEvent);
}
