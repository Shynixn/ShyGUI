# Commands Reference

ShyGUI provides a comprehensive command system for controlling GUIs on your server. Commands can be executed by **players**, **console**, **other plugins**, and **command blocks**.

## Quick Reference

| Command | Description | Permission |
|---------|-------------|------------|
| `/shygui open <name> [args...] [/ player]` | Open a GUI for a player | `shygui.command` |
| `/shygui next <name> [args...] [/ player]` | Navigate to next GUI page | `shygui.command` |
| `/shygui back [player]` | Return to previous GUI | `shygui.command` |
| `/shygui close [player]` | Close current GUI | `shygui.command` |
| `/shygui refresh [player]` | Refresh GUI content | `shygui.refresh` |
| `/shygui server <server> [player]` | Connect to BungeeCord server | `shygui.server` |
| `/shygui message [args...] [/ player]` | Send chat message | `shygui.command` |
| `/shygui reload` | Reload all GUIs | `shygui.refresh` |

## Individual Custom Commands

You can create dedicated commands for specific GUIs by adding a `command` section to your GUI configuration:

```yaml
name: "my_shop"
# ... other GUI settings ...

command:
  command: "shop"              # Creates /shop command
  permission: "myserver.shop"  # Required permission
  usage: "/shop"               # Help text usage
  description: "Opens the server shop"
  aliases:                     # Alternative commands
    - "market"
    - "store"
```

**Example Usage:**
- `/shop` - Opens the shop GUI directly
- `/market` - Same as above (alias)
- No arguments needed, simply opens the GUI

**Benefits:**
- Cleaner user experience (no need for `/shygui open`)
- Custom permissions per GUI
- Easy to remember commands
- Automatic help integration

## Universal Command: `/shygui`

The main `/shygui` command provides full control over the GUI system.

**Base Permission:** `shygui.command`

### `/shygui open`

**Syntax:** `/shygui open <name> [arguments...] [/ player]`

Opens a new GUI session, discarding any existing navigation history.

**Parameters:**
- `<name>` - GUI identifier (from the `name` field in YAML)
- `[arguments...]` - Optional parameters accessible via `%shygui_gui_param1%`, `%shygui_gui_param2%`, etc.
- `[/ player]` - Optional target player (requires `shygui.manipulateother`)

**Examples:**

```bash
# Basic usage - opens petblocks main menu for the executing player
/shygui open petblocks_main_menu

# Opens for another player (admin command)
/shygui open petblocks_main_menu / Steve

# Opens with single parameter (%shygui_gui_param1% = "123456")
/shygui open shop_menu 123456

# Opens with multiple parameters for another player
# %shygui_gui_param1% = "tools", %shygui_gui_param2% = "page1"
/shygui open shop_menu tools page1 / Alex
```

**Real-world scenarios:**
```bash
# Player shop with category pre-selected
/shygui open player_shop weapons / ThatPlayer

# Admin panel with specific server section
/shygui open admin_panel server1 players

# Pet menu with specific pet ID
/shygui open pet_management 12345
```

### `/shygui next`

**Syntax:** `/shygui next <name> [arguments...] [/ player]`

Navigates to a new GUI while preserving navigation history. Players can use `/shygui back` to return.

**Key Difference from `open`:**
- Maintains navigation stack
- Enables back button functionality
- Perfect for multi-page workflows

**Examples:**

```bash
# Navigate from main shop to weapons category
/shygui next shop_weapons

# Navigate with parameters preserved
/shygui next pet_skins premium rare

# Navigate for another player
/shygui next admin_tools ban_management / Moderator
```

**Navigation Flow Example:**
```bash
Player opens: /shygui open main_menu
↓ clicks "Shop" button which runs: /shygui next shop_main
↓ clicks "Weapons" which runs: /shygui next shop_weapons  
↓ clicks "Back" which runs: /shygui back
↑ Returns to shop_main
↓ clicks "Back" again: /shygui back
↑ Returns to main_menu
```

### `/shygui back`

**Syntax:** `/shygui back [player]`

Returns to the previously opened GUI in the navigation history.

**Examples:**

```bash
# Go back to previous GUI
/shygui back

# Send another player back (admin)
/shygui back PlayerName
```

**Behavior:**
- If no previous GUI exists, closes current GUI
- Removes current GUI from navigation stack
- Restores previous GUI with original parameters

### `/shygui close`

**Syntax:** `/shygui close [player]`

Closes the current GUI and clears the entire navigation history.

**Examples:**

```bash
# Close your own GUI
/shygui close

# Close another player's GUI (admin)
/shygui close ThatPlayer
```

### `/shygui refresh`

**Syntax:** `/shygui refresh [player]`

**Permission:** `shygui.refresh`

Refreshes all placeholders and updates the current GUI content without closing it.

