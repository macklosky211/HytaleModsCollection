package com.katsi.rewardbox.commands;

import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.katsi.rewardbox.GlobalRewardBox;
import com.katsi.rewardbox.RewardBox;

public class ListBoxes extends AbstractCommand {

    final OptionalArg<String> optional_box_name_arg;

    @SuppressWarnings("null")
    protected ListBoxes() {
        super("list_boxes", "Lists all RewardBoxes by creator and name.");

        addAliases("list");

        optional_box_name_arg = withOptionalArg("rewardbox", "The specific RewardBox", ArgTypes.STRING);
    }

    @SuppressWarnings("null")
    @Override
    @Nullable
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        String optional_box_name = optional_box_name_arg.get(context);

        GlobalRewardBox global_reward_box = GlobalRewardBox.get();

        if (optional_box_name != null && !optional_box_name.isBlank()) {
            for(RewardBox reward_box : global_reward_box.getRewardBoxes()) {
                if (reward_box.box_name.equals(optional_box_name)) {
                    context.sender().sendMessage(Message.raw(String.format("Box '%s' : %s", optional_box_name, reward_box)));
                    return CompletableFuture.completedFuture(null);
                }
            }
            context.sender().sendMessage(Message.raw(String.format("[Error] Could not find box with name '%s'", optional_box_name)));
        }

        StringBuilder sb = new StringBuilder();
        sb.append("RewardBoxes : {\n");
        for(RewardBox reward_box : global_reward_box.getRewardBoxes()) {
            sb.append(String.format("\t%s : %s x%d\n", reward_box.box_creator, reward_box.box_name, reward_box.getItems().size()));
        }
        sb.append("\t}");
        
        context.sender().sendMessage(Message.raw(sb.toString()));
        return CompletableFuture.completedFuture(null);
    }

    /**
     * This should be callable by anyone.
     */
    @Override
    protected boolean canGeneratePermission() {
        return false;
    }
    
}
