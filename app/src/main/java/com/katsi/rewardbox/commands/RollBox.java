package com.katsi.rewardbox.commands;

import javax.annotation.Nonnull;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.katsi.rewardbox.events.ConsumeRollEvent;

public class RollBox extends AbstractPlayerCommand {

    private final RequiredArg<String> item_box_arg;

    @SuppressWarnings("null")
    public RollBox() {
        super("rollbox", "Rolls an itembox for the player and gives the the item that they pull.");

        addAliases("roll", "pull");
        // requirePermission("rewardboxes.admin");

        item_box_arg = withRequiredArg("RewardBox name", "The name of the RewardBox you are attempting to roll.", ArgTypes.STRING);
    }

    // @SuppressWarnings("null")
    @Override
    protected void execute(@Nonnull CommandContext context, @Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> storeRef, @Nonnull PlayerRef playerRef, @Nonnull World world) {

        String reward_box_id = item_box_arg.get(context);

        IEventDispatcher<ConsumeRollEvent, ConsumeRollEvent> dispatcher = HytaleServer.get().getEventBus().dispatchFor(ConsumeRollEvent.class);
        if (dispatcher.hasListener()) {
            ConsumeRollEvent event = new ConsumeRollEvent(playerRef, reward_box_id);
            dispatcher.dispatch(event);
        }

    }

    /**
     * This should be callable by anyone.
     */
    @Override
    protected boolean canGeneratePermission() {
        return false;
    }
    
}
