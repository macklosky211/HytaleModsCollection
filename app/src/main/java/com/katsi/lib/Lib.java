package com.katsi.lib;

import java.util.logging.Level;

import javax.annotation.Nonnull;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.katsi.lib.config.ConfigManager;

public class Lib extends JavaPlugin{

    private static Lib instance;

    @SuppressWarnings("null")
    public Lib(@Nonnull JavaPluginInit init) {
        super(init);

        new ConfigManager();

        instance = this;
        getLogger().at(Level.INFO).log("[KatsiLib] Initilization Complete.");
    }

    @Override
    public void setup() {
        super.setup();

    }

    public static Lib get(){
        return instance;
    }
}
