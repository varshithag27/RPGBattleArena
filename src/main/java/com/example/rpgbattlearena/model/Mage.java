package com.example.rpgbattlearena.model;

public class Mage extends PlayerCharacter {
    public Mage(String name) {
        super(name, 80, 18);
    }

    @Override
    public int attack() {
        int baseDamage = attackPower;
        int spellBonus = (int) (Math.random() * 10);
        return baseDamage + spellBonus;
    }
}
