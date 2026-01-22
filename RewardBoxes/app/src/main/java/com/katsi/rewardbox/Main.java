package com.katsi.rewardbox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import com.google.gson.Gson;
import com.hypixel.hytale.server.core.permissions.PermissionsModule;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.katsi.rewardbox.commands.CommandGroup;
import com.katsi.rewardbox.events.ConsumeRollEvent;
import com.katsi.rewardbox.events.GrantRollEvent;

@SuppressWarnings({"null"})
public class Main extends JavaPlugin {

    private static Main instance;
    
    public Main(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;

        getLogger().at(Level.INFO).log("[RewardBoxMod] Initilization Complete.");

    }
    
    public static Main get() {
        return instance;
    }

    @Override
    protected void setup() {
        super.setup();
        getCommandRegistry().registerCommand(new CommandGroup());

        if (!initialize_config_folder()) getLogger().at(Level.SEVERE).log("[RewardBoxMod] Failed to initialize configs folder. This mod will not function properly unless {server_root}/config/KatsiLib exists.");
        if (!initialize_config_json()) getLogger().at(Level.SEVERE).log("[RewardBoxMod] Failed to initialize RewardBoxes.json. This mod will not function properly unless {server_root}/config/KatsiLib/RewardBoxes.json exists.");
        if (!calculate_all_itemboxes()) getLogger().at(Level.SEVERE).log("[RewardBoxMod] Literally impossible... But we failed to calculate the total probability of each of the ItemBoxes...");

        if (!init_roll_manager()) getLogger().at(Level.SEVERE).log("[RewardBoxMod] Failed to init the roll manager. No one will be able to roll boxes without this.");
        if (!subscribe_to_grant_roll_event()) getLogger().at(Level.SEVERE).log("[RewardBoxMod] Failed to subscribe to GrantRollEvent, no rolls will be granted if this fails.");

        getLogger().at(Level.INFO).log("[RewardBoxMod] setup complete!");
    }

    @Override
    protected void start() {
        PermissionsModule permissions = PermissionsModule.get();
        

        permissions.addGroupPermission("OP", Set.of("rewardboxes.admin")); // Groups are 'Set's so duplicates are removed -> its fine to add this every game launch, it will not be duplicated.
        permissions.addGroupPermission("rewardbox-admin", Set.of("rewardboxes.admin"));
        
        getLogger().at(Level.INFO).log("[RewardBoxMod] started!");
    }
    
    @Override
    public void shutdown() {

        GlobalRewardBox.save();

        getLogger().at(Level.INFO).log("[RewardBoxMod] Plugin shutdown complete!");
    }

    private boolean initialize_config_folder() {
        File directory = new File("./config");
        if (!directory.exists()){
            directory.mkdir();
            getLogger().at(Level.INFO).log("[RewardBoxMod] Directory created: /config");
        }
        
        directory = new File("./config/KatsiLib");
        if (!directory.exists()){
            directory.mkdir();
            getLogger().at(Level.INFO).log("[RewardBoxMod] Directory created: /config/KatsiLib");
        }

        getLogger().at(Level.INFO).log("[RewardBoxMod] Finished initializing {server_root}/config/KatsiLib directories");
        return true;
    }

    private boolean initialize_config_json() {
        
        File happy_json = new File("./config/KatsiLib/RewardBoxes.json");
        
        new GlobalRewardBox(); // init the global instance. This is important, dont not do this. >.> im looking at you, me.
        
        if (!happy_json.exists()){
            try {
                happy_json.createNewFile();
                getLogger().at(Level.INFO).log("[RewardBoxMod] JSON created: /config/KatsiLib/RewardBoxes.json");
            } catch (IOException e) {
                getLogger().at(Level.SEVERE).log(e.toString());
                return false;
            }

            RewardBox default_rewardbox = new RewardBox("Katsi", "DefaultRewardBox")
                .addItem(new RewardBoxItem("Tool_Pickaxe_Crude", 0.5))
                .addItem(new RewardBoxItem("Tool_Hatchet_Crude", 0.5));

            RewardBox example_rewardbox = new RewardBox("Katsi", "ExampleRewardBox")
                .addItem(new RewardBoxItem("Weapon_Daggers_Crude", 35.0))
                .addItem(new RewardBoxItem("Weapon_Daggers_Bone", 30.0))
                .addItem(new RewardBoxItem("Weapon_Daggers_Bronze", 20.0))
                .addItem(new RewardBoxItem("Weapon_Daggers_Copper", 10.0))
                .addItem(new RewardBoxItem("Weapon_Daggers_Iron", 5.0));

            GlobalRewardBox.addRewardBox(default_rewardbox);
            GlobalRewardBox.addRewardBox(example_rewardbox);

            if (GlobalRewardBox.save())
                getLogger().at(Level.INFO).log("[RewardBoxMod] Successfully wrote default RewardBoxes.json file");
            else {
                getLogger().at(Level.SEVERE).log("[RewardBoxMod] Failed to create default RewardBoxes.json file. This will break the mods functionality.");
                return false; // failed to init_config_json so we stop here.
            }

        } else { //Read the JSON since it already exists
            Gson gson = new Gson();
            String file_content = "";
            try {
                Scanner json_reader = new Scanner(happy_json);
                file_content = json_reader.useDelimiter("\\Z").next();
                json_reader.close();
            } catch (FileNotFoundException e) {
                getLogger().at(Level.SEVERE).log("[RewardBoxMod] Failed to (FIND or READ) {server_root}/config/KatsiLib/RewardBoxes.json");
                return false;
            }
            
            gson.fromJson(file_content, GlobalRewardBox.class); // This used to not add the loaded content to the static GlobalRewardBox, but now it does... Im just gonna roll with it...
        }
        
        getLogger().at(Level.INFO).log("[RewardBoxMod] Finished initializing RewardBoxes.json");
        return true;
    }

    private boolean init_roll_manager() {
        new BoxRollManager(); // init global object.
        if (BoxRollManager.hasSave())
            BoxRollManager.load();
        else {
            BoxRollManager.save(); // init save file & save base.
        }
        getLogger().at(Level.INFO).log("[RewardBoxMod] Finished initializing BoxRollManager");
        return true;
    }

    private boolean calculate_all_itemboxes() {
        GlobalRewardBox global_happy_box = GlobalRewardBox.get();
        for (RewardBox reward_box : global_happy_box.getRewardBoxes()) {
            reward_box.recalculateTotalChance();
        }
        return true;
    }

    private boolean subscribe_to_grant_roll_event() {

        getEventRegistry().register(GrantRollEvent.class, GrantRollEvent::event);
        getEventRegistry().register(ConsumeRollEvent.class, ConsumeRollEvent::event);

            

        return true;
    }

}
