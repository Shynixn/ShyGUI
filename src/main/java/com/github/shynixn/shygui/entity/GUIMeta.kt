package com.github.shynixn.shygui.entity

import com.github.shynixn.mcutils.common.repository.Element
import com.github.shynixn.mcutils.packet.api.meta.enumeration.WindowType

class GUIMeta : Element {
    override var name: String = ""

    /**
     * All items to be rendered.
     */
    var items: List<GUIItemMeta> = emptyList()

    /**
     * Container Size.
     */
    var size: WindowType = WindowType.SIX_ROW

    /**
     * Container title.
     */
    var title: String = "ShyGUI"

    /**
     * How often should the inventory resolve placeholders.
     */
    var updateIntervalTicks: Int = 20
}
