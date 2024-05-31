package com.github.shynixn.shygui.impl.service

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.shygui.contract.GUIItemConditionService
import com.github.shynixn.shygui.contract.GUIMenu
import com.github.shynixn.shygui.contract.GUIMenuService
import com.github.shynixn.shygui.contract.PlaceHolderService
import com.github.shynixn.shygui.entity.GUIMeta
import com.github.shynixn.shygui.impl.GUIMenuImpl
import com.google.inject.Inject
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*

class GUIMenuServiceImpl @Inject constructor(
    private val plugin: Plugin,
    private val packetService: PacketService,
    private val itemService: ItemService,
    private val placeHolderService: PlaceHolderService,
    private val guiItemConditionService: GUIItemConditionService
) :
    GUIMenuService {
    private val maxSubPages = 20
    private val guis = HashMap<Player, Stack<GUIMenu>>()

    /**
     * Opens a GUI for the given player.
     */
    override fun openGUI(player: Player, meta: GUIMeta): GUIMenu {
        val containerId = packetService.getNextContainerId(player)
        val guiMenu = GUIMenuImpl(
            meta,
            plugin,
            containerId,
            packetService,
            placeHolderService,
            itemService,
            player,
            this,
            guiItemConditionService
        )

        if (!guis.containsKey(player)) {
            guis[player] = Stack()
        }

        val stack = guis[player]!!

        if (stack.size > maxSubPages) {
            stack.removeFirst()
        }

        if (!stack.isEmpty()) {
            val previousGUI = stack.peek()
            previousGUI.hide()
        }

        stack.push(guiMenu)
        guiMenu.show()
        return guiMenu
    }

    /**
     * Gets the currently open gui of the player.
     */
    override fun getGUI(player: Player): GUIMenu? {
        if (guis.containsKey(player)) {
            val guiStack = guis[player]!!

            if (guiStack.isEmpty()) {
                return null
            }

            return guiStack.peek()
        }

        return null
    }

    /**
     * Clears the cache for the given player.
     */
    override fun clearCache(player: Player) {
        guis.remove(player)
    }

    /**
     * Closes the current open GUI of the player.
     */
    internal fun closeGUI(player: Player) {
        if (!guis.containsKey(player)) {
            return
        }

        val guiStack = guis[player]!!

        if (guiStack.isEmpty()) {
            guis.remove(player)
            return
        }

        guiStack.pop()

        if (!guiStack.isEmpty()) {
            val previousGUI = guiStack.peek()
            previousGUI.show()
        }
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
