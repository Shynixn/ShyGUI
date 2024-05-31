package com.github.shynixn.shygui

import com.fasterxml.jackson.core.type.TypeReference
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.ConfigurationServiceImpl
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.repository.CacheRepository
import com.github.shynixn.mcutils.common.repository.CachedRepositoryImpl
import com.github.shynixn.mcutils.common.repository.Repository
import com.github.shynixn.mcutils.common.repository.YamlFileRepositoryImpl
import com.github.shynixn.mcutils.guice.DependencyInjectionModule
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.impl.service.ChatMessageServiceImpl
import com.github.shynixn.mcutils.packet.impl.service.ItemServiceImpl
import com.github.shynixn.mcutils.packet.impl.service.PacketServiceImpl
import com.github.shynixn.shygui.contract.GUIMenuService
import com.github.shynixn.shygui.entity.GUIMeta
import com.github.shynixn.shygui.impl.service.GUIMenuServiceImpl
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


    override fun configure() {
        addService<Plugin>(plugin)

        // Repositories
        val templateRepositoryImpl = YamlFileRepositoryImpl<GUIMeta>(plugin, "gui", listOf(), emptyList(), object : TypeReference<GUIMeta>() {})
        val cacheTemplateRepository = CachedRepositoryImpl(templateRepositoryImpl)
        addService<Repository<GUIMeta>>(cacheTemplateRepository)
        addService<CacheRepository<GUIMeta>>(cacheTemplateRepository)

        // Library Services
        addService<ConfigurationService>(ConfigurationServiceImpl(plugin))
        addService<PacketService>(PacketServiceImpl(plugin))
        addService<ChatMessageService, ChatMessageServiceImpl>()
        addService<ItemService>(ItemServiceImpl())

        // Services
        addService<GUIMenuService, GUIMenuServiceImpl>()
    }
}
