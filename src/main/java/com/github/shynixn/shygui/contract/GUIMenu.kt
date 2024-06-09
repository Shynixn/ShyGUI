package com.github.shynixn.shygui.contract

import org.bukkit.entity.Player

interface GUIMenu {
    /**
     * Is this GUI currently actively rendered.
     */
    val isVisible: Boolean

    /**
     * Minecraft Internal container id.
     */
    val containerId: Int

    /**
     * Owner of this menu.
     */
    val player: Player

    /**
     * Is this GUI already disposed.
     */
    val isDisposed: Boolean

    /**
     * Triggers a click on the given index.
     */
    fun click(index: Int)

    /**
     * Sends an open packet to show this gui.
     */
    fun show()

    /**
     * Sends a close packet to hide this gui.
     */
    fun hide()

    /**
     * Sends the contents to the owner.
     */
    fun sendContentUpdate()

    /**
     * Permanently closes this window and all previous windows. Disposes all inventories of the player.
     */
    fun closeAll()

    /**
     * Permanently closes this window and goes back to the previous window. Disposes this inventory.
     */
    fun closeBack()

    /**
     * Disposes the inventory.
     */
    fun dispose()
}
