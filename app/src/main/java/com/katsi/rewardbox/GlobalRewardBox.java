package com.katsi.rewardbox;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.hypixel.hytale.logger.HytaleLogger;

/**
 * This class helps organize the rewardboxes containers.
 * Serves 2 main purposes:
 * A) is_enabled global config. this allows the RewardBoxes module to be
 * completely disabled externally.
 * B) reward_boxes, this allows for global access to all stored rewardboxes, this
 * makes it much easier to manipulate rewardbox data.
 */
public class GlobalRewardBox {
    static GlobalRewardBox instance;

    public boolean is_enabled;
    private ArrayList<RewardBox> reward_boxes;

    public GlobalRewardBox() {
        this(true);
    }

    public GlobalRewardBox(boolean is_enabled) {
        this.is_enabled = is_enabled;
        reward_boxes = new ArrayList<RewardBox>();
        instance = this;
    }

    public static GlobalRewardBox get() {
        return GlobalRewardBox.instance;
    }

    public ArrayList<RewardBox> getRewardBoxes() {
        return this.reward_boxes;
    }

    /**
     * Finds the requested box.
     * 
     * @param box_name
     * @return RewardBox or null if it couldnt be found.
     */
    public static RewardBox getRewardBox(String box_name) {
        for (RewardBox reward_box : instance.getRewardBoxes()) {
            if (reward_box.box_name.equals(box_name)) {
                return reward_box;
            }
        }
        return null;
    }

    public static RewardBox addRewardBox(RewardBox new_box) {
        for (RewardBox rewardBox : instance.getRewardBoxes()) {
            if (rewardBox.box_name.equals(new_box.box_name)) {
                System.err.println(String.format("[RewardBoxMod] RewardBox already existed '%s'", new_box.box_name));
                return rewardBox;
            }
        }
        instance.getRewardBoxes().add(new_box);
        return new_box;
    }

    @SuppressWarnings("null")
    public static boolean save() {
        try {
            File happy_boxes_config_file = new File("./config/KatsiLib/RewardBoxes.json");
            FileWriter json_writer = new FileWriter(happy_boxes_config_file);
            json_writer.write(new Gson().toJson(GlobalRewardBox.get()));
            json_writer.close();
            HytaleLogger.getLogger().at(Level.INFO).log("[RewardBoxMod] Successfully saved RewardBoxes.json config");
            return true;
        } catch (IOException e) {
            HytaleLogger.getLogger().getSubLogger("RewardBoxMod").at(Level.SEVERE).log(e.toString());
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("GlobalRewardBox: { enabled: %s, Boxes: %s }", this.is_enabled, this.reward_boxes);
    }

}
