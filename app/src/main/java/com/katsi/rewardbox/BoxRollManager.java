package com.katsi.rewardbox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.hypixel.hytale.logger.HytaleLogger;

public class BoxRollManager {
    public static BoxRollManager instance;

    HashMap<UUID, HashMap<String, Integer>> roll_access_table;

    protected BoxRollManager() {
        instance = this;
        roll_access_table = new HashMap<UUID, HashMap<String, Integer>>(1);
    }


    public static BoxRollManager get() {
        return instance;
    }

    public static HashMap<UUID, HashMap<String, Integer>> getRollAccessTable() {
        return instance.roll_access_table;
    }

    @SuppressWarnings("null")
    public static boolean save() {
        try {
            File reward_boxes_roll_table = new File("./config/KatsiLib/RewardBoxesRollTable.json");
            FileWriter json_writer = new FileWriter(reward_boxes_roll_table);
            json_writer.write(new Gson().toJson(BoxRollManager.getRollAccessTable()));
            json_writer.close();
            HytaleLogger.getLogger().at(Level.INFO).log("[RewardBoxMod] Successfully saved RewardBoxesRollTable.json");
            return true;
        } catch (IOException e) {
            HytaleLogger.getLogger().getSubLogger("RewardBoxMod").at(Level.SEVERE).log(e.toString());
            return false;
        }
    }


    @SuppressWarnings({ "null", "unchecked" })
    public static boolean load() {
        Gson gson = new Gson();
        String file_content = "";
        File reward_boxes_roll_table = new File("./config/KatsiLib/RewardBoxesRollTable.json");
        try {
            Scanner json_reader = new Scanner(reward_boxes_roll_table);
            file_content = json_reader.useDelimiter("\\Z").next();
            json_reader.close();
        } catch (FileNotFoundException e) {
            HytaleLogger.getLogger().at(Level.SEVERE).log("[RewardBoxMod] Failed to (FIND or READ) {server_root}/config/KatsiLib/RewardBoxes.json");
            return false;
        }
        
        instance.roll_access_table = gson.fromJson(file_content, BoxRollManager.getRollAccessTable().getClass());
        return true;
    }

    public static boolean hasSave() {
        return new File("./config/KatsiLib/RewardBoxesRollTable.json").exists();
    }

    public boolean hasRoll(UUID uuid, String reward_box_name) {
        if (this.roll_access_table.containsKey(uuid)){
            return this.roll_access_table.get(uuid).containsKey(reward_box_name);
        } else {
            return false;
        }
    }

    public boolean hasAnyRolls(UUID uuid) {
        return this.roll_access_table.containsKey(uuid);
    }

    public int getRolls(UUID uuid, String reward_box_name) {
        if (!this.hasRoll(uuid, reward_box_name)) {
            return 0;
        } else {
            return this.roll_access_table.get(uuid).get(reward_box_name);
        }
    }

    public void setRolls(UUID uuid, String reward_box_name, int value) {
        if (value <= 0) {
            if (this.roll_access_table.containsKey(uuid)) {
                HashMap<String, Integer> reward_map = this.roll_access_table.get(uuid);
                if (reward_map.containsKey(reward_box_name)) {
                    reward_map.remove(reward_box_name);
                    if (reward_map.isEmpty()) {
                        this.roll_access_table.remove(uuid);
                    }
                }
            }
            return;
        }

        HashMap<String, Integer> reward_map = this.assertUUIDMap(uuid);
        if (reward_map.containsKey(reward_box_name)) {
            reward_map.replace(reward_box_name, Integer.valueOf(value));
        } else {
            reward_map.put(reward_box_name, Integer.valueOf(value));
        }
        
    }


    /**
     * Increments the number of availible rolls by 1.
     * If the box didnt have any assosiated rolls then its set to 1.
     */
    public void incrementRolls(UUID uuid, String reward_box_name) {
        this.incrementRolls(uuid, reward_box_name, 1);
    }

    public void incrementRolls(UUID uuid, String reward_box_name, int value) {
        HashMap<String, Integer> reward_map = this.assertUUIDMap(uuid);
        if (reward_map.containsKey(reward_box_name)) {
            reward_map.replace(reward_box_name, reward_map.get(reward_box_name) + Integer.valueOf(value));
        } else {
            reward_map.put(reward_box_name, Integer.valueOf(value));
        }
    }

    public void decrementRolls(UUID uuid, String reward_box_name) {
        this.decrementRolls(uuid, reward_box_name, 1);
    }
    
    public void decrementRolls(UUID uuid, String reward_box_name, int value) {
        if (!this.roll_access_table.containsKey(uuid)) return;
        HashMap<String, Integer> reward_map = this.roll_access_table.get(uuid);
        if (!reward_map.containsKey(reward_box_name)) return;

        Integer new_value = reward_map.get(reward_box_name) - Integer.valueOf(value);
        if (new_value <= 0) {
            reward_map.remove(reward_box_name); 
            if (reward_map.isEmpty()) { // if the player has no rolls left remove them to minimize save file space.
                this.roll_access_table.remove(uuid, reward_map); 
            }
        } else {
            reward_map.replace(reward_box_name, new_value);
        }
    }

    

    /**
     * Creates a UUID map for the specified uuid. If it already existed then it returns the existing object.
     */
    protected HashMap<String, Integer> assertUUIDMap(UUID uuid) {
        if (!this.roll_access_table.containsKey(uuid)) {
            HashMap<String, Integer> new_reward_map = new HashMap<String, Integer>(1);

            this.roll_access_table.put(uuid, new_reward_map);
            return new_reward_map;
        } else {
            return this.roll_access_table.get(uuid);
        }
    }
}
