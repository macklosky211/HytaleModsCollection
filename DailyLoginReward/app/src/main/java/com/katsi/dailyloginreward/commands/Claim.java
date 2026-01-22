package com.katsi.dailyloginreward.commands;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.katsi.dailyloginreward.DailyReward;
import com.katsi.dailyloginreward.LoginStore;
import com.katsi.dailyloginreward.Main;
import com.katsi.lib.config.ConfigManager;
import com.katsi.rewardbox.events.GrantRollEvent;

public class Claim extends AbstractPlayerCommand {
    public Claim() {
        super("claim", "Attempts to claim daily reward");
        addAliases("c");
        
    }

    @SuppressWarnings("null")
    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> storeRef, @Nonnull PlayerRef playerRef, @Nonnull World world) {

        UUID uuid = context.sender().getUuid();

        if (!LoginStore.hasRewardAvailable(uuid)) {
            context.sender().sendMessage(Message.raw(String.format("You have already claimed your daily reward for today. Try again tommorow.")));
            return;
        }

        // This returns null when the user has already claimed their rewards.
        DailyReward daily_reward = LoginStore.getDailyReward(uuid);
        if((daily_reward == null)) {
            context.sender().sendMessage(Message.raw("You have already claimed your daily reward for today. Try again tommorow."));
            return;
        }

        Player player = store.getComponent(storeRef, Player.getComponentType());
        CombinedItemContainer player_inventory = player.getInventory().getCombinedEverything();

        // Get Item Rewards.
        Map<String, Double> item_reward_map = daily_reward.getItems();
        if (item_reward_map != null && !item_reward_map.isEmpty()) {
            ArrayList<ItemStack> overflow_items = new ArrayList<ItemStack>(); 
            
            // Add reward items to backpack when they fit.
            for (String itemID : item_reward_map.keySet()) {
                Double quantity = item_reward_map.get(itemID);
    
                ItemStack new_item = new ItemStack(itemID, quantity.intValue());
    
                ItemStack remainder = player_inventory.addItemStack(new_item).getRemainder();
                if (remainder != null && !remainder.isEmpty()) {
                    overflow_items.add(remainder);
                }
            }
    
            TransformComponent transform = store.getComponent(storeRef, TransformComponent.getComponentType());
    
            // If theres any items that dont fit then spawn them on the ground at the feet of the player.
            for (ItemStack item_stack : overflow_items) {
                Holder<EntityStore> item = ItemComponent.generateItemDrop(store, item_stack, transform.getPosition(), transform.getRotation(), 0f, 0f, 0f);
                if (item == null) {
                    continue;
                }
    
                ItemComponent item_component = item.getComponent(ItemComponent.getComponentType());
                if (item_component != null) {
                    item_component.setPickupDelay(1.5f);
                }
    
                store.addEntity(item, AddReason.SPAWN);
            }
        }


        // Get RewardBox Rewards.
        Map<String, Double> reward_box_keys = daily_reward.getRewardBoxKeys();
        if (reward_box_keys != null && !reward_box_keys.isEmpty()) {
            for (String reward_box : reward_box_keys.keySet()) {
                int num_keys_to_give = reward_box_keys.get(reward_box).intValue();
    
                IEventDispatcher<GrantRollEvent, GrantRollEvent> dispatcher = HytaleServer.get().getEventBus().dispatchFor(GrantRollEvent.class);
                if (dispatcher.hasListener()) {
                    GrantRollEvent event = new GrantRollEvent(playerRef, reward_box, num_keys_to_give);
                    dispatcher.dispatch(event);
                }
            }
        }

        ConfigManager.saveConfig(Main.get().CONFIG_PATH, LoginStore.get(), LoginStore.class);

    }

    /**
     * This should be callable by anyone.
     */
    @Override
    protected boolean canGeneratePermission() {
        return false;
    }
    
    @Override
    protected String generatePermissionNode() {
        return "dailyloginreward.admin";
    }
}
