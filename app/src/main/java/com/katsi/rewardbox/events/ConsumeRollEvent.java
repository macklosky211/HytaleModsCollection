package com.katsi.rewardbox.events;

import java.util.UUID;
import java.util.logging.Level;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.katsi.rewardbox.BoxRollManager;
import com.katsi.rewardbox.GlobalRewardBox;
import com.katsi.rewardbox.RewardBox;
import com.katsi.rewardbox.RewardBoxItem;

/**
 * This event consumes 'rolls' for a specific RewardBox, specifying value allows for multiple rolls.
 */
public class ConsumeRollEvent implements IEvent<Void> {
    private final PlayerRef sender;
    private final String reward_box_id;
    private final int quantity;

    public ConsumeRollEvent(PlayerRef sender, String reward_box_id) {
        this.sender = sender;
        this.reward_box_id = reward_box_id;
        this.quantity = 1;
    }

    public ConsumeRollEvent(PlayerRef sender, String reward_box_id, int quantity) {
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
    public static void event(ConsumeRollEvent event) {
        BoxRollManager brm = BoxRollManager.get();
        String reward_box_id = event.getRewardBoxID();
        
        PlayerRef target_playerRef = Universe.get().getPlayerByUsername(event.getSender().getUsername(), NameMatching.DEFAULT);
        if (target_playerRef == null || !target_playerRef.isValid()) {
            event.getSender().sendMessage(Message.raw(String.format("[ERROR] Could not find player '%s'", event.getSender().getUsername())));
            return;
        }
        String uuid_string = target_playerRef.getUuid().toString();

        RewardBox reward_box = GlobalRewardBox.getRewardBox(reward_box_id);
        if ( reward_box == null) {
            target_playerRef.sendMessage(Message.raw(String.format("Failed to find RewardBox with name '%s'", reward_box_id)));
            return;
        }

        if (!brm.hasRoll(uuid_string, reward_box_id)) {
            target_playerRef.sendMessage(Message.raw(String.format("Failed to consume roll due to not having any keys for RewardBox '%s'", reward_box_id)));
            return;
        }
        
        Store<EntityStore> store_holder = Universe.get().getWorld(target_playerRef.getWorldUuid()).getEntityStore().getStore();
        Ref<EntityStore> storeRef = target_playerRef.getReference();
        if (store_holder == null || storeRef == null || !storeRef.isValid()) {
            target_playerRef.sendMessage(Message.raw(String.format("[ERROR] Could not find store_holder entity.")));
            return;
        }

        Player player = store_holder.getComponent(storeRef, Player.getComponentType());
        TransformComponent transform = store_holder.getComponent(storeRef, TransformComponent.getComponentType());
        
        int total_rolls = event.getQuantity();
        if (event.getQuantity() > 1) {
            int remaining_rolls = brm.getRolls(uuid_string, reward_box_id) - event.getQuantity();
            if (remaining_rolls < 0) {
                remaining_rolls = -remaining_rolls;
                total_rolls = event.getQuantity() - remaining_rolls;
                target_playerRef.sendMessage(Message.raw(String.format("Reqested x%d rolls, but only had x%d keys.", event.getQuantity(), total_rolls)));
            }
        }

        brm.decrementRolls(uuid_string, reward_box_id, total_rolls);

        CombinedItemContainer inventory = player.getInventory().getCombinedEverything();

        Store<EntityStore> store = target_playerRef.getReference().getStore();

        for(int i = 0; i < total_rolls; i++) {
            RewardBoxItem random_item = reward_box.getRandomItem();
            if (random_item == null) {
                target_playerRef.sendMessage(Message.raw(String.format("Failed to get an item from the box. Was it empty?")));
                return;
            }

            String item_display_name = String.format("%s", random_item.getItemID().replaceAll("_", " "));

            ItemStack remaining_items = inventory
                .addItemStack(new ItemStack(random_item.getItemID(), random_item.getQuantity()))
                .getRemainder();
        
            if (remaining_items != null && !remaining_items.isEmpty()) {
                Holder<EntityStore> holder = ItemComponent.generateItemDrop(store, remaining_items, transform.getPosition(), transform.getRotation(), 0f, 0f, 0f);
                if (holder != null) {
                    ItemComponent item_component = holder.getComponent(ItemComponent.getComponentType());
                    if(item_component != null) {
                        item_component.setPickupDelay(1.5f);
                    }
                    
                    

                    holder.addComponent(Nameplate.getComponentType(), new Nameplate(item_display_name));

                    store.addEntity(holder, AddReason.SPAWN);
                }
            }

            target_playerRef.sendMessage(Message.raw(String.format(" - Recieved: '%s' x%d", item_display_name, random_item.getQuantity())));

            HytaleLogger.getLogger().at(Level.INFO).log(String.format("%s pulled item '%s'x%d from box '%s'", target_playerRef.getUsername(), random_item.getItemID(), event.getQuantity(), event.getRewardBoxID()));
        }

        BoxRollManager.save();

    }
}
