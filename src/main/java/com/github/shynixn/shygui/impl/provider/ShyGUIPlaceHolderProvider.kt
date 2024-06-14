package com.github.shynixn.shygui.impl.provider

import com.github.shynixn.mcutils.common.placeholder.PlaceHolderProvider
import com.github.shynixn.mcutils.common.translateChatColors
import com.github.shynixn.shygui.contract.GUIMenu
import com.github.shynixn.shygui.enumeration.PlaceHolder
import com.google.inject.Inject
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.HashMap

class ShyGUIPlaceHolderProvider @Inject constructor(private val plugin: Plugin) :
    PlaceHolderProvider {
        companion object{
            val guiKey = "gui"
        }
    private val placeHolderFunctions = HashMap<String, ((GUIMenu, Player) -> String)>()

    init {
        registerPlaceHolder(PlaceHolder.PLAYER_NAME) { guiMenu, player ->
            player.name
        }
        registerPlaceHolder(PlaceHolder.PLAYER_DISPLAY_NAME) { guiMenu, player ->
            player.displayName
        }
        registerPlaceHolder(PlaceHolder.GUI_PREVIOUS) { guiMenu, player ->
            if (guiMenu.previousGUIName != null) {
                guiMenu.previousGUIName!!
            } else {
                ""
            }
        }
        registerPlaceHolder(PlaceHolder.GUI_NAME) { guiMenu, player ->
            guiMenu.name
        }

        registerPlaceHolder(PlaceHolder.PARAM_1) { guiMenu, player ->
            guiMenu.getArgument(0)
        }
        registerPlaceHolder(PlaceHolder.PARAM_2) { guiMenu, player ->
            guiMenu.getArgument(1)
        }
        registerPlaceHolder(PlaceHolder.PARAM_3) { guiMenu, player ->
            guiMenu.getArgument(2)
        }
        registerPlaceHolder(PlaceHolder.PARAM_4) { guiMenu, player ->
            guiMenu.getArgument(3)
        }
        registerPlaceHolder(PlaceHolder.PARAM_5) { guiMenu, player ->
            guiMenu.getArgument(4)
        }
        registerPlaceHolder(PlaceHolder.PARAM_6) { guiMenu, player ->
            guiMenu.getArgument(5)
        }
        registerPlaceHolder(PlaceHolder.PARAM_7) { guiMenu, player ->
            guiMenu.getArgument(6)
        }
        registerPlaceHolder(PlaceHolder.PARAM_8) { guiMenu, player ->
            guiMenu.getArgument(7)
        }
        registerPlaceHolder(PlaceHolder.PARAM_9) { guiMenu, player ->
            guiMenu.getArgument(8)
        }
    }

    override fun resolvePlaceHolder(player: Player, input: String, parameters: Map<String, Any>): String {
        val guiMenu = parameters[guiKey] as GUIMenu? ?: return input
        val locatedPlaceHolders = HashMap<String, String>()
        val characterCache = StringBuilder()

        for (character in input) {
            characterCache.append(character)

            if (character == '%') {
                val evaluatedPlaceHolder = characterCache.toString()
                if (placeHolderFunctions.containsKey(evaluatedPlaceHolder) && !locatedPlaceHolders.containsKey(
                        evaluatedPlaceHolder
                    )
                ) {
                    val result = placeHolderFunctions[evaluatedPlaceHolder]!!.invoke(guiMenu, player)
                    locatedPlaceHolders[evaluatedPlaceHolder] = result
                }
                characterCache.clear()
                characterCache.append(character)
            }
        }

        var output = input

        for (locatedPlaceHolder in locatedPlaceHolders.keys) {
            output = output.replace(locatedPlaceHolder, locatedPlaceHolders[locatedPlaceHolder]!!)
        }
        return output.translateChatColors()
    }

    private fun registerPlaceHolder(placeHolder: PlaceHolder, f: ((GUIMenu, Player) -> String)) {
        placeHolderFunctions["%${plugin.name.lowercase()}_${placeHolder.fullPlaceHolder}%"] = f
    }
}
