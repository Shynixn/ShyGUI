package com.github.shynixn.shygui.impl.provider

import com.github.shynixn.mcutils.common.placeholder.PlaceHolderProvider
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.shygui.contract.GUIMenuService
import com.google.inject.Inject
import me.clip.placeholderapi.PlaceholderAPI
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class ShyGUIPlaceHolderApiProvider @Inject constructor(
    private val plugin: Plugin,
    private val placeHolderService: PlaceHolderService,
    private val guiMenuService: GUIMenuService
) : PlaceholderExpansion(), PlaceHolderProvider {
    private val placeHolderApiKey = "placeholderapi"

    init {
        register()
    }

    override fun onPlaceholderRequest(p: Player?, params: String?): String? {
        if (params == null || p == null) {
            return null
        }

        try {
            val guiMenu = guiMenuService.getGUI(p) ?: return null
            return placeHolderService.resolvePlaceHolder(
                p,
                "%${plugin.name.lowercase()}_$params%",
                mapOf(ShyGUIPlaceHolderProvider.guiKey to guiMenu, placeHolderApiKey to true)
            )
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }

        return null
    }

    override fun resolvePlaceHolder(player: Player, input: String, parameters: Map<String, Any>): String {
        if (parameters.containsKey(placeHolderApiKey)) {
            return input
        }

        return PlaceholderAPI.setPlaceholders(player, input)
    }

    override fun getIdentifier(): String {
        return plugin.name.lowercase()
    }

    override fun getAuthor(): String {
        return plugin.description.authors[0]
    }

    override fun getVersion(): String {
        return plugin.description.version
    }
}
