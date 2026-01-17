package com.katsi.rewardbox.events;

import java.util.UUID;
import java.util.logging.Level;

import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.katsi.rewardbox.BoxRollManager;
import com.katsi.rewardbox.GlobalRewardBox;

/**
 * This event provides the user with a 'key' to give them the ability to roll a specific RewardBox.
 */
public class GrantRollEvent implements IEvent<Void> {
    private final PlayerRef sender;
    private final String reward_box_id;
    private final int quantity;

    public GrantRollEvent(PlayerRef sender, String reward_box_id) {
        this.sender = sender;
        this.reward_box_id = reward_box_id;
        this.quantity = 1;
    }

    public GrantRollEvent(PlayerRef sender, String reward_box_id, int quantity) {
        this.sender = sender;
        this.reward_box_id = reward_box_id;
        this.quantity = quantity;
    }

    public PlayerRef getSender() {
        return this.sender;
    }

    public String getRewardBoxID() {
        return this.reward_box_id;
    }

    public int getQuantity() {
        return this.quantity;
    }

    @SuppressWarnings("null")
    public static void event(GrantRollEvent event) {

        if (GlobalRewardBox.getRewardBox(event.getRewardBoxID()) == null) {
            HytaleLogger.getLogger().at(Level.WARNING).log("Failed to find RewardBox with name: ", event.getRewardBoxID());
            return;
        }

        BoxRollManager brm = BoxRollManager.get();
        UUID uuid = event.getSender().getUuid();
        if (event.getQuantity() == 1) {
            brm.incrementRolls(uuid, event.getRewardBoxID());
        } else {
            
            if (brm.hasRoll(uuid, event.getRewardBoxID())) {
                brm.setRolls(uuid, event.getRewardBoxID(), event.getQuantity());

            } else {
                brm.incrementRolls(uuid, event.getRewardBoxID());
            }
        }

        BoxRollManager.save();

        HytaleLogger.getLogger().at(Level.INFO).log("Granted roll event triggered: ", event.getSender().getUsername(), event.getRewardBoxID(), event.getQuantity() );
    }
}
