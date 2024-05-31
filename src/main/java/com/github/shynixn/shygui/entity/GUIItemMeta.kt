package com.github.shynixn.shygui.entity

import com.github.shynixn.mcutils.common.item.Item

data class GUIItemMeta(
    /**
     * Item being rendered.
     */
    var item: Item = Item(),

    /**
     * Row.
     */
    var row: Int = 0,

    /**
     * Col being displayed.
     */
    var col: Int = 0,

    /**
     * The amount of rows this item spans.
     */
    var rowSpan: Int = 1,

    /**
     * The amount of cols this item span.
     */
    var colSpan: Int = 1,

    /**
     * Condition for being rendered.
     */
    var condition: GUIItemCondition = GUIItemCondition()
) {

}
