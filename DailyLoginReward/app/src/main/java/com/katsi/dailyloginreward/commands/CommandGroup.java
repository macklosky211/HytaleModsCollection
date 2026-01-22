package com.katsi.dailyloginreward.commands;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class CommandGroup extends AbstractCommandCollection {

    public CommandGroup() {
        super("daily_reward", "A collection of daily_reward commands");
        
        addAliases("dr", "daily");

        addSubCommand(new Claim());

    }
    
    /**
     * This should be callable by anyone.
     */
    @Override
    protected boolean canGeneratePermission() {
        return false;
    }

}
