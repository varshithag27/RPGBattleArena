package com.example.rpgbattlearena.model;

public class Enemy {
    private final String name;
    private int health;
    private final int attackPower;

    public Enemy(String name, int health, int attackPower) {
        this.name = name;
        this.health = health;
        this.attackPower = attackPower;
    }

    public int attack() {
        return attackPower;
    }

    public void takeDamage(int damage) {
        health = Math.max(0, health - damage);
    }

    public boolean isAlive() {
        return health > 0;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getAttackPower() {
        return attackPower;
    }

    @Override
    public String toString() {
        return String.format("%s(health=%d, atk=%d)", name, health, attackPower);
    }
}
