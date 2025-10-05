# ShyGUI

ShyGUI is a high performance and asynchronous GUI plugin for Bukkit and Folia based Minecraft servers. Create immersive, interactive inventory menus with advanced scripting capabilities, multi-page navigation, and seamless PlaceHolderAPI integration.

## Features

* **Easy GUI Creation**: Build complex GUI menus with multiple sub-pages and custom items using simple YAML configuration
* **Advanced Scripting**: Built-in scripting language for dynamic content, conditional displays, and complex logic
* **PlaceHolderAPI Integration**: Full support for PlaceHolderAPI placeholders and custom ShyGUI placeholders
* **Multi-Server Compatible**: Works seamlessly with both Bukkit and Folia server implementations
* **Asynchronous Performance**: Runs asynchronously to prevent server lag and maintain optimal performance
* **Wide Version Support**: Compatible with Minecraft versions 1.8.R1 through 1.21.R6
* **Navigation System**: Built-in back/forward navigation between GUI pages
* **Dynamic Updates**: Auto-refresh GUI content with configurable intervals
* **Command Integration**: Execute commands, send messages, and trigger server actions from GUI interactions

## Quick Start

1. **Installation**: Place `ShyGUI.jar` in your server's `plugins` folder
2. **First Launch**: Restart your server to generate default configuration files
3. **Test Installation**: Run `/shygui open simple_sample_menu` to verify the plugin works
4. **Create Your GUI**: Copy and modify the sample files in `/plugins/ShyGUI/gui/` directory

## Example Use Cases

* **Pet Management**: Create pet spawn/despawn menus with skin selection (see included PetBlocks examples)
* **Shop Systems**: Build interactive shop interfaces with category navigation
* **Admin Panels**: Create server management interfaces with permission-based access
* **Player Profiles**: Display player statistics and achievements
* **Server Navigation**: Multi-server teleportation menus for network servers

## Documentation Structure

* **[Getting Started](gettingstarted.md)**: Step-by-step guide to creating your first GUI
* **[Commands](commands.md)**: Complete command reference and usage examples
* **[API Documentation](api.md)**: Developer API for plugin integration
* **[Scripting Language](script.md)**: Comprehensive scripting reference
* **[Placeholders](placeholders.md)**: Available placeholder variables
* **[Permissions](permission.md)**: Permission system and security