This is a mod for Hytale.

This mod adds the ability to create random 'loot-boxes'. 

Standalone it only has /command functionalities, but as a api it adds the ability to trigger custom events to reward players with a 'loot-box'.

# Usage:

All of the following commands start with “/RewardBox” {Example: “/RewardBox create example”}

| Command | Description | Permission |
| :---: | :---: | :---: |
| list\_boxes list | Lists all boxes & how many items they contain “–rewardbox ExampleBox” can be used to list all items in an RewardBox |  |
| roll\_box \<RewardBox\_Name> roll, pull | If the user has a ‘key’ to the specified RewardBox, it rolls the box and puts the reward in the player's inventory. If there is not enough room in a player's inventory then the item is dropped on the ground. |  |
| create \<Name> | Creates a new RewardBox | rewardboxes.admin |
| remove_box \<Name>  rem_box, rb | Deletes an entire RewardBox | rewardboxes.admin |
| add\_item \<RewardBox\_name> \<ItemID> \<Quantity> \<Chance> add, a | Adds an item to a RewardBox. The same item can be added multiple times ItemID can be any item: {"Tool\_Pickaxe\_Crude"} Chance is represented as: {Chance / Sum(RewardBox.items.chance)}  | rewardboxes.admin |
| remove\_item \<RewardBox\_name> \<ItemID>  rem, r | Removes all items that match the ItemID Example: if you have a box with two “Tool\_Pickaxe\_Crude” it will remove both entries from the box | rewardboxes.admin |
| grant\_item\_roll \<player\_name> \<RewardBox\_Name> optional\_arg:\<amount> add\_roll, ar | Adds a ‘key’ to the targeted player, this allows them to open the specified box \<amount> of times. \<amount> when excluded from command is 1 | rewardboxes.admin |


# For Server Owners & Modders

## Json Config

The first time this mod launches it will create a {server\_root\_directory}/config/KatsiLib/RewardBoxes.json, this file stores all the information about the RewardBoxes on your server, the commands dynamically save to this file so if you prefer to setup RewardBoxes from within the game then that works too. 

When you first launch the mod it will generate a default config with examples, but only if the file didn't exist. 

Default & Example for creating an RewardBox config:  
```  
{  
    "is_enabled": true,  
    "reward_boxes": [  
        {  
            "box_creator": "Katsi",  
            "box_name": "DefaultRewardBox",  
            "items": [  
                {  
                    "item_id": "Tool_Pickaxe_Crude",  
                    "pull_chance": 0.5,  
                    "quantity": 1  
                },  
                {  
                    "item_id": "Tool_Hatchet_Crude",  
                    "pull_chance": 0.5,  
                    "quantity": 1  
                }  
            ],  
            "total_chance": 1.0  
        },  
        {  
            "box_creator": "Katsi",  
            "box_name": "ExampleRewardBox",  
            "items": [  
                {  
                    "item_id": "Weapon_Daggers_Crude",  
                    "pull_chance": 35.0,  
                    "quantity": 1  
                },  
                {  
                    "item_id": "Weapon_Daggers_Bone",  
                    "pull_chance": 30.0,  
                    "quantity": 1  
                },  
                {  
                    "item_id": "Weapon_Daggers_Bronze",  
                    "pull_chance": 20.0,  
                    "quantity": 1  
                },  
                {  
                    "item_id": "Weapon_Daggers_Copper",  
                    "pull_chance": 10.0,  
                    "quantity": 1  
                },  
                {  
                    "item_id": "Weapon_Daggers_Iron",  
                    "pull_chance": 5.0,  
                    "quantity": 1  
                }  
            ],  
            "total_chance": 100.0  
        }  
    ]  
}  
```

Note: “total\_chance” just needs to be a number, it is calculated on startup so its value does not matter.

# Modding API

More documentation will be released later. I will release an example project on how to use this mod for developers. \- If you want to start without said documentation, just dispatch either an GrantRollEvent or ConsumeRollEvent.

