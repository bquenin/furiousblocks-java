/**
 *
 */
package me.pixodro.furiousblocks.core.panel;

/**
 * @author tsug
 */
public interface PanelListener {
  public void onCombo(Combo combo);

  void onEvent(long playerId, PanelEvent panelEvent);
}
