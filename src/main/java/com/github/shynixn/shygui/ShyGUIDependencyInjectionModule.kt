package com.github.shynixn.shygui

import com.github.shynixn.mcutils.guice.DependencyInjectionModule
import org.bukkit.plugin.Plugin

class ShyGUIDependencyInjectionModule(private val plugin: Plugin) : DependencyInjectionModule() {
    companion object {
        val areLegacyVersionsIncluded: Boolean by lazy {
            try {
                Class.forName("com.github.shynixn.shygui.lib.com.github.shynixn.mcutils.packet.nms.v1_8_R3.PacketSendServiceImpl")
                true
            } catch (e: ClassNotFoundException) {
                false
            }
        }
    }
}
