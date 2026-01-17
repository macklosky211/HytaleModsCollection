package com.katsi.rewardbox.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.katsi.rewardbox.commands.Debug.grantItemRoll;


public class CommandGroup extends AbstractCommandCollection {

    public CommandGroup() {
        super("RewardBox", "Commands related to creating a rewardbox");
        addAliases("rb");

        addSubCommand(new RollBox());
        addSubCommand(new ListBoxes());
        addSubCommand(new CreateBox());
        addSubCommand(new RemoveBox());
        addSubCommand(new AddItem());
        addSubCommand(new RemoveItem());
        addSubCommand(new grantItemRoll());
    }
    

    /**
     * This should be callable by anyone.
     */
    @Override
    protected boolean canGeneratePermission() {
        return false;
    }
    
}