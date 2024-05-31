package com.github.shynixn.shygui.impl.listener

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mcutils.packet.api.event.PacketAsyncEvent
import com.github.shynixn.mcutils.packet.api.packet.PacketInInventoryClick
import com.github.shynixn.mcutils.packet.api.packet.PacketInInventoryClose
import com.github.shynixn.shygui.contract.GUIMenuService
import com.google.inject.Inject
import kotlinx.coroutines.delay
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin

class GUIMenuListener @Inject constructor(private val plugin: Plugin, private val guiMenuService: GUIMenuService) :
    Listener {
    private val clickCooldownProtection = HashSet<Player>()
    private val scheduledReSync = HashSet<Player>()
    private val clickProtectionMilliseconds = 100L

    /**
     * Gets called when a player quits the server.
     */
    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        plugin.launch {
            guiMenuService.clearCache(event.player)
        }
    }

    /**
     * Gets called on inventory click packet.
     */
    @EventHandler
    fun onPacketEvent(event: PacketAsyncEvent) {
        val packet = event.packet
        val player = event.player

        if (packet is PacketInInventoryClick) {
            plugin.launch {
                if (clickCooldownProtection.contains(player)) {
                    scheduledReSync.add(player)
                    return@launch
                }

                val gui = guiMenuService.getGUI(player) ?: return@launch

                if (gui.containerId != packet.containerId) {
                    return@launch
                }

                gui.click(packet.slotId)
                delay(clickProtectionMilliseconds)

                if (scheduledReSync.contains(player)) {
                    // If a player clicked in the inventory while clickProtection has been active, send one sendContents packet after the clickProtection is over.
                    scheduledReSync.remove(player)
                    gui.sendContentUpdate()
                }

                clickCooldownProtection.remove(player)
            }
            return
        }

        if (packet is PacketInInventoryClose) {
            plugin.launch {
                val gui = guiMenuService.getGUI(player)
                gui?.close()
            }
        }
    }
}
