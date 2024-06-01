package com.github.shynixn.shygui.entity

data class GUICommand(
    var command: String = "",

    var permission: String = "",

    var usage: String = "",

    var description: String = "",

    var aliases: List<String> = emptyList()
)
