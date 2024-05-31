package com.github.shynixn.shygui.impl

import com.github.shynixn.mccoroutine.folia.*
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.translateChatColors
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.api.meta.enumeration.WindowType
import com.github.shynixn.mcutils.packet.api.packet.PacketOutInventoryClose
import com.github.shynixn.mcutils.packet.api.packet.PacketOutInventoryContent
import com.github.shynixn.mcutils.packet.api.packet.PacketOutInventoryOpen
import com.github.shynixn.shygui.contract.GUIItemConditionService
import com.github.shynixn.shygui.contract.GUIMenu
import com.github.shynixn.shygui.contract.PlaceHolderService
import com.github.shynixn.shygui.entity.GUIItemMeta
import com.github.shynixn.shygui.entity.GUIMeta
import com.github.shynixn.shygui.enumeration.GUIItemConditionType
import com.github.shynixn.shygui.impl.service.GUIMenuServiceImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class GUIMenuImpl(
    private val meta: GUIMeta,
    private val plugin: Plugin,
    override val containerId: Int,
    private val packetService: PacketService,
    private val placeHolderService: PlaceHolderService,
    private val itemService: ItemService,
    private var playerHandle: Player? = null,
    private var guiMenuService: GUIMenuServiceImpl? = null,
    private var guiItemConditionService: GUIItemConditionService,
    private val commandService: CommandService
) : GUIMenu {
    private val placeHolderStart = "%"
    private val itemStacks: Array<ItemStack?>
    private val actionItems: Array<GUIItemMeta?>
    private val indicesWithPlaceHolders = HashSet<Int>()

    init {
        if (meta.size == WindowType.SIX_ROW) {
            itemStacks = arrayOfNulls(6 * 9)
            actionItems = arrayOfNulls(6 * 9)
        } else {
            throw Exception("Window Type not found!")
        }

        // Preprocess items to find out which indices need to be refreshed.
        for (i in 0 until meta.items.size) {
            val guiItem = meta.items[i]
            val item = guiItem.item
            if (item.displayName != null && item.displayName!!.contains(placeHolderStart)) {
                indicesWithPlaceHolders.add(i)
            } else if (item.lore != null && item.lore!!.firstOrNull { e -> e.contains(placeHolderStart) } != null) {
                indicesWithPlaceHolders.add(i)
            } else if (item.nbt != null && item.nbt!!.contains(placeHolderStart)) {
                indicesWithPlaceHolders.add(i)
            } else if (item.component != null && item.component!!.contains(placeHolderStart)) {
                indicesWithPlaceHolders.add(i)
            } else if (item.skinBase64 != null && item.skinBase64!!.contains(placeHolderStart)) {
                indicesWithPlaceHolders.add(i)
            } else if (item.typeName.contains(placeHolderStart)) {
                indicesWithPlaceHolders.add(i)
            }
        }

        plugin.launch(plugin.mainDispatcher + object : CoroutineTimings() {}) {
            setGuiItemsToItemStacks(evaluateItemConditions(meta.items))
            while (!isDisposed) {
                if (isVisible) {
                    setGuiItemsToItemStacks(evaluateItemConditions(prepareItemsWithPlaceHolders()))
                    sendContentUpdate()
                }
                delay(meta.updateIntervalTicks.ticks)
            }
        }
    }

    /**
     * Sets this GUI active to be rendered.
     */
    override var isVisible: Boolean = true

    /**
     * Owner of this menu.
     */
    override val player: Player
        get() {
            if (isDisposed) {
                throw RuntimeException("This GUIMenu has already been disposed!")
            }

            return playerHandle!!
        }

    /**
     * Is this GUI already disposed.
     */
    override var isDisposed: Boolean = false

    /**
     * Triggers a click on the given index.
     */
    override fun click(index: Int) {
        if (isDisposed) {
            throw RuntimeException("This GUIMenu has already been disposed!")
        }

        if (index < 0 || index >= actionItems.size) {
            return
        }

        val guiItem = actionItems[index] ?: return

        plugin.launch(plugin.globalRegionDispatcher) {
            for (command in guiItem.commands) {
                commandService.executeCommand(listOf(player), command) { input, player ->
                    placeHolderService.replacePlaceHolders(
                        player, input
                    )
                }
            }
        }
    }

    /**
     * Sends an open packet to show this gui.
     */
    override fun show() {
        if (isDisposed) {
            throw RuntimeException("This GUIMenu has already been disposed!")
        }

        isVisible = true
        plugin.launch {
            setGuiItemsToItemStacks(evaluateItemConditions(prepareItemsWithPlaceHolders()))
            packetService.sendPacketOutInventoryOpen(
                player, PacketOutInventoryOpen(containerId, meta.size, meta.title.translateChatColors())
            )
            sendContentUpdate()
        }
    }

    /**
     * Sends a close packet to hide this gui.
     */
    override fun hide() {
        if (isDisposed) {
            throw RuntimeException("This GUIMenu has already been disposed!")
        }

        isVisible = false
        packetService.sendPacketOutInventoryClose(player, PacketOutInventoryClose(containerId))
    }

    /**
     * Sends the contents to the owner.
     */
    override fun sendContentUpdate() {
        if (isDisposed) {
            throw RuntimeException("This GUIMenu has already been disposed!")
        }

        val items = this.itemStacks
        packetService.sendPacketOutInventoryContent(player, PacketOutInventoryContent().also {
            it.items = items.toList()
        })
    }

    /**
     * Closes the inventory.
     */
    override fun close() {
        if (playerHandle != null) {
            hide()
            val playerData = playerHandle
            playerHandle = null
            guiMenuService?.closeGUI(playerData!!)
        }

        isDisposed = true
        isVisible = false
        playerHandle = null
        guiMenuService = null
    }

    private suspend fun prepareItemsWithPlaceHolders(): List<GUIItemMeta> {
        val result = ArrayList<GUIItemMeta>()

        withContext(plugin.globalRegionDispatcher) {
            for (index in indicesWithPlaceHolders) {
                val guiItem = meta.items[index]
                val oldItem = guiItem.item
                val newGuiItem = guiItem.copy(item = guiItem.item.copy(), condition = guiItem.condition.copy())
                val newItem = newGuiItem.item

                if (oldItem.displayName != null && oldItem.displayName!!.contains(placeHolderStart)) {
                    newItem.displayName = placeHolderService.replacePlaceHolders(player, oldItem.displayName!!)
                }
                if (oldItem.lore != null && oldItem.lore!!.firstOrNull { e -> e.contains(placeHolderStart) } != null) {
                    newItem.lore =
                        oldItem.lore!!.map { e -> placeHolderService.replacePlaceHolders(player, e) }.toMutableList()
                }
                if (oldItem.nbt != null && oldItem.nbt!!.contains(placeHolderStart)) {
                    newItem.nbt = placeHolderService.replacePlaceHolders(player, oldItem.nbt!!)
                }
                if (oldItem.component != null && oldItem.component!!.contains(placeHolderStart)) {
                    newItem.component = placeHolderService.replacePlaceHolders(player, oldItem.component!!)
                }
                if (oldItem.skinBase64 != null && oldItem.skinBase64!!.contains(placeHolderStart)) {
                    newItem.skinBase64 = placeHolderService.replacePlaceHolders(player, oldItem.skinBase64!!)
                }
                if (oldItem.typeName.contains(placeHolderStart)) {
                    newItem.typeName = placeHolderService.replacePlaceHolders(player, oldItem.typeName)
                }

                if (guiItem.condition.type != GUIItemConditionType.NONE) {
                    val newCondition = newGuiItem.condition
                    val oldCondition = guiItem.condition
                    if (oldCondition.left != null) {
                        newCondition.left = placeHolderService.replacePlaceHolders(player, guiItem.condition.left!!)
                    }
                    if (oldCondition.right != null) {
                        newCondition.right = placeHolderService.replacePlaceHolders(player, guiItem.condition.right!!)
                    }
                    if (oldCondition.js != null) {
                        newCondition.js = placeHolderService.replacePlaceHolders(player, guiItem.condition.js!!)
                    }
                }

                result.add(newGuiItem)
            }
        }

        return result
    }

    private suspend fun evaluateItemConditions(input: List<GUIItemMeta>): List<GUIItemMeta> {
        val result = ArrayList<GUIItemMeta>()
        withContext(plugin.asyncDispatcher) {
            for (item in input) {
                val evaluationResult = guiItemConditionService.evaluate(item.condition)

                if (evaluationResult) {
                    result.add(item)
                }
            }
        }
        return result
    }

    private fun setGuiItemsToItemStacks(guiItems: List<GUIItemMeta>) {
        for (guiItemMeta in guiItems) {
            val startIndex = (guiItemMeta.row - 1) * 9 + guiItemMeta.col

            if (startIndex < 0 || startIndex >= itemStacks.size) {
                throw RuntimeException("The specified row ${guiItemMeta.row} and col ${guiItemMeta.col} are out of range of the GUI!")
            }

            for (j in startIndex until guiItemMeta.rowSpan) {
                for (i in startIndex until guiItemMeta.colSpan) {
                    val index = i + j * 9

                    if (index < 0 || index >= itemStacks.size) {
                        continue
                    }

                    try {
                        val itemStack = itemService.toItemStack(guiItemMeta.item)
                        this.itemStacks[index] = itemStack
                        this.actionItems[index] = guiItemMeta
                    } catch (e: Exception) {
                        throw RuntimeException("Cannot parse ItemStack at ${guiItemMeta.row} and col ${guiItemMeta.col}!")
                    }
                }
            }
        }
    }
}
