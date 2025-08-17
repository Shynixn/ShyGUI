# How to create a new GUI

### Confirming that the plugin works

1. Take a look at the provided sample GUIs in the ``/plugins/ShyGUI/gui`` directory.
2. Join your server and execute ``/shygui open simple_sample_menu`` to open the example gui. A GUI should open.
3. After confirming that the plugin works as expected, you can start by playing around with the available options.

### Creating your first menu

1. Copy the ``simple_sample_menu.yml`` and rename it to ``my_first_menu.yml``.
2. Open the ``my_first_menu.yml`` in an editor like VSCode or NotePad++
3. At the top of the file change the unique identifier of the GUI to 

```
name: "my_first_menu.yml"
```

4. Join your server and execute the following command. This command should always be executed after you have made changes in your ``.yml`` files.

```
/shygui reload
```

5. Execute the following command to open your GUI.

```
/shygui open my_first_menu
```

6. The GUI should now open for you. If it does not, check your console log for yaml file parsing errors. Correct your ``my_first_menu.yml`` or start over by deleting it.


### Attaching a custom command to your menu

1. Let's assume you want to open the GUI with the command ``/mycoolgui``.
2. Edit the command section in your ``my_first_menu.yml`` to your needs.

```
command: "mycoolgui"
```

### Customizing the GUI menu

1. There are many different ways to customize the GUI. Like displaying the items, executing certain commands and hiding/showing items on certain conditions.
2. Please take a look into the ``simple_sample_menu.yml`` file again. Each available options is explained with a short comments. 
3. If the comment mentiones ``Required.`` You need to set this property. If it says ``Optional`` you do not need to set it.
4. As an easy test, locate the ``minecraft:gold_block`` in your ``my_first_menu.yml`` and replace it with ``minecraft:diamond_block``. After you gave done that simply execute ``/shygui reload`` again and open the GUI ``/mycoolgui``.