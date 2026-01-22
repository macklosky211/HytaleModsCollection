package com.katsi.rewardbox;

public class RewardBoxItem {
    String item_id;
    double pull_chance;
    int quantity;


    public RewardBoxItem(String item_id, double pull_chance) {
        this(item_id, pull_chance, 1);
    }

    public RewardBoxItem(String item_id, double pull_chance, int quantity){
        this.item_id = item_id;
        this.pull_chance = pull_chance;
        this.quantity = quantity;
    }

    public String getItemID() {
        return this.item_id;
    }

    public int getQuantity() {
        return this.quantity;
    }

    @Override
    public String toString() {
        return String.format("{ ItemID: %s, Pull Chance: %f, Quantity %d }", this.item_id, this.pull_chance, this.quantity);
    }
}
