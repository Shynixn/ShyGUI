package com.github.shynixn.shygui.enumeration

import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.shygui.contract.GUIMenu
import com.github.shynixn.shygui.contract.GUIMenuService
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*

enum class PlaceHolder(
    val text: String,
    val f: ((Player?, GUIMenu?) -> String?),
) {
    PLAYER_NAME("player_name", { p, _ -> p?.name }),
    PLAYER_DISPLAY_NAME("player_displayName", { p, _ -> p?.displayName }),
    GUI_NAME("gui_name", { _, g -> g?.name }),
    GUI_PREVIOUS("gui_backName", { _, g -> g?.previousGUIName }),
    PARAM_1("gui_param1", { _, g -> g?.getArgument(0) }),
    PARAM_2("gui_param2", { _, g -> g?.getArgument(1) }),
    PARAM_3("gui_param3", { _, g -> g?.getArgument(2) }),
    PARAM_4("gui_param4", { _, g -> g?.getArgument(3) }),
    PARAM_5("gui_param5", { _, g -> g?.getArgument(4) }),
    PARAM_6("gui_param6", { _, g -> g?.getArgument(5) }),
    PARAM_7("gui_param7", { _, g -> g?.getArgument(6) }),
    PARAM_8("gui_param8", { _, g -> g?.getArgument(7) }),
    PARAM_9("gui_param9", { _, g -> g?.getArgument(8) });

    fun getFullPlaceHolder(plugin: Plugin): String {
        return "%${plugin.name.lowercase(Locale.ENGLISH)}_${text}%"
    }

    companion object {
        /**
         * Registers all placeHolder. Overrides previously registered placeholders.
         */
        fun registerAll(
            plugin: Plugin,
            placeHolderService: PlaceHolderService,
            guiMenuService: GUIMenuService
        ) {
            for (placeHolder in PlaceHolder.values()) {
                placeHolderService.register(placeHolder.getFullPlaceHolder(plugin)) { player, _ ->
                    val guiMenu = if (player != null) {
                        guiMenuService.getGUI(player)
                    } else {
                        null
                    }
                    placeHolder.f.invoke(player, guiMenu)
                }
            }
        }
    }
}