**Examples:**

```bash
# Refresh your current GUI
/shygui refresh

# Refresh another player's GUI
/shygui refresh PlayerName
```

**Use Cases:**
- Update dynamic content (player stats, balances, etc.)
- Refresh after external changes
- Manual update when auto-refresh is disabled

### `/shygui server`

**Syntax:** `/shygui server <server> [player]`

**Permission:** `shygui.server`

Connects a player to a different BungeeCord or Velocity proxy server.

**Examples:**

```bash
# Send yourself to lobby server
/shygui server lobby

# Send another player to minigames server
/shygui server minigames PlayerName
```

**Requirements:**
- BungeeCord or Velocity proxy setup
- Server must be registered in proxy configuration

### `/shygui message`

**Syntax:** `/shygui message [arguments...] [/ player]`

Sends a formatted chat message to a player. Supports placeholders and color codes.

**Examples:**

```bash
# Send message to yourself
/shygui message &aWelcome to our server!

# Send message with placeholders
/shygui message &eHello %shygui_player_name%! You have &a$%vault_eco_balance%

# Send message to another player with parameters
/shygui message &cYou have been warned! reason / TargetPlayer
```

**Features:**
- Full color code support (`&a`, `&l`, etc.)
- PlaceHolderAPI integration
- Parameter support like other commands

### `/shygui reload`

**Syntax:** `/shygui reload`

**Permission:** `shygui.refresh`

Reloads all GUI configurations and metadata from disk.

**Example:**

```bash
# Reload all GUIs after making changes
/shygui reload
```

**What gets reloaded:**
- All `.yml` files in the GUI directory
- Plugin configuration files
- Command registrations
- Permission settings

**Important Notes:**
- Always run after editing GUI files
- Players with open GUIs may need to reopen them
- Does not restart the plugin (no need for server restart)

## Command Usage in GUIs

When creating GUI items, you can execute these commands when players click:

```yaml
# Example item that opens a sub-menu
- row: 2
  col: 3
  item:
    typeName: "minecraft:diamond_sword"
    displayName: "&eWeapons Shop"
    lore:
      - "&7Click to browse weapons"
  commands:
    - type: "PER_PLAYER"
      command: "/shygui next shop_weapons swords"

# Example close button
- row: 6
  col: 9
  item:
    typeName: "minecraft:barrier"
    displayName: "&cClose"
  commands:
    - type: "PER_PLAYER"
      command: "/shygui close"

# Example back button
- row: 6
  col: 1
  item:
    typeName: "minecraft:arrow"
    displayName: "&7← Back"
  commands:
    - type: "PER_PLAYER"
      command: "/shygui back"
```

## Advanced Usage Patterns

### Multi-Server GUI Navigation

```yaml
# Server selector GUI item
- row: 2
  col: 4
  item:
    typeName: "minecraft:grass_block"
    displayName: "&aSurvival Server"
    lore:
      - "&7Click to connect to survival"
  commands:
    - type: "PER_PLAYER"
      command: "/shygui message &eTeleporting to survival server..."
    - type: "PER_PLAYER"
      command: "/shygui server survival"
```

### Dynamic Shop with Parameters

```yaml
# Shop category item that passes category to the shop GUI
- row: 2
  col: 2
  item:
    typeName: "minecraft:diamond_pickaxe"
    displayName: "&eTools"
  commands:
    - type: "PER_PLAYER"
      command: "/shygui next shop_items tools 1"  # category = tools, page = 1
```

### Admin Action with Confirmation

```yaml
# Dangerous admin action with confirmation
- row: 3
  col: 5
  item:
    typeName: "minecraft:tnt"
    displayName: "&cRestart Server"
  commands:
    - type: "PER_PLAYER"
      command: "/shygui next admin_confirm_restart"
```

## Error Handling

### Common Issues and Solutions

**GUI doesn't open:**
- Check GUI name spelling
- Verify file exists in `/plugins/ShyGUI/gui/`
- Run `/shygui reload` after changes
- Check console for YAML errors

**Permission errors:**
- Ensure player has `shygui.command` permission
- Check GUI-specific permissions in command section
- Verify `shygui.manipulateother` for admin commands

**Parameters not working:**
- Verify parameter order in command
- Check placeholder spelling (`%shygui_gui_param1%`)
- Remember parameters are 1-indexed, not 0-indexed

### Testing Commands

```bash
# Test basic functionality
/shygui open simple_sample_menu

# Test parameters
/shygui open test_menu param1 param2 param3

# Test admin functionality (as admin)
/shygui open admin_menu / TestPlayer

# Test navigation
/shygui open main_menu
# ... navigate through sub-menus ...
/shygui back
```

This command system provides the foundation for creating complex, interactive GUI workflows that can integrate seamlessly with your server's functionality.