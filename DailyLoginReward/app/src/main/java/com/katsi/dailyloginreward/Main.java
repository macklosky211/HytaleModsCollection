package com.katsi.dailyloginreward;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.katsi.dailyloginreward.commands.CommandGroup;
import com.katsi.lib.config.ConfigManager;

@SuppressWarnings("null")
public class Main extends JavaPlugin {

    private static Main instance;

    public final String CONFIG_PATH = "DailyLoginRewards.json";
    public final HytaleLogger logger;

    public Main(JavaPluginInit init) {
        super(init);
        instance = this;
        this.logger = getLogger();
        new LoginStore();
        this.logger.at(Level.INFO).log("[DailyLoginReward] Init complete.");
    }

    public static Main get() {
        return instance;
    }

    @Override
    protected void setup() {
        if (!ConfigManager.configExists(CONFIG_PATH)) {
            getLogger().atInfo().log("[DailyLoginReward] Creating default config.");
            create_default_config();
            ConfigManager.saveConfig(CONFIG_PATH, LoginStore.get(), LoginStore.class);
        } else {
            LoginStore loaded_store = ConfigManager.loadConfig(CONFIG_PATH, LoginStore.class);
            LoginStore.setInstance(loaded_store);
        }

        ConfigManager.setLogger(CONFIG_PATH, getLogger());
        
        getCommandRegistry().registerCommand(new CommandGroup());

        getEventRegistry().register(PlayerConnectEvent.class, this::dailyLoginPlayerConnectedEvent);
        
        getLogger().at(Level.INFO).log("[DailyLoginReward] setup complete!");
    }

    @Override
    protected void start() {
        getLogger().at(Level.INFO).log("[DailyLoginReward] started!");
    }

    @Override
    public void shutdown() {
        ConfigManager.saveConfig(CONFIG_PATH, LoginStore.get(), LoginStore.class);

        getLogger().at(Level.INFO).log("[DailyLoginReward] Plugin shutdown complete!");
    }
    
    LoginStore create_default_config() {
        if (LoginStore.get() == null) new LoginStore();
        
        ArrayList<DailyReward> daily_reward = new ArrayList<>();

        Map<String, Double> reward_1_items = new HashMap<String, Double>();
        reward_1_items.put("Tool_Pickaxe_Crude", 1d);
        reward_1_items.put("Tool_Hatchet_Crude", 1d);

        Map<String, Double> reward_keys_1 = new HashMap<String, Double>();
        reward_keys_1.put("ExampleRewardBox", 1d);
        
        Map<String, Double> reward_2_items = new HashMap<String, Double>();
        reward_2_items.put("Tool_Pickaxe_Copper", 1d);
        reward_2_items.put("Tool_Hatchet_Copper", 1d);
        
        Map<String, Double> reward_3_items = new HashMap<String, Double>();
        reward_3_items.put("Tool_Pickaxe_Iron", 1d);
        reward_3_items.put("Tool_Hatchet_Iron", 1d);

        Map<String, Double> reward_4_items = new HashMap<String, Double>();
        Map<String, Double> reward_keys_4 = new HashMap<String, Double>();
        reward_keys_4.put("ExampleRewardBox", 5d);

        
        daily_reward.add(new DailyReward(reward_1_items, reward_keys_1));
        daily_reward.add(new DailyReward(reward_2_items));
        daily_reward.add(new DailyReward(reward_3_items));
        daily_reward.add(new DailyReward(reward_4_items, reward_keys_4));

        LoginStore.setLoginRewards(daily_reward);

        return LoginStore.get();
    }

    public void dailyLoginPlayerConnectedEvent(PlayerConnectEvent event) {
        if (LoginStore.hasRewardAvailable(event.getPlayerRef().getUuid())) {
            event.getPlayerRef().sendMessage(Message.raw(String.format("[DailyReward] You have a daily reward available! Claim it with '/daily claim'")));
        }
    }

}
