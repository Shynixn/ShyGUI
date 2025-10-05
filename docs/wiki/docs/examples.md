# GUI Examples and Templates

This page provides real-world examples and templates based on the included GUI files. These examples demonstrate various ShyGUI features and patterns you can use in your own server.

## Table of Contents

1. [Pet Management System](#pet-management-system)
2. [Shop and Commerce GUIs](#shop-and-commerce-guis)
3. [Admin and Moderation Panels](#admin-and-moderation-panels)
4. [Player Information Displays](#player-information-displays)
5. [Navigation and Menu Systems](#navigation-and-menu-systems)
6. [Advanced Patterns](#advanced-patterns)

## Pet Management System

The PetBlocks example GUIs demonstrate a complete pet management system with dynamic content and conditional displays.

### Main Pet Control Panel

Based on `petblocks_main_menu.yml`, this example shows how to create a comprehensive pet management interface:

```yaml
name: "pet_main_menu"
windowType: "SIX_ROW"
title: "&6Pet Management"
updateIntervalTicks: 100

# Only allow access if player has a pet selected
condition:
  script: '!CONTAINS("%petblocks_pet_itemType_selected%", "petblocks_pet_itemType_selected")'

# Custom command for easy access
command:
  command: "pets"
  permission: "pets.use"
  usage: "/pets"
  description: "Opens the pet management menu"
  aliases:
    - "pet"
    - "mypet"

items:
  # Background decoration
  - row: 1
    col: 1
    rowSpan: 6
    colSpan: 9
    item:
      typeName: "minecraft:black_stained_glass_pane"
      displayName: " "

  # Dynamic pet display - shows current pet
  - row: 1
    col: 5
    item:
      typeName: "%petblocks_pet_itemType_selected%"
      durability: "%petblocks_pet_itemDurability_selected%"
      displayName: "%petblocks_pet_displayName_selected%"
      skinBase64: "%petblocks_pet_itemHeadBase64_selected%"

  # Conditional spawn/despawn button
  # Shows when pet is spawned
  - row: 2
    col: 5
    item:
      typeName: "minecraft:player_head"
      displayName: "&aPet Active"
      skinBase64: "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTI5YjBiMmY3YzhhNWYwNjBmYjY3NDBjZmM0Y2I3OGVmYjYxZjlmMTZjOGU5NGYxYjc3MjU2N2ZkNDJjNjViYSJ9fX0="
      lore:
        - "&7Status: &aSpawned"
        - "&7Click to despawn your pet"
    condition:
      script: '"%petblocks_pet_isSpawned_selected%" == "true"'
    commands:
      - type: "PER_PLAYER"
        command: "/petblocks despawn %petblocks_pet_name_selected%"
      - type: "PER_PLAYER"
        command: "/shygui close"

  # Shows when pet is not spawned
  - row: 2
    col: 5
    item:
      typeName: "minecraft:player_head"
      displayName: "&cPet Inactive"
      skinBase64: "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWE0NGRhMGFmOTBhY2I2MDdlYWIyOGYyODc5ODUwNGE3MzE4OTM3YTE1N2ZiM2EwM2UxNDdhZTcwZTM1MzFjZSJ9fX0="
      lore:
        - "&7Status: &cDespawned"
        - "&7Click to spawn your pet"
    condition:
      script: '"%petblocks_pet_isSpawned_selected%" == "false"'
    commands:
      - type: "PER_PLAYER"
        command: "/petblocks spawn %petblocks_pet_name_selected%"
      - type: "PER_PLAYER"
        command: "/shygui close"

  # Pet controls - ride/mount system
  - row: 5
    col: 8
    item:
      typeName: "minecraft:saddle"
      displayName: "&eRide Pet"
      lore:
        - "&7Click to ride your pet"
    condition:
      script: '"%petblocks_pet_isMounted_selected%" == "false"'
    commands:
      - type: "PER_PLAYER"
        command: "/petblocks ride %petblocks_pet_name_selected%"
      - type: "PER_PLAYER"
        command: "/shygui refresh"

  # Dismount option when riding
  - row: 5
    col: 8
    item:
      typeName: "minecraft:saddle"
      displayName: "&eDismount Pet"
      lore:
        - "&7Click to dismount your pet"
    condition:
      script: '"%petblocks_pet_isMounted_selected%" == "true"'
    commands:
      - type: "PER_PLAYER"
        command: "/petblocks unmount %petblocks_pet_name_selected%"
      - type: "PER_PLAYER"
        command: "/shygui refresh"

  # Navigation to skin selection
  - row: 2
    col: 2
    item:
      typeName: "minecraft:player_head"
      displayName: "&6Pet Skins"
      skinBase64: "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDU0ODA0NmRkNmZhMDc3MzBjM2ZiYjRlY2JlNWMxYTM0MDYyOTc0NDgwNmY4ODE0NjUzZDZhYTc1YTkwMzJjNCJ9fX0="
      lore:
        - "&7Customize your pet's appearance"
        - "&7Click to open skin menu"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next pet_skins_menu"

  # Pet management options
  - row: 5
    col: 2
    item:
      typeName: "minecraft:name_tag"
      displayName: "&eRename Pet"
      lore:
        - "&7Click to rename your pet"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui close"
      - type: "SERVER_PER_PLAYER"
        command: "/petblocks suggestrename %petblocks_pet_name_selected% %petblocks_player_name%"

  # Close button
  - row: 6
    col: 5
    item:
      typeName: "minecraft:barrier"
      displayName: "&cClose"
      lore:
        - "&7Close this menu"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui close"
```

### Skin Selection System

Based on `petblocks_skins_blockskins_menu.yml`, showing how to create item selection grids:

```yaml
name: "pet_block_skins"
windowType: "SIX_ROW"
title: "&6Block Skins"
updateIntervalTicks: 10

items:
  # Background
  - row: 1
    col: 1
    rowSpan: 6
    colSpan: 9
    item:
      typeName: "minecraft:black_stained_glass_pane"
      displayName: " "

  # Current pet display
  - row: 1
    col: 5
    item:
      typeName: "%petblocks_pet_itemType_selected%"
      durability: "%petblocks_pet_itemDurability_selected%"
      displayName: "%petblocks_pet_displayName_selected%"
      skinBase64: "%petblocks_pet_itemHeadBase64_selected%"

  # Skin options grid
  - row: 3
    col: 1
    item:
      typeName: "minecraft:stone"
      displayName: "&eStone Skin"
      lore:
        - "&7Classic stone appearance"
        - "&7Click to apply this skin"
    commands:
      - type: "SERVER_PER_PLAYER"
        command: "/petblocks skinType %petblocks_pet_name_selected% minecraft:stone 0 %petblocks_owner_name%"

  - row: 3
    col: 2
    item:
      typeName: "minecraft:granite"
      displayName: "&eGranite Skin"
      lore:
        - "&7Reddish granite texture"
        - "&7Click to apply this skin"
    commands:
      - type: "SERVER_PER_PLAYER"
        command: "/petblocks skinType %petblocks_pet_name_selected% minecraft:granite 1 %petblocks_owner_name%"

  - row: 3
    col: 3
    item:
      typeName: "minecraft:gold_block"
      displayName: "&eGold Block Skin"
      lore:
        - "&7Shiny golden appearance"
        - "&7Click to apply this skin"
    commands:
      - type: "SERVER_PER_PLAYER"
        command: "/petblocks skinType %petblocks_pet_name_selected% minecraft:gold_block 0 %petblocks_owner_name%"

  # Back navigation
  - row: 6
    col: 5
    item:
      typeName: "minecraft:barrier"
      displayName: "&cBack"
      lore:
        - "&7Return to main pet menu"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui back"
```

## Shop and Commerce GUIs

### Multi-Category Shop System

```yaml
name: "shop_main"
windowType: "SIX_ROW"
title: "&6Server Shop"
updateIntervalTicks: 200

command:
  command: "shop"
  permission: "shop.use"
  usage: "/shop"
  description: "Opens the server shop"

items:
  # Decorative border
  - row: 1
    col: 1
    rowSpan: 1
    colSpan: 9
    item:
      typeName: "minecraft:blue_stained_glass_pane"
      displayName: " "

  - row: 6
    col: 1
    rowSpan: 1
    colSpan: 9
    item:
      typeName: "minecraft:blue_stained_glass_pane"
      displayName: " "

  # Player info display
  - row: 1
    col: 5
    item:
      typeName: "minecraft:player_head"
      displayName: "&b%shygui_player_name%'s Wallet"
      lore:
        - "&7Balance: &a$%vault_eco_balance%"
        - "&7Rank: &6%vault_group%"
        - ""
        - "&7Welcome to the shop!"

  # Category sections
  - row: 3
    col: 2
    item:
      typeName: "minecraft:diamond_sword"
      displayName: "&eWeapons & Tools"
      lore:
        - "&7Swords, pickaxes, and more"
        - "&7Click to browse weapons"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next shop_weapons tools 1"

  - row: 3
    col: 3
    item:
      typeName: "minecraft:iron_chestplate"
      displayName: "&eArmor & Protection"
      lore:
        - "&7Helmets, chestplates, boots"
        - "&7Click to browse armor"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next shop_armor protection 1"

  - row: 3
    col: 4
    item:
      typeName: "minecraft:stone"
      displayName: "&eBlocks & Building"
      lore:
        - "&7Building materials and blocks"
        - "&7Click to browse blocks"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next shop_blocks building 1"

  - row: 3
    col: 5
    item:
      typeName: "minecraft:cooked_beef"
      displayName: "&eFood & Consumables"
      lore:
        - "&7Food, potions, and consumables"
        - "&7Click to browse food"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next shop_food consumables 1"

  - row: 3
    col: 6
    item:
      typeName: "minecraft:redstone"
      displayName: "&eRedstone & Tech"
      lore:
        - "&7Redstone components and machinery"
        - "&7Click to browse tech items"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next shop_redstone technology 1"

  - row: 3
    col: 7
    item:
      typeName: "minecraft:diamond"
      displayName: "&eSpecial Items"
      lore:
        - "&7Rare and special items"
        - "&7Click to browse specials"
    condition:
      # Only show for VIP members
      script: 'CONTAINS_IGNORE_CASE("%vault_group%", "vip")'
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next shop_special vip 1"

  # Quick buy section
  - row: 5
    col: 3
    item:
      typeName: "minecraft:bread"
      displayName: "&eBread &7- &a$5"
      lore:
        - "&7Restores hunger quickly"
        - "&7Balance: &a$%vault_eco_balance%"
        - ""
        - "&aClick to buy for $5"
    condition:
      script: 'NUMBER("%vault_eco_balance%") >= 5'
    commands:
      - type: "SERVER_PER_PLAYER"
        command: "/eco take %shygui_player_name% 5"
      - type: "SERVER_PER_PLAYER"
        command: "/give %shygui_player_name% bread 1"
      - type: "PER_PLAYER"
        command: "/shygui message &aYou bought bread for $5!"
      - type: "PER_PLAYER"
        command: "/shygui refresh"

  # Insufficient funds version
  - row: 5
    col: 3
    item:
      typeName: "minecraft:barrier"
      displayName: "&cBread &7- &a$5"
      lore:
        - "&7Restores hunger quickly"
        - "&7Balance: &c$%vault_eco_balance%"
        - ""
        - "&cInsufficient funds!"
    condition:
      script: 'NUMBER("%vault_eco_balance%") < 5'

  # Close button
  - row: 6
    col: 9
    item:
      typeName: "minecraft:barrier"
      displayName: "&cClose"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui close"
```

### Category Shop with Pagination

```yaml
name: "shop_weapons"
windowType: "SIX_ROW"
title: "&e%shygui_gui_param1% Shop - Page %shygui_gui_param3%"
updateIntervalTicks: 200

items:
  # Navigation header
  - row: 1
    col: 1
    item:
      typeName: "minecraft:arrow"
      displayName: "&7← Back to Shop"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui back"

  - row: 1
    col: 5
    item:
      typeName: "minecraft:book"
      displayName: "&e%shygui_gui_param1% Shop"
      lore:
        - "&7Category: &b%shygui_gui_param1%"
        - "&7Type: &d%shygui_gui_param2%"
        - "&7Page: &e%shygui_gui_param3%"

  # Item grid - weapons
  - row: 2
    col: 2
    item:
      typeName: "minecraft:wooden_sword"
      displayName: "&eWooden Sword &7- &a$10"
      lore:
        - "&7Basic weapon for beginners"
        - "&7Damage: &c4 ❤"
        - "&7Balance: &a$%vault_eco_balance%"
        - ""
        - "&aClick to purchase!"
    condition:
      script: 'NUMBER("%vault_eco_balance%") >= 10'
    commands:
      - type: "SERVER_PER_PLAYER"
        command: "/eco take %shygui_player_name% 10"
      - type: "SERVER_PER_PLAYER"
        command: "/give %shygui_player_name% wooden_sword 1"
      - type: "PER_PLAYER"
        command: "/shygui message &aPurchased Wooden Sword for $10!"
      - type: "PER_PLAYER"
        command: "/shygui refresh"

  - row: 2
    col: 3
    item:
      typeName: "minecraft:stone_sword"
      displayName: "&eStone Sword &7- &a$25"
      lore:
        - "&7Improved weapon with better damage"
        - "&7Damage: &c5 ❤"
        - "&7Balance: &a$%vault_eco_balance%"
        - ""
        - "&aClick to purchase!"
    condition:
      script: 'NUMBER("%vault_eco_balance%") >= 25'
    commands:
      - type: "SERVER_PER_PLAYER"
        command: "/eco take %shygui_player_name% 25"
      - type: "SERVER_PER_PLAYER"
        command: "/give %shygui_player_name% stone_sword 1"
      - type: "PER_PLAYER"
        command: "/shygui message &aPurchased Stone Sword for $25!"
      - type: "PER_PLAYER"
        command: "/shygui refresh"

  # Pagination controls
  - row: 6
    col: 3
    item:
      typeName: "minecraft:arrow"
      displayName: "&7← Previous Page"
    condition:
      script: 'NUMBER("%shygui_gui_param3%") > 1'
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next shop_weapons %shygui_gui_param1% %shygui_gui_param2% NUMBER(%shygui_gui_param3%) - 1"

  - row: 6
    col: 7
    item:
      typeName: "minecraft:arrow"
      displayName: "&7Next Page →"
    condition:
      script: 'NUMBER("%shygui_gui_param3%") < 3'  # Assuming 3 max pages
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next shop_weapons %shygui_gui_param1% %shygui_gui_param2% NUMBER(%shygui_gui_param3%) + 1"
```

## Admin and Moderation Panels

### Staff Control Panel

```yaml
name: "admin_panel"
windowType: "SIX_ROW"
title: "&cAdmin Control Panel"
updateIntervalTicks: 100

command:
  command: "admin"
  permission: "server.admin"
  usage: "/admin"
  description: "Opens the admin control panel"

items:
  # Header decoration
  - row: 1
    col: 1
    rowSpan: 1
    colSpan: 9
    item:
      typeName: "minecraft:red_stained_glass_pane"
      displayName: " "

  # Admin info
  - row: 1
    col: 5
    item:
      typeName: "minecraft:player_head"
      displayName: "&cAdmin: %shygui_player_name%"
      lore:
        - "&7Rank: &c%vault_group%"
        - "&7World: &e%player_world%"
        - "&7Online: &a%server_online%/%server_max_players%"

  # Player management
  - row: 3
    col: 2
    item:
      typeName: "minecraft:player_head"
      displayName: "&ePlayer Management"
      lore:
        - "&7View and manage players"
        - "&7Kick, ban, teleport"
        - "&7Click to open"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next admin_players management %shygui_player_name%"

  # Server controls
  - row: 3
    col: 3
    item:
      typeName: "minecraft:command_block"
      displayName: "&eServer Controls"
      lore:
        - "&7Server restart, stop, reload"
        - "&7Backup and maintenance"
        - "&7Click to open"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next admin_server controls %shygui_player_name%"

  # World management
  - row: 3
    col: 4
    item:
      typeName: "minecraft:grass_block"
      displayName: "&eWorld Management"
      lore:
        - "&7Manage worlds and regions"
        - "&7WorldEdit, WorldGuard"
        - "&7Click to open"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next admin_worlds management %shygui_player_name%"

  # Economy controls
  - row: 3
    col: 5
    item:
      typeName: "minecraft:emerald"
      displayName: "&eEconomy Management"
      lore:
        - "&7Manage player balances"
        - "&7Economy settings"
        - "&7Click to open"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next admin_economy management %shygui_player_name%"

  # Plugin management
  - row: 3
    col: 6
    item:
      typeName: "minecraft:redstone"
      displayName: "&ePlugin Management"
      lore:
        - "&7Enable/disable plugins"
        - "&7Reload configurations"
        - "&7Click to open"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next admin_plugins management %shygui_player_name%"

  # Quick actions
  - row: 5
    col: 2
    item:
      typeName: "minecraft:paper"
      displayName: "&eBroadcast Message"
      lore:
        - "&7Send message to all players"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui close"
      - type: "SERVER_PER_PLAYER"
        command: "/tellraw @a [{\"text\":\"[ADMIN] \",\"color\":\"red\"},{\"text\":\"%shygui_player_name%\",\"color\":\"yellow\"},{\"text\":\" wants to send a message. Use /msg to respond.\",\"color\":\"white\"}]"

  - row: 5
    col: 4
    item:
      typeName: "minecraft:clock"
      displayName: "&eSet Time"
      lore:
        - "&7Change world time"
        - "&7Click for day time"
    commands:
      - type: "SERVER_PER_PLAYER"
        command: "/time set day"
      - type: "PER_PLAYER"
        command: "/shygui message &aTime set to day!"

  - row: 5
    col: 6
    item:
      typeName: "minecraft:bone"
      displayName: "&eWeather Control"
      lore:
        - "&7Change weather"
        - "&7Click for clear weather"
    commands:
      - type: "SERVER_PER_PLAYER"
        command: "/weather clear"
      - type: "PER_PLAYER"
        command: "/shygui message &aWeather set to clear!"

  # Emergency controls
  - row: 5
    col: 8
    item:
      typeName: "minecraft:tnt"
      displayName: "&cEmergency Stop"
      lore:
        - "&cStop server immediately"
        - "&cUse only in emergencies!"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next admin_confirm_stop emergency %shygui_player_name%"
```

### Player Management Interface

```yaml
name: "admin_players"
windowType: "SIX_ROW"
title: "&ePlayer Management"
updateIntervalTicks: 40

items:
  # Back navigation
  - row: 1
    col: 1
    item:
      typeName: "minecraft:arrow"
      displayName: "&7← Back to Admin Panel"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui back"

  # Search and filter options would go here
  # For this example, we'll show online players

  # Player list (would be dynamically generated in a real implementation)
  - row: 2
    col: 2
    item:
      typeName: "minecraft:player_head"
      displayName: "&aOnline Player Example"
      lore:
        - "&7Click to manage this player"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next admin_player_detail PlayerName actions %shygui_player_name%"

  # Bulk actions
  - row: 6
    col: 3
    item:
      typeName: "minecraft:paper"
      displayName: "&eMessage All Players"
      lore:
        - "&7Send message to all online players"
    commands:
      - type: "SERVER_PER_PLAYER"
        command: "/tellraw @a [{\"text\":\"[ADMIN MESSAGE] \",\"color\":\"gold\"},{\"text\":\"Administrator %shygui_player_name% has sent a server-wide notification.\",\"color\":\"yellow\"}]"

  - row: 6
    col: 7
    item:
      typeName: "minecraft:barrier"
      displayName: "&cKick All Non-Staff"
      lore:
        - "&7Kick all players except staff"
        - "&cUse with caution!"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next admin_confirm_kickall emergency %shygui_player_name%"
```

## Player Information Displays

### Player Statistics Dashboard

```yaml
name: "player_stats"
windowType: "SIX_ROW"
title: "&9%shygui_player_name%'s Statistics"
updateIntervalTicks: 100

command:
  command: "stats"
  permission: "server.stats"
  usage: "/stats"
  description: "View your player statistics"

items:
  # Player head and basic info
  - row: 1
    col: 5
    item:
      typeName: "minecraft:player_head"
      displayName: "&b%shygui_player_name%"
      lore:
        - "&7Display Name: %shygui_player_displayName%"
        - "&7UUID: &8%player_uuid%"
        - "&7First Join: &e%player_first_join_date%"
        - "&7Last Seen: &e%player_last_join_date%"
        - "&7Playtime: &a%statistic_time_played%"

  # Economic information
  - row: 2
    col: 3
    item:
      typeName: "minecraft:gold_ingot"
      displayName: "&6Economic Stats"
      lore:
        - "&7Balance: &a$%vault_eco_balance%"
        - "&7Rank: &6%vault_group%"
        - "&7Total Earned: &a$%playerpoints_points%"

  # Gameplay statistics
  - row: 2
    col: 5
    item:
      typeName: "minecraft:diamond_sword"
      displayName: "&eCombat Stats"
      lore:
        - "&7Player Kills: &c%statistic_player_kills%"
        - "&7Deaths: &c%statistic_deaths%"
        - "&7Mob Kills: &c%statistic_mob_kills%"
        - "&7K/D Ratio: &e%statistic_kdr%"

  - row: 2
    col: 7
    item:
      typeName: "minecraft:diamond_pickaxe"
      displayName: "&eMining Stats"
      lore:
        - "&7Blocks Broken: &b%statistic_blocks_broken%"
        - "&7Blocks Placed: &b%statistic_blocks_placed%"
        - "&7Diamonds Mined: &b%statistic_diamond_ore_mined%"

  # Achievements section
  - row: 4
    col: 3
    item:
      typeName: "minecraft:trophy"
      displayName: "&6Achievements"
      lore:
        - "&7Completed: &a%achievements_completed%"
        - "&7Total Available: &e%achievements_total%"
        - "&7Progress: &a%achievements_percentage%&7%"

  # Social stats
  - row: 4
    col: 5
    item:
      typeName: "minecraft:book"
      displayName: "&eSocial Stats"
      lore:
        - "&7Messages Sent: &b%chatlogger_messages_sent%"
        - "&7Commands Used: &b%statistic_commands_used%"
        - "&7Friends: &a%friends_count%"

  # Server ranks/progression
  - row: 4
    col: 7
    item:
      typeName: "minecraft:experience_bottle"
      displayName: "&aProgression"
      lore:
        - "&7Level: &a%player_level%"
        - "&7Experience: &a%player_total_experience%"
        - "&7Next Level: &e%player_exp_to_level%"

  # Quick actions
  - row: 6
    col: 2
    item:
      typeName: "minecraft:writable_book"
      displayName: "&eView Detailed Stats"
      lore:
        - "&7Open full statistics page"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next player_detailed_stats %shygui_player_name%"

  - row: 6
    col: 8
    item:
      typeName: "minecraft:clock"
      displayName: "&eRefresh Stats"
      lore:
        - "&7Update all statistics"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui refresh"
```

## Navigation and Menu Systems

### Main Server Hub Menu

```yaml
name: "server_hub"
windowType: "SIX_ROW"
title: "&bServer Hub - Welcome %shygui_player_name%!"
updateIntervalTicks: 200

command:
  command: "hub"
  permission: "server.hub"
  usage: "/hub"
  description: "Opens the server main menu"
  aliases:
    - "menu"
    - "main"

items:
  # Decorative borders
  - row: 1
    col: 1
    rowSpan: 1
    colSpan: 9
    item:
      typeName: "minecraft:cyan_stained_glass_pane"
      displayName: " "

  - row: 6
    col: 1
    rowSpan: 1
    colSpan: 9
    item:
      typeName: "minecraft:cyan_stained_glass_pane"
      displayName: " "

  # Player info
  - row: 1
    col: 5
    item:
      typeName: "minecraft:player_head"
      displayName: "&b%shygui_player_name%"
      lore:
        - "&7Rank: &6%vault_group%"
        - "&7Balance: &a$%vault_eco_balance%"
        - "&7World: &e%player_world%"
        - "&7Online: &a%server_online%&7/&e%server_max_players%"

  # Main navigation sections
  - row: 3
    col: 2
    item:
      typeName: "minecraft:compass"
      displayName: "&eTeleport Hub"
      lore:
        - "&7Quick travel to important locations"
        - "&7Spawn, shops, arenas, and more"
        - "&7Click to open teleport menu"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next teleport_menu locations main"

  - row: 3
    col: 3
    item:
      typeName: "minecraft:emerald"
      displayName: "&eServer Shop"
      lore:
        - "&7Buy and sell items"
        - "&7Multiple categories available"
        - "&7Current balance: &a$%vault_eco_balance%"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next shop_main"

  - row: 3
    col: 4
    item:
      typeName: "minecraft:iron_sword"
      displayName: "&ePvP Arena"
      lore:
        - "&7Join combat arenas"
        - "&7Compete with other players"
        - "&7Earn rewards and rankings"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next pvp_menu arenas combat"

  - row: 3
    col: 5
    item:
      typeName: "minecraft:crafting_table"
      displayName: "&eMinigames"
      lore:
        - "&7Join fun minigames"
        - "&7Parkour, races, puzzles"
        - "&7Play with friends!"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next minigames_menu games lobby"

  - row: 3
    col: 6
    item:
      typeName: "minecraft:book"
      displayName: "&ePlayer Stats"
      lore:
        - "&7View your statistics"
        - "&7Achievements and progress"
        - "&7Compare with friends"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next player_stats %shygui_player_name%"

  - row: 3
    col: 7
    item:
      typeName: "minecraft:nether_star"
      displayName: "&eVIP Features"
      lore:
        - "&7Exclusive VIP content"
        - "&7Special perks and benefits"
    condition:
      script: 'CONTAINS_IGNORE_CASE("%vault_group%", "vip")'
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next vip_menu features premium"

  # Staff section (conditional)
  - row: 3
    col: 8
    item:
      typeName: "minecraft:redstone"
      displayName: "&cStaff Panel"
      lore:
        - "&7Moderation and admin tools"
        - "&7Server management"
        - "&cStaff Only"
    condition:
      script: 'CONTAINS_IGNORE_CASE("%vault_group%", "staff") || CONTAINS_IGNORE_CASE("%vault_group%", "admin")'
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next admin_panel"

  # Quick actions bottom row
  - row: 5
    col: 2
    item:
      typeName: "minecraft:paper"
      displayName: "&eServer Rules"
      lore:
        - "&7Read server rules and guidelines"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next server_rules information"

  - row: 5
    col: 4
    item:
      typeName: "minecraft:writable_book"
      displayName: "&eHelp & Support"
      lore:
        - "&7Get help and submit tickets"
        - "&7FAQ and tutorials"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next help_menu support tickets"

  - row: 5
    col: 6
    item:
      typeName: "minecraft:ender_pearl"
      displayName: "&eServer List"
      lore:
        - "&7Connect to other servers"
        - "&7Survival, Creative, Minigames"
    condition:
      # Only show if BungeeCord is available
      script: 'LENGTH("%bungee_server%") > 0'
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next server_list network bungee"

  - row: 5
    col: 8
    item:
      typeName: "minecraft:clock"
      displayName: "&eSettings"
      lore:
        - "&7Player preferences"
        - "&7Chat, notifications, privacy"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next player_settings preferences %shygui_player_name%"
```

## Advanced Patterns

### Confirmation Dialog System

```yaml
name: "confirm_purchase"
windowType: "THREE_ROW"
title: "&cConfirm Purchase"
updateIntervalTicks: 0

# Parameters: item_name, price, command
items:
  # Background - red for danger/important decision
  - row: 1
    col: 1
    rowSpan: 3
    colSpan: 9
    item:
      typeName: "minecraft:red_stained_glass_pane"
      displayName: " "

  # Item being purchased
  - row: 1
    col: 5
    item:
      typeName: "minecraft:diamond"  # Would be dynamic based on item
      displayName: "&e%shygui_gui_param1%"
      lore:
        - "&7Price: &a$%shygui_gui_param2%"
        - "&7Your balance: &a$%vault_eco_balance%"
        - ""
        - "&eConfirm this purchase?"

  # Confirm button
  - row: 2
    col: 3
    item:
      typeName: "minecraft:emerald_block"
      displayName: "&a✓ CONFIRM PURCHASE"
      lore:
        - "&7Purchase: &e%shygui_gui_param1%"
        - "&7Cost: &a$%shygui_gui_param2%"
        - ""
        - "&aClick to confirm!"
    commands:
      - type: "SERVER_PER_PLAYER"
        command: "%shygui_gui_param3%"  # Execute the purchase command
      - type: "PER_PLAYER"
        command: "/shygui message &aPurchase completed!"
      - type: "PER_PLAYER"
        command: "/shygui back"

  # Cancel button
  - row: 2
    col: 7
    item:
      typeName: "minecraft:redstone_block"
      displayName: "&c✗ CANCEL"
      lore:
        - "&7Cancel this purchase"
        - "&7No money will be charged"
        - ""
        - "&cClick to cancel"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui message &cPurchase cancelled"
      - type: "PER_PLAYER"
        command: "/shygui back"
```

### Dynamic List with Search

```yaml
name: "player_list"
windowType: "SIX_ROW"
title: "&eOnline Players (%server_online%)"
updateIntervalTicks: 60

# Parameter: search_term (optional)
items:
  # Search info
  - row: 1
    col: 5
    item:
      typeName: "minecraft:spyglass"
      displayName: "&ePlayer Search"
      lore:
        - "&7Search term: &b%shygui_gui_param1%"
        - "&7Total online: &a%server_online%"
        - "&7Use /playerlist <name> to search"

  # Player entries (would be dynamically generated)
  # This is a static example showing the pattern
  - row: 2
    col: 2
    item:
      typeName: "minecraft:player_head"
      displayName: "&aPlayer1"
      lore:
        - "&7Rank: &6VIP"
        - "&7World: &esurvival"
        - "&7Playtime: &a2h 30m"
        - ""
        - "&7Click to view profile"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next player_profile Player1 view %shygui_player_name%"

  # Pagination
  - row: 6
    col: 4
    item:
      typeName: "minecraft:arrow"
      displayName: "&7← Previous"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next player_list %shygui_gui_param1% NUMBER(%shygui_gui_param2% || 1) - 1"

  - row: 6
    col: 6
    item:
      typeName: "minecraft:arrow"
      displayName: "&7Next →"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next player_list %shygui_gui_param1% NUMBER(%shygui_gui_param2% || 1) + 1"
```

These examples demonstrate the power and flexibility of ShyGUI for creating sophisticated menu systems. You can combine these patterns and adapt them to your server's specific needs. Remember to test thoroughly and adjust permissions, placeholders, and commands to match your server's plugin configuration.