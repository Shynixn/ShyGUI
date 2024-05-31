package com.github.shynixn.shygui.contract

import com.github.shynixn.shygui.entity.GUIMeta
import org.bukkit.entity.Player

interface GUIMenuService : AutoCloseable {
    /**
     * Opens a GUI for the given player.
     */
    fun openGUI(player: Player, meta: GUIMeta): GUIMenu

    /**
     * Gets the currently open gui of the player.
     */
    fun getGUI(player: Player): GUIMenu?

    /**
     * Clears the cache for the given player.
     */
    fun clearCache(player: Player)
}
