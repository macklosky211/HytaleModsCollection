package com.katsi.rewardbox.commands;

import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.katsi.rewardbox.GlobalRewardBox;
import com.katsi.rewardbox.RewardBox;

public class RemoveItem extends AbstractCommand {

    private final RequiredArg<String> item_box_arg;
    private final RequiredArg<String> item_id_arg;

    @SuppressWarnings("null")
    protected RemoveItem() {
        super("remove_item", "removes and item from the specified RewardBox");

        addAliases("remove", "rem", "r");
        requirePermission("rewardboxes.admin");
        
        item_box_arg = withRequiredArg("RewardBox Name", "The name of the RewardBox you are trying to remove an item from.", ArgTypes.STRING);
        item_id_arg = withRequiredArg("ItemID", "The id of the item you are attempting to remove.", ArgTypes.STRING);
        
    }

    @SuppressWarnings("null")
    @Override
    @Nullable
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {

        String item_box_name = item_box_arg.get(context);
        String item_id = item_id_arg.get(context);

        RewardBox reward_box = GlobalRewardBox.getRewardBox(item_box_name);

        int removed_items = reward_box.removeItem(item_id);

        if (removed_items > 0) {
            context.sender().sendMessage(Message.raw(String.format("Successfully removed x%d items '%s' from '%s'", removed_items, item_id, item_box_name)));
            reward_box.recalculateTotalChance();
        }
        else
            context.sender().sendMessage(Message.raw(String.format("Failed to find an item to remove with id '%s' in box '%s'", item_id, item_box_name)));

        GlobalRewardBox.save();

        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Nullable
    protected String generatePermissionNode() {
        return "rewardboxes.admin";
    }
    
}
