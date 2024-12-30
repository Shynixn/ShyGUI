package com.github.shynixn.shygui

import com.fasterxml.jackson.core.type.TypeReference
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.ConfigurationServiceImpl
import com.github.shynixn.mcutils.common.CoroutineExecutor
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.command.CommandServiceImpl
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.language.globalChatMessageService
import com.github.shynixn.mcutils.common.language.globalPlaceHolderService
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderServiceImpl
import com.github.shynixn.mcutils.common.repository.CacheRepository
import com.github.shynixn.mcutils.common.repository.CachedRepositoryImpl
import com.github.shynixn.mcutils.common.repository.Repository
import com.github.shynixn.mcutils.common.repository.YamlFileRepositoryImpl
import com.github.shynixn.mcutils.guice.DependencyInjectionModule
import com.github.shynixn.mcutils.javascript.JavaScriptService
import com.github.shynixn.mcutils.javascript.JavaScriptServiceImpl
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.impl.service.ChatMessageServiceImpl
import com.github.shynixn.mcutils.packet.impl.service.ItemServiceImpl
import com.github.shynixn.mcutils.packet.impl.service.PacketServiceImpl
import com.github.shynixn.shygui.contract.GUIItemConditionService
import com.github.shynixn.shygui.contract.GUIMenuService
import com.github.shynixn.shygui.contract.ShyGUILanguage
import com.github.shynixn.shygui.entity.GUIMeta
import com.github.shynixn.shygui.entity.ShyGUISettings
import com.github.shynixn.shygui.impl.service.GUIItemConditionServiceImpl
import com.github.shynixn.shygui.impl.service.GUIMenuServiceImpl
import org.bukkit.plugin.Plugin

class ShyGUIDependencyInjectionModule(
    private val plugin: Plugin,
    private val settings: ShyGUISettings,
    private val language: ShyGUILanguage
) :
    DependencyInjectionModule() {

    override fun configure() {
        // Module
        addService<Plugin>(plugin)
        addService<ShyGUILanguage>(language)
        addService<ShyGUISettings>(settings)

        // Repositories
        val templateRepositoryImpl = YamlFileRepositoryImpl<GUIMeta>(
            plugin,
            "gui",
            settings.guis,
            emptyList(),
            object : TypeReference<GUIMeta>() {})
        val cacheTemplateRepository = CachedRepositoryImpl(templateRepositoryImpl)
        addService<Repository<GUIMeta>>(cacheTemplateRepository)
        addService<CacheRepository<GUIMeta>>(cacheTemplateRepository)

        // Services
        addService<GUIMenuService, GUIMenuServiceImpl>()
        addService<GUIItemConditionService, GUIItemConditionServiceImpl>()

        // Library Services
        addService<ConfigurationService>(ConfigurationServiceImpl(plugin))
        addService<PacketService>(PacketServiceImpl(plugin))
        addService<ItemService>(ItemServiceImpl())
        val placeHolderService = PlaceHolderServiceImpl(plugin)
        addService<PlaceHolderService>(placeHolderService)
        addService<CommandService>(CommandServiceImpl(object : CoroutineExecutor {
            override fun execute(f: suspend () -> Unit) {
                plugin.launch {
                    f.invoke()
                }
            }
        }))
        val chatMessageService = ChatMessageServiceImpl(plugin)
        addService<ChatMessageService>(chatMessageService)
        addService<JavaScriptService>(
            JavaScriptServiceImpl(
                plugin,
                this.plugin.config.getStringList("scriptEngine.options")
            )
        )
        plugin.globalChatMessageService = chatMessageService
        plugin.globalPlaceHolderService = placeHolderService
    }
}
