package com.github.shynixn.shygui.impl.service

import com.github.shynixn.mcutils.common.script.ScriptService
import com.github.shynixn.shygui.contract.GUIItemConditionService
import com.github.shynixn.shygui.entity.GUIItemCondition
import org.bukkit.entity.Player

class GUIItemConditionServiceImpl(
    private val scriptService: ScriptService
) :
    GUIItemConditionService {
    /**
     * Evaluates the condition. If condition type is NONE. True is returned.
     */
    override fun evaluate(player: Player, guiItemCondition: GUIItemCondition): Boolean {
        if (!guiItemCondition.permission.isNullOrBlank() && !player.hasPermission(guiItemCondition.permission!!)) {
            return false
        }

        if (guiItemCondition.script != null && guiItemCondition.script!!.isNotBlank()) {
            return scriptService.evaluateExpression(guiItemCondition.script!!)
        }

        return true
    }
}

