package com.github.shynixn.shygui.impl.service

import com.github.shynixn.shygui.contract.PlaceHolderService
import org.bukkit.entity.Player

class PlaceHolderServiceImpl  : PlaceHolderService{
    /**
     * Replaces incoming strings with the escaped version.
     */
    override fun replacePlaceHolders(player: Player?, input: String): String {
        return input
    }
}
