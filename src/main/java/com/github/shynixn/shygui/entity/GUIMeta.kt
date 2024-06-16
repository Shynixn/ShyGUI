package com.github.shynixn.shygui.entity

import com.github.shynixn.mcutils.common.repository.Element
import com.github.shynixn.mcutils.packet.api.meta.enumeration.WindowType

class GUIMeta : Element {
    /**
     * Unique identifier.
     */
    override var name: String = ""

    /**
     * All items to be rendered.
     */
    var items: List<GUIItemMeta> = emptyList()

    /**
     * Commands to execute.
     */
    var command: GUICommand = GUICommand()

    /**
     * Container Size.
     */
    var windowType: WindowType = WindowType.SIX_ROW

    /**
     * Optional condition to open the GUI.
     */
    var condition : GUIItemCondition? = null

    /**
     * Container title.
     */
    var title: String = "ShyGUI"

    /**
     * How often should the inventory resolve placeholders.
     */
    var updateIntervalTicks: Int = 20
}
