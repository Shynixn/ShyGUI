package com.github.shynixn.shygui.contract

import com.github.shynixn.shygui.entity.GUIMeta
import org.bukkit.entity.Player
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor

interface GUIMenuService : AutoCloseable {
    /**
     * Gets the thread executor for this menu.
     */
    fun getExecutor(): Executor

    /**
     * Gets all gui menus.
     */
    fun getAllGUIMetas(): CompletionStage<List<GUIMeta>>

    /**
     * Opens a GUI for the given player.
     * Returns null if not opened.
     */
    suspend fun openGUI(player: Player, meta: GUIMeta, arguments: Array<String> = emptyArray()): GUIMenu?

    /**
     * Opens the GUI menu async.
     * Returns null if not opened.
     */
    fun openGUIAsync(player: Player, meta: GUIMeta, arguments: Array<String> = emptyArray()): CompletionStage<GUIMenu?>

    /**
     * Gets the currently open gui of the player.
     */
    fun getGUI(player: Player): GUIMenu?

    /**
     * Clears the cache for the given player.
     */
    fun clearCache(player: Player)
}
