# Developer API

ShyGUI provides a comprehensive Developer API for integrating with other plugins. The API allows you to programmatically create, modify, and control GUIs from your Java plugins.

## Installation

Currently, the ShyGUI API is not published to Maven Central. You need to directly reference the ShyGUI.jar file.

### Plugin Dependencies

Add ShyGUI as a soft dependency in your `plugin.yml`:

```yaml
name: YourPlugin
version: 1.0.0
main: com.yourplugin.YourPlugin
softdepend: [ ShyGUI ]
```

### Maven/Gradle Setup

**Maven:**
```xml
<dependency>
    <groupId>local</groupId>
    <artifactId>ShyGUI</artifactId>
    <version>1.0.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/libs/ShyGUI.jar</systemPath>
</dependency>
```

**Gradle:**
```gradle
dependencies {
    compileOnly files('libs/ShyGUI.jar')
}
```

## API Overview

The ShyGUI API is centered around the `GUIMenuService` which provides all core functionality for GUI management.

### Getting the Service

```java
import com.github.shynixn.shygui.api.bukkit.GUIMenuService;
import org.bukkit.Bukkit;

public class YourPlugin extends JavaPlugin {
    private GUIMenuService guiMenuService;
    
    @Override
    public void onEnable() {
        // Get the GUIMenuService instance
        guiMenuService = Bukkit.getServicesManager().load(GUIMenuService.class);
        
        if (guiMenuService == null) {
            getLogger().warning("ShyGUI not found! Disabling plugin.");
            getPluginLoader().disablePlugin(this);
            return;
        }
        
        getLogger().info("ShyGUI API loaded successfully!");
    }
}
```

## Basic Usage Examples

### Opening a GUI for a Player

```java
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public void openGUIForPlayer(Player player, String guiName) {
    // Get the GUI thread executor for thread safety
    Executor guiExecutor = guiMenuService.getExecutor();
    
    // Open GUI asynchronously
    CompletableFuture.runAsync(() -> {
        guiMenuService.getAllGUIMetas() // Get all available GUI metadata
            .thenAcceptAsync(metas -> {
                // Find the GUI by name
                metas.stream()
                    .filter(meta -> meta.getName().equals(guiName))
                    .findFirst()
                    .ifPresent(meta -> {
                        // Open the GUI with no parameters
                        guiMenuService.openGUIAsync(player, meta, new String[0]);
                    });
            }, guiExecutor);
    }, guiExecutor);
}
```

### Opening a GUI with Parameters

```java
public void openShopGUI(Player player, String category, int page) {
    Executor guiExecutor = guiMenuService.getExecutor();
    
    CompletableFuture.runAsync(() -> {
        guiMenuService.getAllGUIMetas()
            .thenAcceptAsync(metas -> {
                metas.stream()
                    .filter(meta -> meta.getName().equals("shop_menu"))
                    .findFirst()
                    .ifPresent(meta -> {
                        // Pass parameters that will be available as %shygui_gui_param1%, %shygui_gui_param2%
                        String[] parameters = {category, String.valueOf(page)};
                        guiMenuService.openGUIAsync(player, meta, parameters);
                    });
            }, guiExecutor);
    }, guiExecutor);
}
```

### Checking if a GUI Exists

```java
public void checkGUIExists(String guiName, Consumer<Boolean> callback) {
    Executor guiExecutor = guiMenuService.getExecutor();
    
    CompletableFuture.runAsync(() -> {
        guiMenuService.getAllGUIMetas()
            .thenAcceptAsync(metas -> {
                boolean exists = metas.stream()
                    .anyMatch(meta -> meta.getName().equals(guiName));
                
                // Execute callback on main thread
                Bukkit.getScheduler().runTask(this, () -> callback.accept(exists));
            }, guiExecutor);
    }, guiExecutor);
}
```

## Thread Safety

⚠️ **Important**: Always use the GUI executor when working with ShyGUI APIs to ensure thread safety:

```java
// CORRECT - Using GUI executor
Executor guiExecutor = guiMenuService.getExecutor();
CompletableFuture.runAsync(() -> {
    // GUI operations here
}, guiExecutor);

// INCORRECT - Running on main thread or other threads
guiMenuService.getAllGUIMetas(); // This could cause issues
```
## Best Practices

1. **Always use the GUI executor** for thread safety
2. **Handle errors gracefully** with try-catch blocks and `.exceptionally()`
3. **Return to main thread** for Bukkit API calls outside of GUI operations
4. **Cache GUIMenuService** reference instead of repeatedly fetching it
5. **Use meaningful parameter names** in your documentation
6. **Validate permissions** before opening admin GUIs
7. **Log important operations** for debugging purposes
