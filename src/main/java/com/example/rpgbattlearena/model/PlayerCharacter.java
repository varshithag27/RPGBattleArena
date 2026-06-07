package com.example.rpgbattlearena.model;

import java.util.Objects;

public abstract class PlayerCharacter {
    protected String name;
    protected int maxHealth;
    protected int health;
    protected int attackPower;
    protected int level;
    protected int experience;

    protected PlayerCharacter(String name, int maxHealth, int attackPower) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.attackPower = attackPower;
        this.level = 1;
        this.experience = 0;
    }

    public abstract int attack();

    public void takeDamage(int damage) {
        health = Math.max(0, health - damage);
    }

    public void heal(int amount) {
        health = Math.min(maxHealth, health + amount);
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void gainExperience(int xp) {
        experience += xp;
        if (experience >= 100) {
            levelUp();
            experience -= 100;
        }
    }

    protected void levelUp() {
        level++;
        maxHealth += 10;
        health = maxHealth;
        attackPower += 3;
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

    public void increaseAttackPower(int amount) {
        attackPower += amount;
    }

    public void increaseMaxHealth(int amount) {
        maxHealth += amount;
        heal(amount);
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    @Override
    public String toString() {
        return String.format("%s(level=%d, health=%d/%d, atk=%d, xp=%d)", name, level, health, maxHealth, attackPower, experience);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, level, experience);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PlayerCharacter other)) return false;
        return Objects.equals(name, other.name) && level == other.level && experience == other.experience;
    }
}
