package com.github.shynixn.shygui

import com.fasterxml.jackson.core.type.TypeReference
import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.ConfigurationServiceImpl
import com.github.shynixn.mcutils.common.CoroutineExecutor
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.command.CommandServiceImpl
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
import com.github.shynixn.shygui.contract.GUIItemConditionService
import com.github.shynixn.shygui.contract.GUIMenuService
import com.github.shynixn.shygui.contract.PlaceHolderService
import com.github.shynixn.shygui.entity.GUIMeta
import com.github.shynixn.shygui.impl.service.GUIItemConditionServiceImpl
import com.github.shynixn.shygui.impl.service.GUIMenuServiceImpl
import com.github.shynixn.shygui.impl.service.PlaceHolderServiceImpl
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
        val templateRepositoryImpl =
            YamlFileRepositoryImpl<GUIMeta>(
                plugin,
                "gui",
                listOf("gui/petblocks_main_menu.yml" to "petblocks_main_menu.yml"),
                emptyList(),
                object : TypeReference<GUIMeta>() {})
        val cacheTemplateRepository = CachedRepositoryImpl(templateRepositoryImpl)
        addService<Repository<GUIMeta>>(cacheTemplateRepository)
        addService<CacheRepository<GUIMeta>>(cacheTemplateRepository)

        // Library Services
        addService<ConfigurationService>(ConfigurationServiceImpl(plugin))
        addService<PacketService>(PacketServiceImpl(plugin))
        addService<ItemService>(ItemServiceImpl())
        addService<CommandService>(CommandServiceImpl(object : CoroutineExecutor {
            override fun execute(f: suspend () -> Unit) {
                plugin.launch {
                    f.invoke()
                }
            }
        }))
        addService<ChatMessageService>(ChatMessageServiceImpl(plugin))

        // Services
        addService<GUIMenuService, GUIMenuServiceImpl>()
        addService<GUIItemConditionService, GUIItemConditionServiceImpl>()
        addService<PlaceHolderService, PlaceHolderServiceImpl>()
    }
}
