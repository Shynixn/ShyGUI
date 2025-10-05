# Placeholders Reference

ShyGUI provides a rich set of placeholder variables that can be used throughout your GUI configurations. These placeholders are dynamically resolved when the GUI is displayed or refreshed, allowing for dynamic and personalized content.

## ShyGUI Built-in Placeholders

These placeholders are provided directly by ShyGUI and are always available:

| Placeholder | Description | Example Value |
|-------------|-------------|---------------|
| `%shygui_player_name%` | Username of the player viewing the GUI | `"Steve"` |
| `%shygui_player_displayName%` | Display name of the player (may include colors/formatting) | `"&a[VIP] Steve"` |
| `%shygui_gui_name%` | Name/identifier of the current GUI | `"shop_main"` |
| `%shygui_gui_backName%` | Name of the previous GUI in navigation history | `"main_menu"` |
| `%shygui_gui_param1%` | First parameter passed to the GUI | `"weapons"` |
| `%shygui_gui_param2%` | Second parameter passed to the GUI | `"page1"` |
| `%shygui_gui_param3%` | Third parameter passed to the GUI | `"rare"` |
| `%shygui_gui_param4%` | Fourth parameter passed to the GUI | `"123"` |
| `%shygui_gui_param5%` | Fifth parameter passed to the GUI | `"true"` |
| `%shygui_gui_param6%` | Sixth parameter passed to the GUI | `"admin"` |
| `%shygui_gui_param7%` | Seventh parameter passed to the GUI | `"data"` |
| `%shygui_gui_param8%` | Eighth parameter passed to the GUI | `"extra"` |
| `%shygui_gui_param9%` | Ninth parameter passed to the GUI | `"final"` |

## PlaceholderAPI Integration

ShyGUI fully supports PlaceholderAPI, giving you access to hundreds of additional placeholders from other plugins. Simply use any PlaceholderAPI placeholder in your GUI configurations.

### Common PlaceholderAPI Examples

| Category | Placeholder | Description |
|----------|-------------|-------------|
| **Player Info** | `%player_name%` | Player's username |
| | `%player_displayname%` | Player's display name |
| | `%player_uuid%` | Player's unique identifier |
| | `%player_world%` | Current world name |
| | `%player_gamemode%` | Current gamemode |
| **Economy** | `%vault_eco_balance%` | Player's money balance |
| | `%vault_eco_balance_formatted%` | Formatted balance with currency |
| **Statistics** | `%statistic_time_played%` | Total playtime |
| | `%statistic_player_kills%` | Player kills |
| | `%statistic_deaths%` | Death count |
| **Permissions** | `%luckperms_primary_group_name%` | Player's primary group |
| | `%vault_group%` | Permission group |

## Using Placeholders in GUI Configurations

### Basic Usage Examples

#### Player Personalization

```yaml
# Welcome message with player name
displayName: "&bWelcome, %shygui_player_name%!"

# Show player's balance
lore:
  - "&7Your balance: &a$%vault_eco_balance%"
  - "&7World: &e%player_world%"
  - "&7Rank: &6%vault_group%"
```

#### Parameter-Based Content

```yaml
# Using GUI parameters for dynamic content
displayName: "&e%shygui_gui_param1% Shop"  # "Weapons Shop" if param1 = "Weapons"

# Category-specific lore
lore:
  - "&7Category: &b%shygui_gui_param1%"     # Category from parameter
  - "&7Page: &e%shygui_gui_param2%"        # Page number from parameter
  - "&7Filter: &d%shygui_gui_param3%"      # Filter type from parameter
```

#### Navigation Information

```yaml
# Back button showing previous menu
displayName: "&7← Back to %shygui_gui_backName%"

# Current location breadcrumb  
lore:
  - "&7Location: &e%shygui_gui_backName% &7> &e%shygui_gui_name%"
```

### Real-World Examples from PetBlocks GUI

#### Dynamic Pet Display

```yaml
# Shows the currently selected pet using external placeholders
- row: 1
  col: 5
  item:
    typeName: "%petblocks_pet_itemType_selected%"           # Dynamic item type
    durability: "%petblocks_pet_itemDurability_selected%"   # Dynamic durability
    displayName: "%petblocks_pet_displayName_selected%"     # Dynamic name
    skinBase64: "%petblocks_pet_itemHeadBase64_selected%"   # Dynamic head texture
```

#### Conditional Content Based on State

```yaml
# Pet control button - text changes based on pet state
displayName: "&aPet enabled"
lore:
  - "&7Owner: &e%petblocks_owner_name%"
  - "&7Status: %petblocks_pet_isSpawned_selected%"
  - "&7Mounted: %petblocks_pet_isMounted_selected%"
```

#### Command Integration with Placeholders

