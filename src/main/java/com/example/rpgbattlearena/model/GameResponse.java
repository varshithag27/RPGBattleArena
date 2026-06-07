package com.example.rpgbattlearena.model;

import java.util.List;

public class GameResponse {
    private final String message;
    private final PlayerCharacter player;
    private final Enemy enemy;
    private final List<Item> inventory;
    private final StatusEffect statusEffect;
    private final int wave;
    private final BattleDetails battle;

    public GameResponse(String message, PlayerCharacter player, Enemy enemy, List<Item> inventory,
                        StatusEffect statusEffect, int wave) {
        this(message, player, enemy, inventory, statusEffect, wave, null);
    }

    public GameResponse(String message, PlayerCharacter player, Enemy enemy, List<Item> inventory,
                        StatusEffect statusEffect, int wave, BattleDetails battle) {
        this.message = message;
        this.player = player;
        this.enemy = enemy;
        this.inventory = inventory;
        this.statusEffect = statusEffect;
        this.wave = wave;
        this.battle = battle;
    }

    public String getMessage() {
        return message;
    }

    public PlayerCharacter getPlayer() {
        return player;
    }

    public Enemy getEnemy() {
        return enemy;
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public StatusEffect getStatusEffect() {
        return statusEffect;
    }

    public int getWave() {
        return wave;
    }

    public BattleDetails getBattle() {
        return battle;
    }
}
