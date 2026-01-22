package com.katsi.rewardbox;

import java.util.ArrayList;
import java.util.Random;

import javax.annotation.Nonnull;

public class RewardBox {
    public String box_creator;
    public String box_name;

    private ArrayList<RewardBoxItem> items;
    private double total_chance;
    private static Random rand;

    @Nonnull
    static ArrayList<RewardBox> global_instance = new ArrayList<>();
    
    public RewardBox(String box_creator, String box_name) {
        this.box_creator = box_creator;
        this.box_name = box_name;
        this.items = new ArrayList<RewardBoxItem>();
        this.recalculateTotalChance();
        global_instance.add(this);
    }

    public ArrayList<RewardBoxItem> getItems() {
        return this.items;
    }

    /**
     * This function returns itself so that it can be daisy-chained.
     * 
     * @param item
     * @return
     */
    public RewardBox addItem(RewardBoxItem item) {
        if (!this.items.contains(item)) {
            this.items.add(item);
            this.total_chance += item.pull_chance;
            rand = new Random(System.currentTimeMillis());
        }
        return this;
    }

    /**
     * Removes item with id form the this itembox.
     * If multiple items with the same item_id appear, then it removes ALL instances of the object.
     * if no items match the item_id then this function returns '0'.
     * @param item_id
     * @return the amount of items that were removed.
     */
    public int removeItem(String item_id) {
        ArrayList<RewardBoxItem> removal_items = new ArrayList<>();
        for (int i = 0; i < this.items.size(); i++) {
            if(this.items.get(i).item_id.equals(item_id)){
                removal_items.add(this.items.get(i));
            }
        }

        this.items.removeAll(removal_items);

        return removal_items.size();
    }

    public RewardBoxItem getRandomItem() {
        if (items.isEmpty())
            return null;

        double random_num = rand.nextDouble(this.total_chance);

        // chaching would help with this next bit.
        double current_chance = 0.0;
        for (RewardBoxItem item : this.items) {
            current_chance += item.pull_chance;
            if (random_num < current_chance) {
                return item;
            }
        }

        return null;
    }

    /**
     * Recalcultes the maximum chance pool for pull-probability calculations
     * Also re-seeds the random generator.
     */
    public void recalculateTotalChance() {
        double chance = 0.0;
        for (RewardBoxItem item : this.items) {
            chance += item.pull_chance;
        }
        this.total_chance = chance;
        rand = new Random(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return String.format("{ Mod Author: %s, Box Name: %s, Items: %s }", this.box_creator, this.box_name, this.items.toString());
    }

}