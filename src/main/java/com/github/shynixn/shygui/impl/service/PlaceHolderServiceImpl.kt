package com.github.shynixn.shygui.impl.service

import com.github.shynixn.mcutils.common.translateChatColors
import com.github.shynixn.shygui.contract.GUIMenu
import com.github.shynixn.shygui.contract.PlaceHolderService
import com.github.shynixn.shygui.enumeration.PlaceHolder
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*

class PlaceHolderServiceImpl(private val plugin: Plugin) : PlaceHolderService {
    private val placeHolderFunctions = HashMap<PlaceHolder, ((GUIMenu, Player) -> String)>()
    private val placeHolders = HashMap<String, PlaceHolder>()
    private val id = "%shygui_"

    init {
        for (placeHolder in PlaceHolder.values()) {
            placeHolders[placeHolder.fullPlaceHolder] = placeHolder
        }

        placeHolderFunctions[PlaceHolder.PLAYER_NAME] = { guiMenu, player ->
            player.name
        }
        placeHolderFunctions[PlaceHolder.PLAYER_DISPLAY_NAME] = { guiMenu, player ->
            player.displayName
        }
        placeHolderFunctions[PlaceHolder.GUI_PREVIOUS] = { guiMenu, player ->
            if (guiMenu.previousGUIName != null) {
                guiMenu.previousGUIName!!
            } else {
                ""
            }
        }
        placeHolderFunctions[PlaceHolder.GUI_NAME] = { guiMenu, player ->
            guiMenu.name
        }
        placeHolderFunctions[PlaceHolder.PARAM_1] = { guiMenu, player ->
            guiMenu.getArgument(0)
        }
        placeHolderFunctions[PlaceHolder.PARAM_2] = { guiMenu, player ->
            guiMenu.getArgument(1)
        }
        placeHolderFunctions[PlaceHolder.PARAM_3] = { guiMenu, player ->
            guiMenu.getArgument(2)
        }
        placeHolderFunctions[PlaceHolder.PARAM_4] = { guiMenu, player ->
            guiMenu.getArgument(3)
        }
        placeHolderFunctions[PlaceHolder.PARAM_5] = { guiMenu, player ->
            guiMenu.getArgument(4)
        }
        placeHolderFunctions[PlaceHolder.PARAM_6] = { guiMenu, player ->
            guiMenu.getArgument(5)
        }
        placeHolderFunctions[PlaceHolder.PARAM_7] = { guiMenu, player ->
            guiMenu.getArgument(6)
        }
        placeHolderFunctions[PlaceHolder.PARAM_8] = { guiMenu, player ->
            guiMenu.getArgument(8)
        }
        placeHolderFunctions[PlaceHolder.PARAM_9] = { guiMenu, player ->
            guiMenu.getArgument(9)
        }
    }

    /**
     * Registers the placeholders.
     */
    override fun registerPlaceHolders() {
    }

    /**
     * Replaces incoming strings with the escaped version.
     */
    override fun replacePlaceHolders(menu: GUIMenu, player: Player, input: String): String {
        val nInput = input.replace("%${plugin.name}_", id)
        val locatedPlaceHolders = HashMap<PlaceHolder, String>()
        val characterCache = StringBuilder()

        for (character in nInput) {
            characterCache.append(character)

            if (character == '%') {
                val evaluatedPlaceHolder = characterCache.toString()
                if (placeHolders.containsKey(evaluatedPlaceHolder)) {
                    val placeHolder = placeHolders[evaluatedPlaceHolder]!!
                    if (!locatedPlaceHolders.containsKey(placeHolder)) {
                        if (placeHolderFunctions.containsKey(placeHolder)) {
                            val result = placeHolderFunctions[placeHolder]!!.invoke(menu, player)
                            locatedPlaceHolders[placeHolder] = result
                        }
                    }
                }

                characterCache.clear()
                characterCache.append(character)
            }
        }

        var output = nInput

        for (locatedPlaceHolder in locatedPlaceHolders.keys) {
            output = output.replace(locatedPlaceHolder.fullPlaceHolder, locatedPlaceHolders[locatedPlaceHolder]!!)
        }
        return output.translateChatColors()
    }
}
