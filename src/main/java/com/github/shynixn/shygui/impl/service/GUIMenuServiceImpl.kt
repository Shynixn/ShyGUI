package com.github.shynixn.shygui.impl.service

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.repository.CacheRepository
import com.github.shynixn.mcutils.common.translateChatColors
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.api.packet.PacketOutInventoryOpen
import com.github.shynixn.shygui.contract.GUIItemConditionService
import com.github.shynixn.shygui.contract.GUIMenu
import com.github.shynixn.shygui.contract.GUIMenuService
import com.github.shynixn.shygui.contract.ShyGUILanguage
import com.github.shynixn.shygui.entity.GUIItemCondition
import com.github.shynixn.shygui.entity.GUIMeta
import com.github.shynixn.shygui.impl.GUIMenuImpl
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor

class GUIMenuServiceImpl (
    private val plugin: Plugin,
    private val packetService: PacketService,
    private val itemService: ItemService,
    private val placeHolderService: PlaceHolderService,
    private val guiItemConditionService: GUIItemConditionService,
    private val commandService: CommandService,
    private val repository: CacheRepository<GUIMeta>,
    private val language : ShyGUILanguage
) : GUIMenuService {
    private val maxSubPages = 20
    private val guis = HashMap<Player, Stack<GUIMenu>>()

    /**
     * Gets the thread executor for this menu.
     */
    override fun getExecutor(): Executor {
        return Executor { command ->
            plugin.launch {
                command.run()
            }
        }
    }

    /**
     * Gets all gui menus.
     */
    override fun getAllGUIMetas(): CompletionStage<List<GUIMeta>> {
        val completeAble = CompletableFuture<List<GUIMeta>>()

        plugin.launch {
            val metas = repository.getAll()
            completeAble.complete(metas)
        }

        return completeAble
    }

    /**
     * Opens a GUI for the given player.
     */
    override suspend fun openGUI(player: Player, meta: GUIMeta, arguments: Array<String>): GUIMenu? {
        val condition = evaluateGUIOpenCondition(player, meta)

        if (condition != null) {
            val conditionResult = guiItemConditionService.evaluate(player, condition)

            if (!conditionResult) {
                return null
            }
        }


        if (!guis.containsKey(player)) {
            guis[player] = Stack()
        }

        val stack = guis[player]!!

        if (stack.size > maxSubPages) {
            stack.removeFirst()
        }

        val previousGuiName = if (!stack.isEmpty()) {
            val previousGUI = stack.peek()
            previousGUI.hide()
            previousGUI.name
        } else {
            null
        }

        val containerId = if (!stack.isEmpty()) {
            val previousGUI = stack.peek()
            previousGUI.containerId
        } else {
            val containerId = packetService.getNextContainerId(player)
            plugin.launch {
                packetService.sendPacketOutInventoryOpen(
                    player, PacketOutInventoryOpen(containerId, meta.windowType, meta.title.translateChatColors())
                )
            }
            containerId
        }

        val guiMenu = GUIMenuImpl(
            meta,
            plugin,
            containerId,
            packetService,
            placeHolderService,
            itemService,
            player,
            this,
            guiItemConditionService,
            commandService,
            language,
            previousGuiName,
            arguments
        )

        stack.push(guiMenu)
        guiMenu.show()
        return guiMenu
    }

    /**
     * Opens the GUI menu async.
     * Returns null if not opened.
     */
    override fun openGUIAsync(player: Player, meta: GUIMeta, arguments: Array<String>): CompletionStage<GUIMenu?> {
        val completableStage = CompletableFuture<GUIMenu?>()

        plugin.launch {
            completableStage.complete(openGUI(player, meta, arguments))
        }

        return completableStage
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
    internal fun closeGUI(player: Player, closeAll: Boolean) {
        if (!guis.containsKey(player)) {
            return
        }

        val guiStack = guis[player]!!

        if (closeAll) {
            guiStack.clear()
        }

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
                gui.closeAll()
            }
        }
        guis.clear()
    }

    private suspend fun evaluateGUIOpenCondition(player: Player, guiMeta: GUIMeta): GUIItemCondition? {
        val condition = guiMeta.condition ?: return null
        val newCondition = condition.copy()
        val oldCondition = condition

        withContext(plugin.globalRegionDispatcher) {
            if (oldCondition.script != null) {
                newCondition.script =
                    placeHolderService.resolvePlaceHolder(
                        oldCondition.script!!,
                        player
                    )
            }
        }

        return newCondition
    }
}
