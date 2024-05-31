package com.github.shynixn.shygui.contract

import com.github.shynixn.shygui.entity.GUIItemCondition

interface GUIItemConditionService {
    /**
     * Evaluates the condition. If condition type is NONE. True is returned.
     */
    fun evaluate(guiItemCondition: GUIItemCondition): Boolean
}
