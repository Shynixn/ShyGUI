package com.github.shynixn.shygui.contract

interface ScriptService {
    /**
     * Evaluates a Javascript expression.
     */
    fun evaluate(expression: String): Any?
}
