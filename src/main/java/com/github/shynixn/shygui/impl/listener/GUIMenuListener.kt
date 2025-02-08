package com.github.shynixn.shygui.impl.listener

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mcutils.packet.api.event.PacketAsyncEvent
import com.github.shynixn.mcutils.packet.api.packet.PacketInInventoryClick
import com.github.shynixn.mcutils.packet.api.packet.PacketInInventoryClose
import com.github.shynixn.shygui.contract.GUIMenuService
import kotlinx.coroutines.delay
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin

class GUIMenuListener (private val plugin: Plugin, private val guiMenuService: GUIMenuService) :
    Listener {
    private val clickCooldownProtection = HashSet<Player>()
    private val scheduledReSync = HashSet<Player>()
    private val clickProtectionMilliseconds = 300L
    private val reSyncProtectionMilliseconds = 80L

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
                    if (scheduledReSync.contains(player)) {
                        return@launch
                    }

                    scheduledReSync.add(player)
                    val gui = guiMenuService.getGUI(player) ?: return@launch
                    delay(reSyncProtectionMilliseconds)
                    gui.sendContentUpdate()
                    player.updateInventory()
                    scheduledReSync.remove(player)
                    return@launch
                }

                val gui = guiMenuService.getGUI(player) ?: return@launch

                if (gui.containerId != packet.containerId) {
                    return@launch
                }

                clickCooldownProtection.add(player)
                gui.click(packet.slotId)
                gui.sendContentUpdate()
                player.updateInventory()
                delay(clickProtectionMilliseconds)
                clickCooldownProtection.remove(player)
            }
            return
        }

        if (packet is PacketInInventoryClose) {
            plugin.launch {
                val gui = guiMenuService.getGUI(player)
                gui?.closeAll()
            }
        }
    }
}
