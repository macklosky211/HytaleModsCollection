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

public class RemoveBox extends AbstractCommand {

    private final RequiredArg<String> reward_box_name_arg;

    @SuppressWarnings("null")
    protected RemoveBox() {
        super("remove_box", "Deletes an entire RewardBox and its content.");
        
        addAliases("rem_box", "rb", "del_box");

        requirePermission("rewardboxes.admin");

        reward_box_name_arg = withRequiredArg("RewardBox name", "The name of the box you want to delete.", ArgTypes.STRING);

    }

    @SuppressWarnings("null")
    @Override
    @Nullable
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        String reward_box_name = reward_box_name_arg.get(context);

        GlobalRewardBox global_boxes = GlobalRewardBox.get();

        int current_size = global_boxes.getRewardBoxes().size();

        global_boxes.getRewardBoxes().removeIf(reward_box -> reward_box.box_name.equals(reward_box_name));

        if (current_size > global_boxes.getRewardBoxes().size()) {
            context.sender().sendMessage(Message.raw(String.format("Successfully removed RewardBoxes with name '%s'", reward_box_name)));
        } else {
            context.sender().sendMessage(Message.raw(String.format("Failed to remove any RewardBoxes with name '%s'", reward_box_name)));
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Nullable
    protected String generatePermissionNode() {
        return "rewardboxes.admin";
    }
    
}
