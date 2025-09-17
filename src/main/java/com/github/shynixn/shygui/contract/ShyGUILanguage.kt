package com.github.shynixn.shygui.contract

import com.github.shynixn.mcutils.common.language.LanguageItem
import com.github.shynixn.mcutils.common.language.LanguageProvider

interface ShyGUILanguage : LanguageProvider {
  var shyGuiPlayerNotFoundMessage: LanguageItem

  var shyGuiNoPermissionCommand: LanguageItem

  var shyGuiCommandUsage: LanguageItem

  var shyGuiCommandDescription: LanguageItem

  var shyGuiCommandSenderHasToBePlayer: LanguageItem

  var shyGuiGuiMenuNotFoundMessage: LanguageItem

  var shyGuiGuiMenuNoPermissionMessage: LanguageItem

  var shyGuiManipulateOtherMessage: LanguageItem

  var shyGuiCloseCommandHint: LanguageItem

  var shyGuiBackCommandHint: LanguageItem

  var shyGuiOpenCommandHint: LanguageItem

  var shyGuiNextCommandHint: LanguageItem

  var shyGuiReloadCommandHint: LanguageItem

  var shyGuiMessageCommandHint: LanguageItem

  var shyGuiReloadMessage: LanguageItem

  var shyGuiCannotParseItemStackError: LanguageItem

  var shyGuiRowColOutOfRangeError: LanguageItem

  var shyGuiRefreshCommandHint: LanguageItem

  var shyGuiServerCommandHint: LanguageItem

  var shyGuiServerMessage: LanguageItem
}
