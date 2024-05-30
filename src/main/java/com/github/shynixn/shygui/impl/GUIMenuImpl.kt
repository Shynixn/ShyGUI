package com.github.shynixn.shygui.impl

import com.github.shynixn.shygui.contract.GUIMenu
import com.github.shynixn.shygui.contract.GUIMenuService
import com.github.shynixn.shygui.entity.GUIMeta
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class GUIMenuImpl(
    private val meta: GUIMeta,
    private val plugin: Plugin,
    private var player: Player? = null,
    private var guiMenuService: GUIMenuService? = null
) : GUIMenu {
    /**
     * Sets this GUI active to be rendered.
     */
    override var isActive: Boolean = true

    /**
     * Sends an open packet to show this gui.
     */
    override fun show() {
        isActive = true

        TODO("Not yet implemented")
    }

    /**
     * Closes the inventory.
     */
    override fun close() {
        if (player != null) {
            guiMenuService?.closeGUI(player!!)
        }

        isActive = false
        player = null
        guiMenuService = null
    }
}
