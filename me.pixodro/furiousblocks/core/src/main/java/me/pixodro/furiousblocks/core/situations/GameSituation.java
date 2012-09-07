package me.pixodro.furiousblocks.core.situations;

import java.util.Collections;
import java.util.Map;

public class GameSituation {
  private final Map<Integer, PanelSituation> playerIdToPanelSituation;

  public GameSituation(final Map<Integer, PanelSituation> panelSituations) {
    playerIdToPanelSituation = panelSituations;
  }

  /**
   * @return the panelSituations
   */
  public final Map<Integer, PanelSituation> getPlayerIdToPanelSituation() {
    return Collections.unmodifiableMap(playerIdToPanelSituation);
  }
}
