package com.example.rpgbattlearena.model;

import java.util.List;

/** Step-by-step battle narration for the UI to animate each moment. */
public class BattleDetails {
    private final List<String> lines;
    private final int playerDamageDealt;
    private final int enemyDamageDealt;
    private final boolean criticalHit;
    private final boolean enemyDefeated;
    private final boolean playerDefeated;
    private final boolean playerDodged;

    public BattleDetails(
        List<String> lines,
        int playerDamageDealt,
        int enemyDamageDealt,
        boolean criticalHit,
        boolean enemyDefeated,
        boolean playerDefeated,
        boolean playerDodged
    ) {
        this.lines = lines;
        this.playerDamageDealt = playerDamageDealt;
        this.enemyDamageDealt = enemyDamageDealt;
        this.criticalHit = criticalHit;
        this.enemyDefeated = enemyDefeated;
        this.playerDefeated = playerDefeated;
        this.playerDodged = playerDodged;
    }

    public List<String> getLines() {
        return lines;
    }

    public int getPlayerDamageDealt() {
        return playerDamageDealt;
    }

    public int getEnemyDamageDealt() {
        return enemyDamageDealt;
    }

    public boolean isCriticalHit() {
        return criticalHit;
    }

    public boolean isEnemyDefeated() {
        return enemyDefeated;
    }

    public boolean isPlayerDefeated() {
        return playerDefeated;
    }

    public boolean isPlayerDodged() {
        return playerDodged;
    }
}
