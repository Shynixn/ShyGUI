package com.github.shynixn.shygui.contract

import org.bukkit.entity.Player

interface PlaceHolderService {
    /**
     * Replaces incoming strings with the escaped version.
     */
    fun replacePlaceHolders(player: Player, input: String): String
}
