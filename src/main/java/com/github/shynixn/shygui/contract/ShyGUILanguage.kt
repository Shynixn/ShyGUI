package com.github.shynixn.shygui.contract

import com.github.shynixn.mcutils.common.language.LanguageItem
import com.github.shynixn.mcutils.common.language.LanguageProvider

interface ShyGUILanguage : LanguageProvider {
  var playerNotFoundMessage: LanguageItem

  var commandNoPermission: LanguageItem

  var commandUsage: LanguageItem

  var commandDescription: LanguageItem

  var commandSenderHasToBePlayer: LanguageItem

  var guiMenuNotFoundMessage: LanguageItem

  var guiMenuNoPermissionMessage: LanguageItem

  var manipulateOtherPlayerMessage: LanguageItem

  var closeCommandHint: LanguageItem

  var backCommandHint: LanguageItem

  var openCommandHint: LanguageItem

  var nextCommandHint: LanguageItem

  var reloadCommandHint: LanguageItem

  var messageCommandHint: LanguageItem

  var reloadMessage: LanguageItem

  var cannotParseItemStackError: LanguageItem

  var rowColOutOfRangeError: LanguageItem
}
