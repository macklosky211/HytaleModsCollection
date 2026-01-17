package com.katsi.rewardbox.commands;

import java.util.ArrayList;
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

/**
 * "/RewardBox Create"
 * Creates a new rewardbox.
 * This must be performed before you can add items to said rewardbox.
 */
public class CreateBox extends AbstractCommand {

    private final RequiredArg<String> reward_box_name_arg;

    @SuppressWarnings("null")
    protected CreateBox(){
        super("create", "creation command to create a new rewardbox");
        requirePermission("rewardboxes.admin");

        reward_box_name_arg = withRequiredArg("box name", "new RewardBox name", ArgTypes.STRING);
    }
    
    @SuppressWarnings({ "null" })
    @Override
    @Nullable
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {

        String reward_box_name = reward_box_name_arg.get(context);

        GlobalRewardBox global_rewardbox = GlobalRewardBox.get();
        if (!global_rewardbox.is_enabled){ 
            context.sender().sendMessage(Message.raw("RewardBoxes are disabled by server config."));
            return CompletableFuture.completedFuture(null); 
        }

        ArrayList<RewardBox> global_happy_boxes = global_rewardbox.getRewardBoxes();

        String display_name = context.sender().getDisplayName();
        
        for (RewardBox reward_box : global_happy_boxes){
            if (reward_box.box_name.equals(reward_box_name)) {
                context.sender().sendMessage(Message.raw(String.format("A RewardBox with name '%s' alreay exists.", reward_box_name)));
                return CompletableFuture.completedFuture(null);
            }
        }

        RewardBox new_reward_box = new RewardBox(display_name, reward_box_name);
        GlobalRewardBox.addRewardBox(new_reward_box);

        GlobalRewardBox.save();

        context.sender().sendMessage(Message.raw(String.format("Created RewardBox: '%s'. Note - The box is empty upon creation, add items before use.", reward_box_name)));
        
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Nullable
    protected String generatePermissionNode() {
        return "rewardboxes.admin";
    }
}