```yaml
commands:
  - type: "SERVER_PER_PLAYER"
    # Uses placeholders in the command itself
    command: "/petblocks spawn %petblocks_pet_name_selected%"
  - type: "PER_PLAYER"  
    # Message with placeholder
    command: "/shygui message &aSpawned pet: %petblocks_pet_displayName_selected%"
```

## Advanced Placeholder Usage

### Conditional Display with Scripting

Placeholders can be used within conditional scripts:

```yaml
condition:
  # Only show if player has enough money
  script: 'NUMBER("%vault_eco_balance%") >= 1000'

# Or check player permissions
condition:
  script: 'CONTAINS_IGNORE_CASE("%vault_group%", "vip")'

# Or verify player state
condition:
  script: '"%player_gamemode%" == "SURVIVAL"'
```

### Complex Parameter Combinations

```yaml
# Shop GUI that uses multiple parameters
# Called with: /shygui open advanced_shop weapons swords 1 rare steve

items:
  - row: 1
    col: 5
    item:
      typeName: "minecraft:diamond_sword"
      displayName: "&e%shygui_gui_param1% - %shygui_gui_param2%"  # "weapons - swords"
      lore:
        - "&7Category: &b%shygui_gui_param1%"    # "weapons"
        - "&7Subcategory: &d%shygui_gui_param2%" # "swords"  
        - "&7Page: &e%shygui_gui_param3%"       # "1"
        - "&7Rarity: &6%shygui_gui_param4%"    # "rare"
        - "&7For player: &a%shygui_gui_param5%" # "steve"
```

### Dynamic Item Types

```yaml
# Item type determined by parameter
- row: 2
  col: 3
  item:
    # Use parameter to determine item type
    typeName: "minecraft:%shygui_gui_param1%"  # Could be "diamond_sword", "iron_pickaxe", etc.
    displayName: "&eSelected: %shygui_gui_param1%"
```

### Multi-Language Support

```yaml
# Using placeholders for language-specific content
displayName: "%lang_shop_title%"  # Resolves to language-specific title
lore:
  - "%lang_shop_description%"     # Language-specific description
  - "&7Player: %shygui_player_name%"
```

## Placeholder Debugging

### Common Issues and Solutions

**Placeholder not resolving:**
- Ensure the placeholder syntax is correct (`%placeholder%`)
- Verify the required plugin is installed and loaded
- Check if the placeholder exists using `/papi parse <player> <placeholder>`

**Empty/undefined placeholders:**
- Some placeholders may be empty if data doesn't exist
- Use conditional scripts to check for empty values:
  ```yaml
  condition:
    script: 'LENGTH("%vault_eco_balance%") > 0'
  ```

**Parameter placeholders showing incorrectly:**
- Verify parameters are passed in the correct order
- Remember parameters are 1-indexed (`param1`, `param2`, etc.)
- Check the command syntax: `/shygui open menu param1 param2 / player`

### Testing Placeholders

Use PlaceholderAPI's parse command to test placeholders:

```bash
# Test ShyGUI placeholders (must be in a GUI context)
/papi parse Steve %shygui_player_name%

# Test other placeholders
/papi parse Steve %vault_eco_balance%
/papi parse Steve %player_world%
```

## Best Practices

### Performance Considerations

- **Avoid expensive placeholders** in frequently updating GUIs
- **Use appropriate refresh intervals** for dynamic content
- **Cache static content** when possible

### Placeholder Safety

```yaml
# Always provide fallbacks for optional placeholders
displayName: "%custom_title% &7- %shygui_player_name%"

# Use conditions to handle missing data
condition:
  script: '!CONTAINS("%vault_eco_balance%", "vault_eco_balance")'
```

### Naming Conventions

- Use descriptive parameter names in documentation
- Document expected parameter values
- Consider parameter order carefully for usability

### Example: Complete Shop Item

```yaml
# Complete example combining multiple placeholder types
- row: 2
  col: 4
  item:
    typeName: "minecraft:diamond"
    displayName: "&e%shop_item_name% &7(&a$%shop_item_price%&7)"
    amount: "%shop_item_quantity%"
    lore:
      - "&7Category: &b%shygui_gui_param1%"
      - "&7Your balance: &a$%vault_eco_balance%"
      - ""
      - "&7Click to purchase"
      - "&7Buyer: &e%shygui_player_name%"
  condition:
    # Only show if player can afford it
    script: 'NUMBER("%vault_eco_balance%") >= NUMBER("%shop_item_price%")'
  commands:
    - type: "SERVER_PER_PLAYER"
      command: "/shop buy %shop_item_id% %shygui_player_name%"
    - type: "PER_PLAYER"
      command: "/shygui refresh"  # Refresh to update balance
```

This placeholder system provides the foundation for creating highly dynamic and personalized GUI experiences that adapt to player state, server conditions, and external plugin data.