# Api

ShyGUI offers a Developer Api, however it is not published to Maven Central or any other distribution system yet.
You need to directly reference the ShyGUI.jar file.

## Usage

Add a dependency in your plugin.yml

```yaml
softdepend: [ ShyGUI ]
```

Take a look at the following example:
```java
public class YourPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // Always gets the same instance of the GUIMenuService.
        GUIMenuService guiMenuService = Bukkit.getServicesManager().load(GUIMenuService.class);
        Player player = Bukkit.getPlayer("YourPlayerName");
        Plugin plugin = this;

        // ShyGUI uses its own main thread to perform actions, to avoid concurrency problems, access it with the executor.
        Executor guiThreadExecutor = guiMenuService.getExecutor();

        CompletableFuture.runAsync(() -> guiMenuService.getAllGUIMetas() // Retrieve all GUI Metadata on the GUI Thread.
                .thenAcceptAsync(metas -> metas.stream().filter(e -> e.getName().equals("simple_sample_menu")).findFirst() // Filter it to find the meta with the given name.
                        .ifPresent(meta -> guiMenuService.openGUIAsync(player, meta, new String[0])), guiThreadExecutor), guiThreadExecutor); // Open the gui with the located meta on the GUI Thread.
    }
}
```
