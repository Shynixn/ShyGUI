package com.github.shynixn.shygui.contract

import com.github.shynixn.shygui.entity.GUIItemCondition
import org.bukkit.entity.Player

interface GUIItemConditionService {
    /**
     * Evaluates the condition. If condition type is NONE. True is returned.
     */
    fun evaluate(player: Player, guiItemCondition: GUIItemCondition): Boolean
}
