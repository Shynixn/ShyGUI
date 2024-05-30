package com.github.shynixn.shygui.contract

import com.github.shynixn.shygui.entity.GUIMeta
import org.bukkit.entity.Player

interface GUIMenuService : AutoCloseable {
    /**
     * Opens a GUI for the given player.
     */
    fun openGUI(player: Player, meta: GUIMeta): GUIMenu

    /**
     * Closes the current open GUI of the player.
     */
    fun closeGUI(player: Player);

    /**
     * Clears the cache for the given player.
     */
    fun clearCache(player: Player)
}
