package com.github.shynixn.shygui.entity

import com.github.shynixn.shygui.enumeration.GUIItemConditionType

data class GUIItemCondition(
    /**
     * Type of condition.
     */
    var type: GUIItemConditionType = GUIItemConditionType.NONE,

    /**
     * Left Parameter.
     */
    var left: String? = null,

    /**
     * Right parameter.
     */
    var right: String? = null,

    /**
     * JavaScript to execute.
     */
    var js: String? = null
)
