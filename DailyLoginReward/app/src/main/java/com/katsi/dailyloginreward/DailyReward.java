package com.katsi.dailyloginreward;

import java.util.HashMap;
import java.util.Map;

public class DailyReward {
    // Key = "Item_ID", Value = Quantity
    Map<String, Double> items;
    // Key = "RewardBoxName", Value = Quantity of rolls to grant.
    Map<String, Double> reward_box_keys;

    public DailyReward(Map<String, Double> items) {
        this.items = items;
        this.reward_box_keys = new HashMap<String, Double>(0);
    }

    public DailyReward(Map<String, Double> items, Map<String, Double> reward_box_keys) {
        this.items = items;
        this.reward_box_keys = reward_box_keys;
    }

    public Map<String, Double> getItems() {
        return this.items;
    }

    public Map<String, Double> getRewardBoxKeys() {
        return this.reward_box_keys;
    }

    public void setRewardBoxKeys(Map<String, Double> reward_box_keys) {
        this.reward_box_keys = reward_box_keys;
    }

    public String toString() {
        if (this.items == null)
            this.items = new HashMap<String, Double>(0);
        return String.format("DailyReward : { Items : '%s', RewardBoxKeys : '%s' ", this.items, this.reward_box_keys);
    }
}