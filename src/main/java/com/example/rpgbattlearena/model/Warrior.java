package com.example.rpgbattlearena.model;

public class Warrior extends PlayerCharacter {
    public Warrior(String name) {
        super(name, 120, 12);
    }

    @Override
    public int attack() {
        int baseDamage = attackPower;
        int bonus = (int) (Math.random() * 6);
        return baseDamage + bonus;
    }
}
