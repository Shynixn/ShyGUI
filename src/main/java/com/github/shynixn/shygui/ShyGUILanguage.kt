package com.github.shynixn.shygui

object ShyGUILanguage {
  /** [&9ShyGUI&f] &cMenu %1$1s not found. **/
  var guiMenuNotFoundMessage : String = "[&9ShyGUI&f] &cMenu %1$1s not found."

  /** [&9ShyGUI&f] All commands for the ShyGUI plugin. **/
  var commandDescription : String = "[&9ShyGUI&f] All commands for the ShyGUI plugin."

  /** Opens the next GUI page. **/
  var nextCommandHint : String = "Opens the next GUI page."

  /** [&9ShyGUI&f] Reloaded all GUI menus and configuration. **/
  var reloadMessage : String = "[&9ShyGUI&f] Reloaded all GUI menus and configuration."

  /** [&9ShyGUI&f] &cYou do not have permission to execute this command. **/
  var commandNoPermission : String = "[&9ShyGUI&f] &cYou do not have permission to execute this command."

  /** [&9ShyGUI&f] &cCannot parse ItemStack at row %1$1s and col %2$1s! Check your GUI menu configuration by reviewing the full item parsing error in the console log. **/
  var cannotParseItemStackError : String = "[&9ShyGUI&f] &cCannot parse ItemStack at row %1$1s and col %2$1s! Check your GUI menu configuration by reviewing the full item parsing error in the console log."

  /** [&9ShyGUI&f] &cYou do not have permission for menu %1$1s. **/
  var guiMenuNoPermissionMessage : String = "[&9ShyGUI&f] &cYou do not have permission for menu %1$1s."

  /** [&9ShyGUI&f] The command sender has to be a player if you do not specify the optional player argument. **/
  var commandSenderHasToBePlayer : String = "[&9ShyGUI&f] The command sender has to be a player if you do not specify the optional player argument."

  /** Goes back one GUI page. **/
  var backCommandHint : String = "Goes back one GUI page."

  /** Opens the GUI menu with the given name. **/
  var openCommandHint : String = "Opens the GUI menu with the given name."

  /** [&9ShyGUI&f] &cPlayer %1$1s not found. **/
  var playerNotFoundMessage : String = "[&9ShyGUI&f] &cPlayer %1$1s not found."

  /** Reloads all GUI menus and configuration. **/
  var reloadCommandHint : String = "Reloads all GUI menus and configuration."

  /** [&9ShyGUI&f] Use /shygui help to see more info about the plugin. **/
  var commandUsage : String = "[&9ShyGUI&f] Use /shygui help to see more info about the plugin."

  /** Closes the GUI menu. **/
  var closeCommandHint : String = "Closes the GUI menu."

  /** [&9ShyGUI&f] &cThe specified row %1$1s and col %2$1s are out of range of the GUI! Check your GUI menu configuration. **/
  var rowColOutOfRangeError : String = "[&9ShyGUI&f] &cThe specified row %1$1s and col %2$1s are out of range of the GUI! Check your GUI menu configuration."

  /** [&9ShyGUI&f] &cYou do not have permission to open the GUI menu for other players. **/
  var manipulateOtherPlayerMessage : String = "[&9ShyGUI&f] &cYou do not have permission to open the GUI menu for other players."
}
