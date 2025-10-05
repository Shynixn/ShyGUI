package com.github.shynixn.shygui.entity

import com.github.shynixn.shygui.enumeration.Permission

class ShyGUISettings {
    var guis: List<Pair<String, String>> = listOf(
        "gui/simple_sample_menu.yml" to "simple_sample_menu.yml"
    )
    var commandPermission: String = Permission.COMMAND.text
    var otherPlayerPermission: String = Permission.OTHER_PLAYER.text
    var guiPermission: String = Permission.DYN_OPEN.text
    var baseCommand: String = "shygui"
    var aliasesPath: String = "commands.shygui.aliases"
    var serverPermission : String = Permission.SERVER.text
    var refreshPermission : String = Permission.REFRESH.text
}
