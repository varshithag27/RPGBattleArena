package com.example.rpgbattlearena.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerSession {
    private final PlayerCharacter player;
    private Enemy currentEnemy;
    private int waveNumber;
    private final List<Item> inventory;
    private StatusEffect effect;

    public PlayerSession(PlayerCharacter player, Enemy initialEnemy) {
        this.player = player;
        this.currentEnemy = initialEnemy;
        this.waveNumber = 1;
        this.inventory = new ArrayList<>();
        this.effect = StatusEffect.HEALED;
    }

    public PlayerCharacter getPlayer() {
        return player;
    }

    public Enemy getCurrentEnemy() {
        return currentEnemy;
    }

    public void setCurrentEnemy(Enemy currentEnemy) {
        this.currentEnemy = currentEnemy;
    }

    public int getWaveNumber() {
        return waveNumber;
    }

    public void incrementWave() {
        waveNumber++;
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public boolean removeItem(String itemName) {
        Optional<Item> item = inventory.stream()
            .filter(i -> i.getName().equalsIgnoreCase(itemName))
            .findFirst();
        item.ifPresent(inventory::remove);
        return item.isPresent();
    }

    public StatusEffect getEffect() {
        return effect;
    }

    public void setEffect(StatusEffect effect) {
        this.effect = effect;
    }
}
