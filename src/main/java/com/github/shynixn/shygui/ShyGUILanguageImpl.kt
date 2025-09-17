package com.github.shynixn.shygui

import com.github.shynixn.mcutils.common.language.LanguageItem
import com.github.shynixn.shygui.contract.ShyGUILanguage

class ShyGUILanguageImpl : ShyGUILanguage {
 override val names: List<String>
  get() = listOf("en_us")
 override var shyGuiPlayerNotFoundMessage = LanguageItem("[&9ShyGUI&f] &cPlayer %shygui_param_1% not found.")

 override var shyGuiNoPermissionCommand = LanguageItem("[&9ShyGUI&f] &cYou do not have permission to execute this command.")

 override var shyGuiCommandUsage = LanguageItem("[&9ShyGUI&f] Use /shygui help to see more info about the plugin.")

 override var shyGuiCommandDescription = LanguageItem("[&9ShyGUI&f] All commands for the ShyGUI plugin.")

 override var shyGuiCommandSenderHasToBePlayer = LanguageItem("[&9ShyGUI&f] The command sender has to be a player if you do not specify the optional player argument.")

 override var shyGuiGuiMenuNotFoundMessage = LanguageItem("[&9ShyGUI&f] &cMenu %shygui_param_1% not found.")

 override var shyGuiGuiMenuNoPermissionMessage = LanguageItem("[&9ShyGUI&f] &cYou do not have permission for menu %shygui_param_1%.")

 override var shyGuiManipulateOtherMessage = LanguageItem("[&9ShyGUI&f] &cYou do not have permission to open the GUI menu for other players.")

 override var shyGuiCloseCommandHint = LanguageItem("Closes the GUI menu.")

 override var shyGuiBackCommandHint = LanguageItem("Goes back one GUI page.")

 override var shyGuiOpenCommandHint = LanguageItem("Opens the GUI menu with the given name.")

 override var shyGuiNextCommandHint = LanguageItem("Opens the next GUI page.")

 override var shyGuiReloadCommandHint = LanguageItem("Reloads all GUI menus and configuration.")

 override var shyGuiMessageCommandHint = LanguageItem("Sends a chat message.")

 override var shyGuiReloadMessage = LanguageItem("[&9ShyGUI&f] Reloaded all GUI menus and configuration.")

 override var shyGuiCannotParseItemStackError = LanguageItem("[&9ShyGUI&f] &cCannot parse ItemStack at row %shygui_param_1% and col %shygui_param_2%! Check your GUI menu configuration by reviewing the full item parsing error in the console log.")

 override var shyGuiRowColOutOfRangeError = LanguageItem("[&9ShyGUI&f] &cThe specified row %shygui_param_1% and col %shygui_param_2% are out of range of the GUI! Check your GUI menu configuration.")

 override var shyGuiRefreshCommandHint = LanguageItem("Refreshes the current GUI page.")

 override var shyGuiServerCommandHint = LanguageItem("Sends the player to the given server.")

 override var shyGuiServerMessage = LanguageItem("[&9ShyGUI&f] Connecting to server '%shygui_param_1%' ...")
}
