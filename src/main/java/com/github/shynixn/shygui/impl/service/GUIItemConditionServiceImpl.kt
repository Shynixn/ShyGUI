package com.github.shynixn.shygui.impl.service

import com.github.shynixn.shygui.contract.GUIItemConditionService
import com.github.shynixn.shygui.entity.GUIItemCondition

class GUIItemConditionServiceImpl : GUIItemConditionService {
    /**
     * Evaluates the condition. If condition type is NONE. True is returned.
     */
    override fun evaluate(guiItemCondition: GUIItemCondition): Boolean {
        return true
    }
}
