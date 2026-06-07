package com.example.rpgbattlearena.model;

public class Archer extends PlayerCharacter {
    public Archer(String name) {
        super(name, 100, 14);
    }

    @Override
    public int attack() {
        int baseDamage = attackPower;
        int critChance = (Math.random() < 0.3) ? attackPower / 2 : 0;
        return baseDamage + critChance;
    }
}
