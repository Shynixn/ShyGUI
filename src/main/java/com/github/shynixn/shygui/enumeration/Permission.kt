package com.github.shynixn.shygui.enumeration

enum class Permission(val text: String) {
    COMMAND("shygui.command"),
    OTHER_PLAYER("shygui.manipulateother"),
    // Dynamic
    DYN_OPEN("shygui.gui."),
}
