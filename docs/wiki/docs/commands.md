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
/shygui open <name> [argument/player...]
```

Starts a new GUI session and opens the GUI with the given name for the executing player. If any other GUIs were open when executing this command, they get discarded from the navigation history.

* Name: Identifier of a GUI menu
* Argument/Player: Optional arguments to provide the GUI with additional arguments e.g. ```/shygui open simple_sample_menu 123456 Pikachu```. Here the placeholder ``%shygui_gui_param1%`` is now ``123456`` and the GUI is opened for player ``Pikachu``. The last argument may or may not be a player name.

### /shygui next

```
/shygui next <name> [argument/player...]
```

Reuses the existing GUI session (or starts a new one if it is not available) and opens the GUI with the given name for the executing player. If any other GUIs were open when executing this command, they get put into the navigation history. Executing ``/shygui back`` reopens the previous GUI.

* Name: Identifier of a GUI menu
* Argument/Player: Optional arguments to provide the GUI with additional arguments e.g. ```/shygui open simple_sample_menu 123456 Pikachu```. Here the placeholder ``%shygui_gui_param1%`` is now ``123456`` and the GUI is opened for player ``Pikachu``. The last argument may or may not be a player name.

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
