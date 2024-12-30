package com.github.shynixn.shygui

import com.github.shynixn.mcutils.common.language.LanguageItem
import com.github.shynixn.shygui.contract.ShyGUILanguage

class ShyGUILanguageImpl : ShyGUILanguage {
 override val names: List<String>
  get() = listOf("en_us", "es_es", "zh_cn")
 override var playerNotFoundMessage = LanguageItem("[&9ShyGUI&f] &cPlayer %1$1s not found.")

 override var commandNoPermission = LanguageItem("[&9ShyGUI&f] &cYou do not have permission to execute this command.")

 override var commandUsage = LanguageItem("[&9ShyGUI&f] Use /shygui help to see more info about the plugin.")

 override var commandDescription = LanguageItem("[&9ShyGUI&f] All commands for the ShyGUI plugin.")

 override var commandSenderHasToBePlayer = LanguageItem("[&9ShyGUI&f] The command sender has to be a player if you do not specify the optional player argument.")

 override var guiMenuNotFoundMessage = LanguageItem("[&9ShyGUI&f] &cMenu %1$1s not found.")

 override var guiMenuNoPermissionMessage = LanguageItem("[&9ShyGUI&f] &cYou do not have permission for menu %1$1s.")

 override var manipulateOtherPlayerMessage = LanguageItem("[&9ShyGUI&f] &cYou do not have permission to open the GUI menu for other players.")

 override var closeCommandHint = LanguageItem("Closes the GUI menu.")

 override var backCommandHint = LanguageItem("Goes back one GUI page.")

 override var openCommandHint = LanguageItem("Opens the GUI menu with the given name.")

 override var nextCommandHint = LanguageItem("Opens the next GUI page.")

 override var reloadCommandHint = LanguageItem("Reloads all GUI menus and configuration.")

 override var messageCommandHint = LanguageItem("Sends a chat message.")

 override var reloadMessage = LanguageItem("[&9ShyGUI&f] Reloaded all GUI menus and configuration.")

 override var cannotParseItemStackError = LanguageItem("[&9ShyGUI&f] &cCannot parse ItemStack at row %1$1s and col %2$1s! Check your GUI menu configuration by reviewing the full item parsing error in the console log.")

 override var rowColOutOfRangeError = LanguageItem("[&9ShyGUI&f] &cThe specified row %1$1s and col %2$1s are out of range of the GUI! Check your GUI menu configuration.")
}
