package com.github.shynixn.shygui

import com.github.shynixn.fasterxml.jackson.core.type.TypeReference
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.ConfigurationServiceImpl
import com.github.shynixn.mcutils.common.CoroutinePlugin
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.command.CommandServiceImpl
import com.github.shynixn.mcutils.common.di.DependencyInjectionModule
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.repository.CacheRepository
import com.github.shynixn.mcutils.common.repository.CachedRepositoryImpl
import com.github.shynixn.mcutils.common.repository.Repository
import com.github.shynixn.mcutils.common.repository.YamlFileRepositoryImpl
import com.github.shynixn.mcutils.common.script.ScriptService
import com.github.shynixn.mcutils.common.script.ScriptServiceImpl
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
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.ServicePriority

class ShyGUIDependencyInjectionModule(
    private val plugin: Plugin,
    private val settings: ShyGUISettings,
    private val language: ShyGUILanguage,
    private val placeHolderService: PlaceHolderService
) {

    fun build(): DependencyInjectionModule {
        val module = DependencyInjectionModule()

        // Params
        module.addService<Plugin>(plugin)
        module.addService<CoroutinePlugin>(plugin)
        module.addService<ShyGUILanguage>(language)
        module.addService<ShyGUISettings>(settings)
        module.addService<PlaceHolderService>(placeHolderService)

        // Repositories
        val templateRepositoryImpl = YamlFileRepositoryImpl<GUIMeta>(
            plugin,
            "gui",
            plugin.dataFolder.toPath().resolve("gui"),
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
            GUIItemConditionServiceImpl(module.getService())
        }
        module.addService<ShyGUICommandExecutor> {
            ShyGUICommandExecutor(
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
        module.addService<GUIMenuListener> {
            GUIMenuListener(module.getService(), module.getService())
        }
        module.addService<ConfigurationService> {
            ConfigurationServiceImpl(module.getService())
        }
        module.addService<PacketService> {
            PacketServiceImpl(module.getService())
        }
        module.addService<ItemService> {
            ItemServiceImpl()
        }
        module.addService<CommandService> {
            CommandServiceImpl(module.getService())
        }
        module.addService<ChatMessageService> {
            ChatMessageServiceImpl(module.getService(), module.getService())
        }
        module.addService<ScriptService> {
            ScriptServiceImpl()
        }

        // Developer Api
        Bukkit.getServicesManager().register(
            GUIMenuService::class.java, module.getService<GUIMenuService>(), plugin, ServicePriority.Normal
        )

        return module
    }
}
