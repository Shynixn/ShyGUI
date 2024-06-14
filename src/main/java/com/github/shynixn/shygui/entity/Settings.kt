package com.github.shynixn.shygui.entity

abstract class Settings {
    var commandPermission: String = ""
    var otherPlayerPermission: String = ""
    var guiPermission: String = ""
    var baseCommand: String = ""
    var aliasesPath: String = ""
    var commandUsage: String = ""
    var commandDescription: String = ""

    var playerNotFoundMessage: String = ""
    var commandSenderHasToBePlayerMessage: String = ""
    var manipulateOtherMessage: String = ""
    var reloadMessage: String = ""
    var noPermissionMessage: String = ""
    var guiNotFoundMessage: String = ""
    var guiMenuNoPermissionMessage : String = ""

    var reloadCommandHint: String = ""
    var openCommandHint: String = ""
    var nextCommandHint: String = ""
    var closeCommandHint: String = ""
    var backCommandHint: String = ""
    var messageCommandHint: String = ""

    /**
     * Reloads the settings.
     */
    abstract fun reload()
}
