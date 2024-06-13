# Commands

ShyGUI contains many commands to control the state of the GUI on your server, which may be executed by **players**, **console**,  **other plugins**, **command blocks**.


## Individual commands

You can create individual commands like ``/mycoolgui`` to open your created GUI by attaching it to a single GUI page. Open your created ``menu.yml``
and specify the command in the ``command`` section. 

* This command does not have any arguments and simply opens the GUI when being executed by a player. 
* For server automation and integration into other plugins, use the ``/shygui`` command as explained below.

## Command /shygui


**Required Permission:**
``
shygui.command
``

### /shygui open

```
/shygui open <name> [arguments.../player]
```

Starts a new GUI session and opens the GUI with the given name for the executing player. If any other GUIs were open when executing this command, they get discarded from the navigation history.

* Name: Identifier of a GUI menu
* Argument/Player: Optional arguments to provide the GUI with additional arguments and optionally a player to open the GUI for.

Samples:

* Opens the inventory for the executing player.

```
/shygui open simple_sample_menu
```

* Opens the inventory for the player named "Pikachu".
* The slash separates arguments with the player name (in this case there are 0 arguments)

```
/shygui open simple_sample_menu / Pikachu
```

* Opens the inventory with additional arguments which can be accessed via the placeholders. ``%shygui_gui_param1%`` is now ``123456``.

```
/shygui open simple_sample_menu 123456
```

* Opens the inventory with additional arguments which can be accessed via the placeholders for the player named "Pikachu". ``%shygui_gui_param1%`` is now ``123456`` ``%shygui_gui_param2%`` is now ``abcde``.
* The slash separates arguments with the player name

```
/shygui open simple_sample_menu 123456 abcde / Pikachu
```

### /shygui next

```
/shygui next <name> [arguments.../player]
```

Reuses the existing GUI session (or starts a new one if it is not available) and opens the GUI with the given name for the executing player. If any other GUIs were open when executing this command, they get put into the navigation history. Executing ``/shygui back`` reopens the previous GUI.

* Name: Identifier of a GUI menu
* Argument/Player: Optional arguments to provide the GUI with additional arguments. See the open command for samples.

### /shygui back

```
/shygui back [player]
```

Checks if the current GUI session contains a previously opened GUI. If that is the case, the previous GUI is opened and the current GUI is discarded. If not GUI is found, the current GUI is simply closed.

* Player: Optional player argument to execute the action for another player.

### /shygui close

```
/shygui close [player]
```

Closes the current GUI and clears the GUI session and navigation history.

* Player: Optional player argument to execute the action for another player.

### /shygui reload

```
/shygui reload
```

Reloads all GUI meta data and configuration files of the plugin ShyGUI.
