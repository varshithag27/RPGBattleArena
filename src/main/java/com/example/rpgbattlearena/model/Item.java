package com.example.rpgbattlearena.model;

public class Item {
    private final String name;
    private final ItemType type;
    private final int value;

    public Item(String name, ItemType type, int value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public ItemType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }
}
