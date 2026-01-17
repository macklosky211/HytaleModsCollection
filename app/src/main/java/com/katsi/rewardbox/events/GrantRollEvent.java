package com.katsi.rewardbox.events;

import java.util.UUID;
import java.util.logging.Level;

import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
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
            event.getSender().sendMessage(Message.raw(String.format("Attempted to grant key for box '%s' but the targetted box could not be found.", event.getRewardBoxID())));
            return;
        }

        BoxRollManager brm = BoxRollManager.get();
        String uuid_string = event.getSender().getUuid().toString();
        brm.incrementRolls(uuid_string, event.getRewardBoxID(), event.getQuantity());

        event.getSender().sendMessage(Message.raw(String.format("Recieved x%d keys for RewardBox '%s'", event.getQuantity(), event.getRewardBoxID())));

        BoxRollManager.save();

        HytaleLogger.getLogger().at(Level.INFO).log(String.format("%s recieved x%d keys for RewardBox '%s'", event.getSender().getUsername(), event.getQuantity(), event.getRewardBoxID()) );
    }
}
