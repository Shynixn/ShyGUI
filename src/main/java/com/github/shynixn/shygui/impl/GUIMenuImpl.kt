package com.github.shynixn.shygui.impl

import com.github.shynixn.mccoroutine.folia.*
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.translateChatColors
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.api.meta.enumeration.WindowType
import com.github.shynixn.mcutils.packet.api.packet.PacketOutInventoryClose
import com.github.shynixn.mcutils.packet.api.packet.PacketOutInventoryContent
import com.github.shynixn.shygui.contract.GUIItemConditionService
import com.github.shynixn.shygui.contract.GUIMenu
import com.github.shynixn.shygui.contract.ShyGUILanguage
import com.github.shynixn.shygui.entity.GUIItemMeta
import com.github.shynixn.shygui.entity.GUIMeta
import com.github.shynixn.shygui.exception.GUIException
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
    private val commandService: CommandService,
    private val language: ShyGUILanguage,
    override val previousGUIName: String?,
    private val params: Array<String>,
) : GUIMenu {
    private val placeHolderStart = "%"
    private val itemStacks: Array<ItemStack?>
    private val actionItems: Array<GUIItemMeta?>
    private val indicesWithPlaceHolders = HashSet<Int>()

    init {
        val rows = if (meta.windowType == WindowType.SIX_ROW) {
            6
        } else if (meta.windowType == WindowType.THREE_ROW) {
            3
        } else if (meta.windowType == WindowType.TWO_ROW) {
            2
        } else if (meta.windowType == WindowType.ONE_ROW) {
            1
        } else {
            throw Exception("Window Type not found!")
        }

        itemStacks = arrayOfNulls(rows * 9)
        actionItems = arrayOfNulls(rows * 9)

        // Preprocess items to find out which indices need to be refreshed.
        for (i in 0 until meta.items.size) {
            val guiItem = meta.items[i]
            val item = guiItem.item
            if (item.displayName != null && item.displayName!!.contains(placeHolderStart)) {
                indicesWithPlaceHolders.add(i)
            } else if (item.durability != null && item.durability!!.contains(placeHolderStart)) {
                indicesWithPlaceHolders.add(i)
            } else if (item.amount.contains(placeHolderStart)) {
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
            } else if (guiItem.commands.firstOrNull { e -> e.command.contains(placeHolderStart) } != null) {
                indicesWithPlaceHolders.add(i)
            } else if (guiItem.condition.script != null && guiItem.condition.script!!.contains(placeHolderStart)) {
                indicesWithPlaceHolders.add(i)
            }
        }

        plugin.launch(plugin.mainDispatcher + object : CoroutineTimings() {}) {
            setGuiItemsToItemStacks(evaluateItemConditions(meta.items.filterIndexed { index, _ ->
                !indicesWithPlaceHolders.contains(
                    index
                )
            }))
            setGuiItemsToItemStacks(evaluateItemConditions(prepareItemsWithPlaceHolders()))
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
     * Name of the gui type.
     */
    override val name: String
        get() {
            return meta.name
        }

    /**
     * Gets the argument of the given index or returns an empty string if not set.
     */
    override fun getArgument(index: Int): String {
        if (index < params.size) {
            return params[index]
        }

        return ""
    }

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
        for (command in guiItem.commands) {
            if (command.command.isNotBlank()) {
                commandService.executeCommand(listOf(player), command) { input, player ->
                    if (player != null) {
                        placeHolderService.resolvePlaceHolder(input, player)
                    } else {
                        input
                    }
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
    }

    /**
     * Refreshes the placeHolders and sends a content update.
     */
    override fun refresh() {
        plugin.launch {
            setGuiItemsToItemStacks(evaluateItemConditions(prepareItemsWithPlaceHolders()))
            sendContentUpdate()
        }
    }

    /**
     * Sends the contents to the owner.
     */
    override fun sendContentUpdate() {
        if (isDisposed) {
            return
        }

        if (isVisible) {
            val items = this.itemStacks
            packetService.sendPacketOutInventoryContent(
                player,
                PacketOutInventoryContent(containerId = containerId, stateId = 1, items = items)
            )
        }
    }

    /**
     * Closes the inventory.
     */
    override fun closeAll() {
        if (playerHandle != null) {
            packetService.sendPacketOutInventoryClose(player, PacketOutInventoryClose(containerId))
            val playerData = playerHandle
            playerHandle = null
            guiMenuService?.closeGUI(playerData!!, true)
        }

        dispose()
    }

    /**
     * Permanently closes this window and goes back to the previous window. Disposes this inventory.
     */
    override fun closeBack() {
        if (playerHandle != null) {
            hide()
            val playerData = playerHandle
            playerHandle = null
            guiMenuService?.closeGUI(playerData!!, false)
        }

        dispose()
    }

    /**
     * Disposes the inventory.
     */
    override fun dispose() {
        isDisposed = true
        isVisible = false
        playerHandle = null
        guiMenuService = null
    }

    private suspend fun prepareItemsWithPlaceHolders(): List<GUIItemMeta> {
        val result = ArrayList<GUIItemMeta>()

        withContext(plugin.globalRegionDispatcher) {
            val player = playerHandle ?: return@withContext


            for (index in indicesWithPlaceHolders) {
                val guiItem = meta.items[index]
                val oldItem = guiItem.item
                val newGuiItem = guiItem.copy(item = guiItem.item.copy(), condition = guiItem.condition.copy())
                val newItem = newGuiItem.item

                if (oldItem.displayName != null && oldItem.displayName!!.contains(placeHolderStart)) {
                    newItem.displayName = placeHolderService.resolvePlaceHolder(
                        oldItem.displayName!!,
                        player,

                        )
                }
                if (oldItem.lore != null && oldItem.lore!!.firstOrNull { e -> e.contains(placeHolderStart) } != null) {
                    newItem.lore =
                        oldItem.lore!!.map { e ->
                            placeHolderService.resolvePlaceHolder(
                                e,
                                player,
                            )
                        }.toMutableList()

                }
                if (oldItem.nbt != null && oldItem.nbt!!.contains(placeHolderStart)) {
                    newItem.nbt = placeHolderService.resolvePlaceHolder(
                        oldItem.nbt!!,
                        player,
                    )
                }
                if (oldItem.component != null && oldItem.component!!.contains(placeHolderStart)) {
                    newItem.component = placeHolderService.resolvePlaceHolder(
                        oldItem.component!!,
                        player,
                    )
                }
                if (oldItem.skinBase64 != null && oldItem.skinBase64!!.contains(placeHolderStart)) {
                    newItem.skinBase64 = placeHolderService.resolvePlaceHolder(
                        oldItem.skinBase64!!,
                        player,
                    )
                }
                if (oldItem.typeName.contains(placeHolderStart)) {
                    newItem.typeName = placeHolderService.resolvePlaceHolder(
                        oldItem.typeName,
                        player,
                    )
                }
                if (oldItem.durability != null && oldItem.durability!!.contains(placeHolderStart)) {
                    newItem.durability = placeHolderService.resolvePlaceHolder(
                        oldItem.durability!!,
                        player,
                    )
                }

                val newCondition = newGuiItem.condition
                val oldCondition = guiItem.condition
                if (oldCondition.script != null) {
                    newCondition.script =
                        placeHolderService.resolvePlaceHolder(
                            guiItem.condition.script!!,
                            player,
                        )
                }

                result.add(newGuiItem)
            }
        }

        return result
    }

    private fun evaluateItemConditions(input: List<GUIItemMeta>): List<GUIItemMeta> {
        if (playerHandle == null) {
            return emptyList()
        }

        val result = ArrayList<GUIItemMeta>()
        for (item in input) {
            val evaluationResult = guiItemConditionService.evaluate(playerHandle!!, item.condition)

            if (evaluationResult) {
                result.add(item)
            }
        }
        return result
    }

    private fun setGuiItemsToItemStacks(guiItems: List<GUIItemMeta>) {
        for (guiItemMeta in guiItems) {
            val startIndex = (guiItemMeta.row - 1) * 9 + guiItemMeta.col - 1

            if (startIndex < 0 || startIndex >= itemStacks.size) {
                playerHandle?.sendMessage(
                    language.shyGuiRowColOutOfRangeError.text.format(guiItemMeta.row, guiItemMeta.col).translateChatColors()
                )
                throw GUIException(language.shyGuiRowColOutOfRangeError.text.format(guiItemMeta.row, guiItemMeta.col))
            }

            for (i in 0 until guiItemMeta.rowSpan) {
                for (j in 0 until guiItemMeta.colSpan) {
                    val index = startIndex + (i * 9) + j

                    if (index < 0 || index >= itemStacks.size) {
                        continue
                    }

                    try {
                        val itemStack = itemService.toItemStack(guiItemMeta.item)
                        this.itemStacks[index] = itemStack
                        this.actionItems[index] = guiItemMeta
                    } catch (e: Exception) {
                        playerHandle?.sendMessage(
                            language.shyGuiCannotParseItemStackError.text.format(
                                guiItemMeta.row,
                                guiItemMeta.col
                            ).translateChatColors()
                        )
                        throw GUIException(
                            language.shyGuiCannotParseItemStackError.text.format(
                                guiItemMeta.row,
                                guiItemMeta.col
                            ), e
                        )
                    }
                }
            }
        }
    }
}
