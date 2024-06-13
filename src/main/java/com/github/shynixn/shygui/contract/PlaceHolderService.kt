package com.github.shynixn.shygui.contract

import org.bukkit.entity.Player

interface PlaceHolderService {
    /**
     * Replaces incoming strings with the escaped version.
     */
    fun replacePlaceHolders(menu: GUIMenu, player: Player, input: String): String
}