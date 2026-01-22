# Summary
This mod adds the ability to gift players with daily login rewards.

# Player Useage
When a player logs into the server they will recieve a message if they have an available reward to claim
The player can claim the daily reward with "/daily_reward claim" or "/daily claim" or "/daily c"

# Server Useage
When the mod first runs it will create a "{Server_Root}/config/KatsiLib/DailyLoginRewards.json" file, this file contains all the relevant information to control this mod.

The following is the default config (With comments)
```
{
    "login_rewards": [
        {
            "items": { # <-- Each login reward has an "items" field. This directly gives the players these items.
                "Tool_Pickaxe_Crude": 1.0, # <-- You can specify any item in the game, including modded items.
                "Tool_Hatchet_Crude": 1.0  # <-- The number represents the quantity of the item that the player will recieve.
            },
            "reward_box_keys": { # <-- Each login reward also has an "reward_box_keys" field, this is used to -
                                 # - specify any RewardBoxes (Another one of my mods) that the player will recieve keys for.
                "ExampleRewardBox": 1.0 # <-- The name of the box and the amount of keys you want the player to recieve.
            }
        },
        {
            "items": {
                "Tool_Hatchet_Copper": 1.0,
                "Tool_Pickaxe_Copper": 1.0
            },
            "reward_box_keys": {} # <-- If you dont want to gift player RewardBox keys, then dont!
        },
        {
            "items": {
                "Tool_Hatchet_Iron": 1.0,
                "Tool_Pickaxe_Iron": 1.0
            },
            "reward_box_keys": {}
        },
        {
            "items": {}, # <-- If you want to, you may give only RewardBox keys. You can even leave both fields empty for a blank reward for that day.
            "reward_box_keys": {
                "ExampleRewardBox": 5.0
            }
        }
    ],
    "map": { # <-- This portion of the file is used for persistent saves across server boots. This just stores relevant player information. You may edit this if you want to... 
        "6bdc927f-b163-41e4-954b-b8f04e4db561": { # <-- Users UUID. "/whoami <playername>" gives this (I think)
            "last_login_epoch": 20471, # <-- The day the user last logged in as an epoch value. TLDR; increment/decrement this value by 1 is going forward/back a day.
            "login_streak": 0.0 # <-- How long this users streak is going.
        }
    }
}
```

## Notes: 
 - Unclaimed rewards are not stored, if you dont claim your login reward then you lose your login streak.
 - The order of "login_rewards" in the config matches the day that the player recieves the items
   - Example: Day 0 -> first item, Day 1 -> second item.
   - the day can have 0 items as a valid config. You can use this to space out days they recieve rewards.
 - Map objects are not permanent to save on file size. Players who have lost their streaks may have their data removed from the file.

# The bread and butter
This mod was intended to show how another mod can use the RewardBox mod, meaning we have some good valid code in here :)

Specifically:
```
IEventDispatcher<GrantRollEvent, GrantRollEvent> dispatcher = HytaleServer.get().getEventBus().dispatchFor(GrantRollEvent.class);
  if (dispatcher.hasListener()) {
      GrantRollEvent event = new GrantRollEvent(playerRef, reward_box, num_keys_to_give);
      dispatcher.dispatch(event);
  }
```
The code above showcases how to dispatch an 'GrantRollEvent' which signals the RewardBox mod to give a key to the player targetted in the event. 
The GrantRollEvent takes 3 arguments -> <PlayerRef = the targetted player\> <RewardBox name = the name of the RewardBox\> <num_keys_to_give = this is 1 by default and can be left out> 

The other event that the RewardBox mod contains is the 'ConsumeRollEvent' it takes the same arguments as the command above and rolls the RewardBoxes for the players.
