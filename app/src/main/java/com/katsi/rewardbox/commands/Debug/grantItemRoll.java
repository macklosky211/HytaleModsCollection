package com.katsi.rewardbox.commands.Debug;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.katsi.rewardbox.BoxRollManager;
import com.katsi.rewardbox.GlobalRewardBox;
import com.katsi.rewardbox.events.GrantRollEvent;

public class grantItemRoll extends AbstractCommand {

    private final RequiredArg<String> player_name_arg;
    private final RequiredArg<String> reward_box_name_arg;
    private final DefaultArg<Integer> increment_value_arg;

    @SuppressWarnings("null")
    public grantItemRoll() {
        super("grant_item_roll", "grants a user rolls for a RewardBox");

        addAliases("add_roll", "ar");

        requirePermission("rewardboxes.admin");

        player_name_arg = withRequiredArg("Target player name", "The name of the player who's rolls you are granting", ArgTypes.STRING);
        reward_box_name_arg = withRequiredArg("RewardBox name", "The name of the RewardBox you are granting rolls for", ArgTypes.STRING);
        increment_value_arg = withDefaultArg("value", "the value to increase the box by.", ArgTypes.INTEGER, 1, "increments by 1 by default.");
    }

    @SuppressWarnings("null")
    @Override
    protected CompletableFuture<Void> execute(@Nonnull CommandContext context) {
        String player_name = player_name_arg.get(context);
        String reward_box_id = reward_box_name_arg.get(context);
        Integer value = increment_value_arg.get(context);


        PlayerRef target_playerRef = Universe.get().getPlayerByUsername(player_name, NameMatching.DEFAULT);
        if (target_playerRef == null || !target_playerRef.isValid()) {
            context.sender().sendMessage(Message.raw(String.format("Could not find targeted player '%s' (they must be online)", player_name)));
            return CompletableFuture.completedFuture(null);
        }

        IEventDispatcher<GrantRollEvent, GrantRollEvent> dispatcher = HytaleServer.get().getEventBus().dispatchFor(GrantRollEvent.class);
        
        if (dispatcher.hasListener()) {
            GrantRollEvent event = new GrantRollEvent(target_playerRef, reward_box_id, value);
            dispatcher.dispatch(event);
        }

        // BoxRollManager.get().incrementRolls(uuid, reward_box_id, value);
        UUID uuid = target_playerRef.getUuid();
        
        int total_rolls = BoxRollManager.get().getRolls(uuid, reward_box_id);

        Message m = Message.raw(String.format("Total rolls for '%s' increased to 'x%d' for RewardBox '%s'", target_playerRef.getUsername(), total_rolls, reward_box_id));
        
        context.sender().sendMessage(m);
        if (uuid != context.sender().getUuid()) {
            target_playerRef.sendMessage(m);
        }
        
        BoxRollManager.save();
        
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Nullable
    protected String generatePermissionNode() {
        return "rewardboxes.admin";
    }
}
