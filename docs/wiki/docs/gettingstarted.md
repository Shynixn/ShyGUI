# Getting Started with ShyGUI

This guide will walk you through creating your first interactive GUI menu using ShyGUI. We'll start with basic concepts and gradually build up to more advanced features.

## Prerequisites

- ShyGUI plugin installed on your Bukkit/Folia server
- Basic understanding of YAML file structure
- Text editor (VSCode, Notepad++, or similar recommended)

## Step 1: Verify Installation

### Test the Sample GUI

1. Navigate to the `/plugins/ShyGUI/gui` directory on your server
2. Locate the sample GUI files (e.g., `simple_sample_menu.yml`)
3. Join your server and execute:
   ```
   /shygui open simple_sample_menu
   ```
4. A GUI inventory should open - this confirms ShyGUI is working correctly

### Understanding the File Structure

Your GUI directory should contain files similar to:
```
/plugins/ShyGUI/gui/
├── simple_sample_menu.yml
├── petblocks_main_menu.yml
├── petblocks_skins_menu.yml
└── ... (other example files)
```

## Step 2: Create Your First GUI

### Copy and Modify a Sample

1. **Copy the sample**: Copy `simple_sample_menu.yml` and rename it to `my_first_menu.yml`
2. **Edit the file**: Open `my_first_menu.yml` in your text editor
3. **Change the identifier**: Update the name field at the top:
   ```yaml
   name: "my_first_menu"
   ```

### Basic GUI Structure

Here's a minimal GUI configuration:

```yaml
# Unique identifier for this GUI (must match filename without .yml)
name: "my_first_menu"

# Inventory size: ONE_ROW, TWO_ROW, THREE_ROW, FOUR_ROW, FIVE_ROW, SIX_ROW  
windowType: "THREE_ROW"

# Title displayed at the top of the inventory
title: "&9My First GUI Menu"

# How often to refresh placeholders (in ticks, 20 = 1 second)
updateIntervalTicks: 100

# Optional: Custom command to open this GUI
command:
  command: "mymenu"
  permission: "myplugin.gui"
  usage: "/mymenu"
  description: "Opens my custom GUI menu"

# Items to display in the inventory
items:
  # Background glass pane
  - row: 1
    col: 1
    rowSpan: 3
    colSpan: 9
    item:
      typeName: "minecraft:black_stained_glass_pane"
      displayName: " "
  
  # Welcome item
  - row: 2
    col: 5
    item:
      typeName: "minecraft:diamond"
      displayName: "&bWelcome, %shygui_player_name%!"
      lore:
        - "&7This is your first GUI"
        - "&7Click to get a message"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui message &aHello from your GUI!"
  
  # Close button
  - row: 3
    col: 9
    item:
      typeName: "minecraft:barrier"
      displayName: "&cClose"
      lore:
        - "&7Click to close this menu"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui close"
```

## Step 3: Load and Test Your GUI

### Reload the Plugin

After making changes to any `.yml` file, always reload:
```
/shygui reload
```

### Open Your GUI

Test your new GUI with:
```
/shygui open my_first_menu
```

Or if you added a custom command:
```
/mymenu
```

### Troubleshooting

If your GUI doesn't open:
1. **Check console logs** for YAML parsing errors
2. **Verify file name** matches the `name` field in the YAML
3. **Ensure proper indentation** (use spaces, not tabs)
4. **Validate YAML syntax** using an online YAML validator

## Step 4: Understanding GUI Components

### Item Positioning

Items are positioned using a row/column grid system:
- **Rows**: 1-6 (depending on windowType)
- **Columns**: 1-9 (standard inventory width)
- **Spanning**: Use `rowSpan` and `colSpan` for multi-slot items

```yaml
- row: 2      # Second row
  col: 3      # Third column  
  rowSpan: 2  # Covers 2 rows (2 and 3)
  colSpan: 3  # Covers 3 columns (3, 4, and 5)
```

### Item Properties

Essential item properties:

```yaml
item:
  typeName: "minecraft:diamond_sword"    # Material name
  durability: 0                          # Item damage/durability
  displayName: "&bMy Sword"             # Display name with color codes
  amount: 1                             # Stack size (1-64)
  lore:                                 # Description lines
    - "&7This is a custom sword"
    - "&7Click to use"
  skinBase64: "eyJ0ZXh0dXJl..."        # Custom head texture (for player_head items)
```

### Command Types

When players click items, you can execute different command types:

```yaml
commands:
  - type: "PER_PLAYER"          # Runs as the clicking player
    command: "/heal"
  - type: "SERVER_PER_PLAYER"   # Runs from server console for the player
    command: "/give %shygui_player_name% diamond 1"
  - type: "SERVER"              # Runs from server console
    command: "/broadcast %shygui_player_name% opened the GUI!"
```

