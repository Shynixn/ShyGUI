package com.github.shynixn.shygui

import com.fasterxml.jackson.core.type.TypeReference
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.ConfigurationServiceImpl
import com.github.shynixn.mcutils.common.CoroutineExecutor
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.command.CommandServiceImpl
import com.github.shynixn.mcutils.common.di.DependencyInjectionModule
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.language.globalChatMessageService
import com.github.shynixn.mcutils.common.language.globalPlaceHolderService
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderServiceImpl
import com.github.shynixn.mcutils.common.repository.CacheRepository
import com.github.shynixn.mcutils.common.repository.CachedRepositoryImpl
import com.github.shynixn.mcutils.common.repository.Repository
import com.github.shynixn.mcutils.common.repository.YamlFileRepositoryImpl
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
import com.github.shynixn.shygui.impl.commandexecutor.ShyGUICommandExecutor
import com.github.shynixn.shygui.impl.listener.GUIMenuListener
import com.github.shynixn.shygui.impl.service.GUIItemConditionServiceImpl
import com.github.shynixn.shygui.impl.service.GUIMenuServiceImpl
import org.bukkit.plugin.Plugin

class ShyGUIDependencyInjectionModule(
    private val plugin: Plugin,
    private val settings: ShyGUISettings,
    private val language: ShyGUILanguage
) {

    fun build(): DependencyInjectionModule {
        val module = DependencyInjectionModule()

        // Params
        module.addService<Plugin>(plugin)
        module.addService<ShyGUILanguage>(language)
        module.addService<ShyGUISettings>(settings)

        // Repositories
        val templateRepositoryImpl = YamlFileRepositoryImpl<GUIMeta>(
            plugin,
            "gui",
            settings.guis,
            emptyList(),
            object : TypeReference<GUIMeta>() {})
        val cacheTemplateRepository = CachedRepositoryImpl(templateRepositoryImpl)
        module.addService<Repository<GUIMeta>>(cacheTemplateRepository)
        module.addService<CacheRepository<GUIMeta>>(cacheTemplateRepository)

        // Services
        module.addService<GUIMenuService> {
            GUIMenuServiceImpl(
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService(),
                module.getService()
            )
        }
        module.addService<GUIItemConditionService> {
            GUIItemConditionServiceImpl(module.getService(), module.getService())
        }
        module.addService<ShyGUICommandExecutor> {
            ShyGUICommandExecutor(
                module.getService(), module.getService(), module.getService(), module.getService(), module.getService(),
                module.getService(), module.getService()
            )
        }
        module.addService<GUIMenuListener> {
            GUIMenuListener(module.getService(), module.getService())
        }

        // Library Services
        module.addService<ConfigurationService>(ConfigurationServiceImpl(plugin))
        module.addService<PacketService>(PacketServiceImpl(plugin))
        module.addService<ItemService>(ItemServiceImpl())
        val placeHolderService = PlaceHolderServiceImpl(plugin)
        module.addService<PlaceHolderService>(placeHolderService)
        module.addService<CommandService>(CommandServiceImpl(object : CoroutineExecutor {
            override fun execute(f: suspend () -> Unit) {
                plugin.launch {
                    f.invoke()
                }
            }
        }))
        val chatMessageService = ChatMessageServiceImpl(plugin)
        module.addService<ChatMessageService>(chatMessageService)
        module.addService<JavaScriptService>(
            JavaScriptServiceImpl(
                plugin,
                this.plugin.config.getStringList("scriptEngine.options")
            )
        )
        plugin.globalChatMessageService = chatMessageService
        plugin.globalPlaceHolderService = placeHolderService
        return module
    }
}
