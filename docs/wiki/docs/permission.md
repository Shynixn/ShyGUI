# Permissions System

ShyGUI uses a comprehensive permission system to control access to GUI functions and administrative features. This ensures server security while providing flexibility for different user roles.

## Permission Overview

ShyGUI permissions are organized into two main categories:
- **User Permissions**: Standard permissions for regular players
- **Admin Permissions**: Advanced permissions for server administrators and moderators

## Core Permissions

### User Level Permissions

| Permission | Level | Description |
|------------|-------|-------------|
| `shygui.command` | User | Allows access to basic `/shygui` commands | 
| `shygui.gui.<name>` | User | Allows opening a specific GUI by name | 

### Admin Level Permissions

| Permission | Level | Description |
|------------|-------|-------------|
| `shygui.refresh` | Admin | Allows access to `/shygui refresh` and `/shygui reload` commands |
| `shygui.server` | Admin | Allows access to `/shygui server` command for BungeeCord connections | 
| `shygui.manipulateother` | Admin | Allows performing GUI actions for other players |

## Permission Details

### `shygui.command`
**Level:** User  
**Purpose:** Base permission for ShyGUI functionality

This permission grants access to:
- `/shygui open <name>` - Opening GUIs for yourself
- `/shygui next <name>` - Navigating to next GUI
- `/shygui back` - Returning to previous GUI
- `/shygui close` - Closing current GUI
- `/shygui message` - Sending messages to yourself

**Example Usage:**
```yaml
# Grant to all players
permissions:
  shygui.command:
    default: true
    
# Grant to specific groups
groups:
  default:
    permissions:
      - shygui.command
```

### `shygui.gui.<name>`
**Level:** User  
**Purpose:** Individual GUI access control

Controls access to specific GUIs. Replace `<name>` with the actual GUI identifier.

**Examples:**
- `shygui.gui.shop_main` - Access to shop_main GUI
- `shygui.gui.admin_panel` - Access to admin_panel GUI
- `shygui.gui.player_stats` - Access to player_stats GUI

**Use Cases:**
```yaml
# VIP shop access
groups:
  vip:
    permissions:
      - shygui.gui.vip_shop
      - shygui.gui.premium_features

# Staff panel access
groups:
  moderator:
    permissions:
      - shygui.gui.mod_panel
      - shygui.gui.player_management
```

### `shygui.refresh`
**Level:** Admin  
**Purpose:** Content and configuration management

Grants access to:
- `/shygui refresh [player]` - Refresh GUI content
- `/shygui reload` - Reload all GUI configurations

**Security Note:** This permission allows reloading plugin configurations, which could affect server performance.

### `shygui.server`
**Level:** Admin  
**Purpose:** BungeeCord/Velocity server management

Grants access to:
- `/shygui server <server> [player]` - Connect players to different servers

**Requirements:**
- BungeeCord or Velocity proxy setup
- Proper server network configuration

**Security Note:** This permission allows sending players to different servers, which could be used maliciously.

### `shygui.manipulateother`
**Level:** Admin  
**Purpose:** Administrative control over other players

Allows executing GUI commands for other players:
- `/shygui open <name> / <player>` - Open GUI for another player
- `/shygui close <player>` - Close another player's GUI
- `/shygui refresh <player>` - Refresh another player's GUI
- `/shygui back <player>` - Send another player back in GUI navigation