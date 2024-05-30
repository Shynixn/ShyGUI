package com.github.shynixn.shygui.contract

interface GUIMenu {
    /**
     * Sets this GUI active to be rendered.
     */
    var isActive: Boolean

    /**
     * Sends an open packet to show this gui.
     */
    fun show()

    /**
     * Closes the inventory.
     */
    fun close()
}
