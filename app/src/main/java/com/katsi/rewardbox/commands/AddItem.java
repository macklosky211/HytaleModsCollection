package com.katsi.rewardbox.commands;

import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.katsi.rewardbox.GlobalRewardBox;
import com.katsi.rewardbox.RewardBox;
import com.katsi.rewardbox.RewardBoxItem;

public class AddItem extends AbstractCommand {
    /**
     *
     */
    private final RequiredArg<String> reward_box_name_arg;
    private final RequiredArg<String> item_id_arg;
    private final RequiredArg<Integer> item_quantity_arg;
    private final RequiredArg<Double> item_pull_chance_arg;

    @SuppressWarnings("null")
    protected AddItem() {
        super("add_item", "Adds an item to the specified itembox");
        addAliases("add", "a");
        requirePermission("rewardboxes.admin");

        reward_box_name_arg = withRequiredArg("RewardBox name", "The name of the RewardBox this item belongs in", ArgTypes.STRING);
        item_id_arg = withRequiredArg("ItemID", "The full itemID path for the specified item.", ArgTypes.STRING);
        item_quantity_arg = withRequiredArg("Quantity", "The amount of this item that is given when pulling it.", ArgTypes.INTEGER);
        item_pull_chance_arg = withRequiredArg("Pull Chance", "The chance in {chance}/sum_all_item_chances that the item will be pulled.", ArgTypes.DOUBLE);
    }

    @SuppressWarnings("null")
    @Override
    @Nullable
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {

        GlobalRewardBox global_happy_box = GlobalRewardBox.get();
        if (!global_happy_box.is_enabled){ 
            context.sender().sendMessage(Message.raw("RewardBoxes are disabled by server config."));
            return CompletableFuture.completedFuture(null); 
        }

        String reward_box_name = reward_box_name_arg.get(context);
        String item_id = item_id_arg.get(context);
        Integer item_quantity = item_quantity_arg.get(context);
        double item_pull_chance = item_pull_chance_arg.get(context);

        if (item_pull_chance <= 0.0) {
            context.sender().sendMessage(Message.raw(String.format("(%f <= 0.0) Can not have {ZERO or Negative} weights for the items pull chance.", item_pull_chance)));
            return CompletableFuture.completedFuture(null);
        }

        RewardBox target_reward_box = GlobalRewardBox.getRewardBox(reward_box_name);
        if (target_reward_box == null) {
            context.sender().sendMessage(Message.raw(String.format("Could not find RewardBox with name: '%s'", reward_box_name)));
            return CompletableFuture.completedFuture(null);
        }

        boolean valid_item = Item.getAssetMap().getAssetMap().containsKey(item_id);

        if (!valid_item){
            context.sender().sendMessage(Message.raw(String.format("Invalid itemID: '%s'", item_id)));
            return CompletableFuture.completedFuture(null);
        }

        RewardBoxItem new_item = new RewardBoxItem(item_id, item_pull_chance, item_quantity);
        
        target_reward_box.addItem(new_item);
        
        GlobalRewardBox.save();

        context.sender().sendMessage(Message.raw(String.format("Added item '%s'x%d @%f to RewardBox '%s'", item_id, item_quantity.intValue(), item_pull_chance, reward_box_name)));

        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Nullable
    protected String generatePermissionNode() {
        return "rewardboxes.admin";
    }
}