package com.github.shynixn.shygui.impl.service

import com.github.shynixn.mcutils.javascript.JavaScriptService
import com.github.shynixn.shygui.contract.GUIItemConditionService
import com.github.shynixn.shygui.entity.GUIItemCondition
import com.github.shynixn.shygui.enumeration.GUIItemConditionType
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.logging.Level

class GUIItemConditionServiceImpl (
    private val plugin: Plugin,
    private val scriptService: JavaScriptService
) :
    GUIItemConditionService {
    /**
     * Evaluates the condition. If condition type is NONE. True is returned.
     */
    override fun evaluate(player: Player, guiItemCondition: GUIItemCondition): Boolean {
        if (!guiItemCondition.permission.isNullOrBlank() && !player.hasPermission(guiItemCondition.permission!!)) {
            return false
        }

        val conditionType = guiItemCondition.type

        if (conditionType == GUIItemConditionType.NONE) {
            return true
        }

        if (conditionType == GUIItemConditionType.JAVASCRIPT) {
            if (guiItemCondition.js.isNullOrBlank()) {
                throw RuntimeException("JS property is required when having a condition with type JAVASCRIPT!")
            }

            try {
                // Script Engine is thread safe.
                return scriptService.evaluate(guiItemCondition.js!!) as Boolean
            } catch (e: Exception) {
                plugin.logger.log(Level.SEVERE, "Cannot evaluate expression '${guiItemCondition.js!!}'.", e)
                return false
            }
        }

        val left = guiItemCondition.left
        val right = guiItemCondition.right

        if (left.isNullOrBlank()) {
            throw RuntimeException("Left property is required when having a condition with type $conditionType!")
        }

        if (right.isNullOrBlank()) {
            throw RuntimeException("Rigth property is required when having a condition with type $conditionType!")
        }

        if (conditionType == GUIItemConditionType.STRING_EQUALS) {
            return left == right
        }

        if (conditionType == GUIItemConditionType.STRING_NOT_EQUALS) {
            return left != right
        }

        if (conditionType == GUIItemConditionType.STRING_EQUALS_IGNORE_CASE) {
            return left.equals(right, true)
        }

        if (conditionType == GUIItemConditionType.STRING_NOT_EQUALS_IGNORE_CASE) {
            return !left.equals(right, true)
        }

        if (conditionType == GUIItemConditionType.STRING_CONTAINS) {
            return left.contains(right)
        }

        if (conditionType == GUIItemConditionType.STRING_NOT_CONTAINS) {
            return !left.contains(right)
        }

        if (conditionType == GUIItemConditionType.STRING_CONTAINS_IGNORE_CASE) {
            return left.contains(right, true)
        }

        if (conditionType == GUIItemConditionType.STRING_NOT_CONTAINS_IGNORE_CASE) {
            return !left.contains(right, true)
        }

        val leftNumber =
            left.toDoubleOrNull() ?: throw RuntimeException("Left property cannot be converted to a number!")
        val rightNumber =
            right.toDoubleOrNull() ?: throw RuntimeException("Right property cannot be converted to a number!")

        if (conditionType == GUIItemConditionType.NUMBER_GREATER_THAN) {
            return leftNumber > rightNumber
        }

        if (conditionType == GUIItemConditionType.NUMBER_GREATER_THAN_OR_EQUAL) {
            return leftNumber >= rightNumber
        }

        if (conditionType == GUIItemConditionType.NUMBER_LESS_THAN) {
            return leftNumber < rightNumber
        }

        if (conditionType == GUIItemConditionType.NUMBER_LESS_THAN_OR_EQUAL) {
            return leftNumber <= rightNumber
        }

        throw RuntimeException("ConditionType $conditionType not supported!")
    }
}

