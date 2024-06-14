package com.github.shynixn.shygui.impl.service

import com.github.shynixn.shygui.contract.GUIMenu
import com.github.shynixn.shygui.contract.GUIMenuService
import com.github.shynixn.shygui.contract.PlaceHolderService
import me.clip.placeholderapi.PlaceholderAPI
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class DependencyPlaceHolderApiServiceImpl constructor(
    private val plugin: Plugin,
    private val guiMenuService: GUIMenuService,
    private val placeHolderService: PlaceHolderService
) : PlaceholderExpansion(), PlaceHolderService {

    override fun onPlaceholderRequest(p: Player?, params: String?): String? {
        if (params == null || p == null) {
            return null
        }

        try {
            val guiMenu = guiMenuService.getGUI(p) ?: return null
            return placeHolderService.replacePlaceHolders(guiMenu, p, "%${plugin.name}_$params")
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }

        return null
    }

    override fun getIdentifier(): String {
        return "shygui"
    }

    override fun getAuthor(): String {
        return plugin.description.authors[0]
    }

    override fun getVersion(): String {
        return plugin.description.version
    }

    /**
     * Registers the placeholders.
     */
    override fun registerPlaceHolders() {
        this.register()
    }

    /**
     * Replaces incoming strings with the escaped version.
     */
    override fun replacePlaceHolders(menu: GUIMenu, player: Player, input: String): String {
        val replacedInput = placeHolderService.replacePlaceHolders(menu, player, input)
        return PlaceholderAPI.setPlaceholders(player, replacedInput)
    }

}
