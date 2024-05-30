package com.github.shynixn.shygui.impl.service

import com.github.shynixn.shygui.contract.GUIMenu
import com.github.shynixn.shygui.contract.GUIMenuService
import com.github.shynixn.shygui.entity.GUIMeta
import com.github.shynixn.shygui.impl.GUIMenuImpl
import com.google.inject.Inject
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*

class GUIMenuServiceImpl @Inject constructor(private val plugin: Plugin) : GUIMenuService {
    private val maxSubPages = 20
    private val guis = HashMap<Player, Stack<GUIMenu>>()

    /**
     * Opens a GUI for the given player.
     */
    override fun openGUI(player: Player, meta: GUIMeta): GUIMenu {
        val guiMenu = GUIMenuImpl(meta, plugin, player, this)

        if (!guis.containsKey(player)) {
            guis[player] = Stack()
        }

        val stack = guis[player]!!

        if (stack.size > maxSubPages) {
            stack.removeFirst()
        }

        if (!stack.isEmpty()) {
            val previousGUI = stack.peek()
            previousGUI.isActive = false
        }

        stack.push(guiMenu)
        guiMenu.show()
        return guiMenu
    }

    /**
     * Closes the current open GUI of the player.
     */
    override fun closeGUI(player: Player) {
        if (!guis.containsKey(player)) {
            return
        }

        val guiStack = guis[player]!!

        if (guiStack.isEmpty()) {
            guis.remove(player)
            return
        }

        val currentGUI = guiStack.pop()
        currentGUI.close()

        if (!guiStack.isEmpty()) {
            val previousGUI = guiStack.peek()
            previousGUI.show()
        }
    }

    /**
     * Clears the cache for the given player.
     */
    override fun clearCache(player: Player) {
        guis.remove(player)
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * `try`-with-resources statement.
     * However, implementers of this interface are strongly encouraged
     * to make their `close` methods idempotent.
     *
     * @throws Exception if this resource cannot be closed
     */
    override fun close() {
        for (player in guis.keys) {
            val guiStack = guis[player]!!
            while (!guiStack.empty()) {
                val gui = guiStack.pop()
                gui.close()
            }
        }
        guis.clear()
    }
}
