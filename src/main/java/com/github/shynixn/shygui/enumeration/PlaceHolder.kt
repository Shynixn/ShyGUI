package com.github.shynixn.shygui.enumeration

import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.shygui.contract.GUIMenu
import com.github.shynixn.shygui.contract.GUIMenuService
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*

enum class PlaceHolder(
    val text: String,
    val f: ((Player?, GUIMenu?, Map<String, Any>?) -> String?),
) {
    PARAM_1("param_1", { _, _, context ->
        if (context != null) {
            context["0"] as String?
        } else {
            null
        }
    }),
    PARAM_2("param_2", { _, _, context ->
        if (context != null) {
            context["1"] as kotlin.String?
        } else {
            null
        }
    }),
    PLAYER_NAME("player_name", { p, _, _ -> p?.name }),
    PLAYER_DISPLAY_NAME("player_displayName", { p, _, _ -> p?.displayName }),
    GUI_NAME("gui_name", { _, g, _ -> g?.name }),
    GUI_PREVIOUS("gui_backName", { _, g, _ -> g?.previousGUIName }),
    GUI_PARAM_1("gui_param1", { _, g, _ -> g?.getArgument(0) }),
    GUI_PARAM_2("gui_param2", { _, g, _ -> g?.getArgument(1) }),
    GUI_PARAM_3("gui_param3", { _, g, _ -> g?.getArgument(2) }),
    GUI_PARAM_4("gui_param4", { _, g, _ -> g?.getArgument(3) }),
    GUI_PARAM_5("gui_param5", { _, g, _ -> g?.getArgument(4) }),
    GUI_PARAM_6("gui_param6", { _, g, _ -> g?.getArgument(5) }),
    GUI_PARAM_7("gui_param7", { _, g, _ -> g?.getArgument(6) }),
    GUI_PARAM_8("gui_param8", { _, g, _ -> g?.getArgument(7) }),
    GUI_PARAM_9("gui_param9", { _, g, _ -> g?.getArgument(8) });

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
                placeHolderService.register(placeHolder.getFullPlaceHolder(plugin)) { player, context ->
                    val guiMenu = if (player != null) {
                        guiMenuService.getGUI(player)
                    } else {
                        null
                    }
                    placeHolder.f.invoke(player, guiMenu, context)
                }
            }
        }
    }
}