## Step 5: Real-World Example - Pet Management GUI

Let's examine the included PetBlocks main menu to understand advanced features:

### Dynamic Content Display

```yaml
# Shows the currently selected pet
- row: 1
  col: 5
  item:
    typeName: "%petblocks_pet_itemType_selected%"
    durability: "%petblocks_pet_itemDurability_selected%"
    displayName: "%petblocks_pet_displayName_selected%"
    skinBase64: "%petblocks_pet_itemHeadBase64_selected%"
```

### Conditional Items

```yaml
# Enable pet button - only shows when pet is disabled
- row: 2
  col: 5
  item:
    typeName: "minecraft:player_head"
    displayName: "&aPet enabled"
    skinBase64: "eyJ0ZXh0dXJl..."
  condition:
    script: '"%petblocks_pet_isSpawned_selected%" == "true"'
  commands:
    - type: "PER_PLAYER"
      command: "/petblocks despawn %petblocks_pet_name_selected%"

# Different item for the same position when pet is disabled
- row: 2
  col: 5
  item:
    typeName: "minecraft:player_head"
    displayName: "&cPet disabled" 
    skinBase64: "eyJ0ZXh0dXJl..."
  condition:
    script: '"%petblocks_pet_isSpawned_selected%" == "false"'
  commands:
    - type: "PER_PLAYER"
      command: "/petblocks spawn %petblocks_pet_name_selected%"
```

### Navigation Between GUIs

```yaml
# Navigation to sub-menu
- row: 2
  col: 2
  item:
    typeName: "minecraft:player_head"
    displayName: "&6Pet Skins"
    lore:
      - "&7Click to open the pet skins menu"
  commands:
    - type: "PER_PLAYER"
      command: "/shygui next petblocks_skins_menu"  # Opens sub-menu

# Back button in sub-menu
- row: 6
  col: 5
  item:
    typeName: "minecraft:barrier"
    displayName: "&cBack"
  commands:
    - type: "PER_PLAYER"
      command: "/shygui back"  # Returns to previous menu
```

## Step 6: Advanced Features

### Using Placeholders

ShyGUI provides built-in placeholders:

```yaml
displayName: "Welcome, %shygui_player_name%!"
lore:
  - "&7Current GUI: %shygui_gui_name%"
  - "&7Parameter 1: %shygui_gui_param1%"
```

### GUI Parameters

Pass parameters when opening GUIs:

```bash
# Opens GUI with parameters that can be accessed via %shygui_gui_param1%, %shygui_gui_param2%
/shygui open my_menu player123 diamond
```

### Scripting Conditions

Use the built-in scripting language for complex conditions:

```yaml
condition:
  script: 'CONTAINS_IGNORE_CASE("%shygui_player_name%", "admin") && LENGTH("%shygui_gui_param1%") > 0'
```

### Auto-Refresh

Set GUI to automatically update:

```yaml
# Updates every 5 seconds (100 ticks)
updateIntervalTicks: 100
```

## Next Steps

1. **Explore Examples**: Study the included PetBlocks GUIs for advanced patterns
2. **Learn Scripting**: Read the [Scripting Language](script.md) documentation
3. **Use Placeholders**: Check [Placeholders](placeholders.md) for available variables
4. **Set Permissions**: Configure access using [Permissions](permission.md)
5. **API Integration**: For plugin developers, see [API Documentation](api.md)

## Common Patterns

### Shop Menu with Categories

```yaml
name: "shop_main"
windowType: "SIX_ROW"
title: "&6Server Shop"

items:
  # Category: Blocks
  - row: 2
    col: 2
    item:
      typeName: "minecraft:stone"
      displayName: "&eBlocks"
      lore:
        - "&7Click to browse blocks"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next shop_blocks"
  
  # Category: Tools  
  - row: 2
    col: 4
    item:
      typeName: "minecraft:diamond_pickaxe"
      displayName: "&eTools"
      lore:
        - "&7Click to browse tools"
    commands:
      - type: "PER_PLAYER"
        command: "/shygui next shop_tools"
```

### Player Information Display

```yaml
name: "player_info"
windowType: "THREE_ROW"
title: "&9Player Information"

items:
  # Player head
  - row: 1
    col: 5
    item:
      typeName: "minecraft:player_head"
      displayName: "&b%shygui_player_name%"
      lore:
        - "&7Display Name: %shygui_player_displayName%"
        - "&7Online Since: %player_first_join_date%"
        - "&7Playtime: %statistic_time_played%"
```

This concludes the getting started guide. You now have the foundation to create powerful, interactive GUI menus with ShyGUI!