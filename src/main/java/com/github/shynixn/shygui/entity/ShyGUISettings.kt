package com.github.shynixn.shygui.entity

import com.github.shynixn.shygui.enumeration.Permission

class ShyGUISettings {
    var guis: List<Pair<String, String>> = listOf(
        "gui/petblocks_main_menu.yml" to "petblocks_main_menu.yml",
        "gui/petblocks_skins_menu.yml" to "petblocks_skins_menu.yml",
        "gui/petblocks_skins_blockskins_menu.yml" to "petblocks_skins_blockskins_menu.yml",
        "gui/petblocks_skins_petskins_menu.yml" to "petblocks_skins_petskins_menu.yml",
        "gui/petblocks_skins_plushieskins_menu.yml" to "petblocks_skins_plushieskins_menu.yml",
        "gui/petblocks_skins_vehicleskins_menu.yml" to "petblocks_skins_vehicleskins_menu.yml",
        "gui/simple_sample_menu.yml" to "simple_sample_menu.yml"
    )
    var embedded: String = "ShyGUI"
    var commandPermission: String = Permission.COMMAND.text
    var otherPlayerPermission: String = Permission.OTHER_PLAYER.text
    var guiPermission: String = Permission.DYN_OPEN.text
    var baseCommand: String = "shygui"
    var aliasesPath: String = "commands.shygui.aliases"
}
