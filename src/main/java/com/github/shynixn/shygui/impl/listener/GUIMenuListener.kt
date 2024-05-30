package com.github.shynixn.shygui.impl.listener

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.shygui.contract.GUIMenuService
import com.google.inject.Inject
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin

class GUIMenuListener @Inject constructor(private val plugin: Plugin, private val guiMenuService: GUIMenuService) :
    Listener {
    /**
     * Gets called when a player quits the server.
     */
    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        plugin.launch {
            guiMenuService.clearCache(event.player)
        }
    }
}
