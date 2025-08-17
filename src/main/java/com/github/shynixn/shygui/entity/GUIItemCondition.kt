package com.github.shynixn.shygui.entity

data class GUIItemCondition(
    /**
     * Permission to show, this is independent of type and also is an AND operation to the other type.
     */
    var permission: String? = null,

    /**
     * Script to evaluate.
     */
    var script: String? = null
)
