package com.github.shynixn.shygui.entity

import com.github.shynixn.mcutils.common.repository.Element

class GUIMeta : Element {
    override var name: String = ""

    /**
     * How often should the inventory resolve placeholders.
     */
    var updateIntervalTicks: Int = 20
}
