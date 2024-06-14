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
import com.github.shynixn.shygui.contract.ScriptService
import com.github.shynixn.shygui.entity.GUIMeta
import com.github.shynixn.shygui.entity.Settings
import com.github.shynixn.shygui.impl.service.*
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.util.logging.Level

class ShyGUIDependencyInjectionModule(private val settings: Settings, private val plugin: Plugin) :
    DependencyInjectionModule() {
    companion object {
        private val placeHolderPluginName = "PlaceholderAPI"
    }

    override fun configure() {
        val configurationService = ConfigurationServiceImpl(plugin)
        addService<Plugin>(plugin)
        addService<Settings>(settings)

        // Repositories
        val templateRepositoryImpl =
            YamlFileRepositoryImpl<GUIMeta>(
                plugin,
                "gui",
                listOf(
                    "gui/petblocks_main_menu.yml" to "petblocks_main_menu.yml",
                    "gui/simple_sample_menu.yml" to "simple_sample_menu.yml"
                ),
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
        if (Bukkit.getPluginManager().getPlugin(placeHolderPluginName) != null) {
            addService<PlaceHolderService>(
                DependencyPlaceHolderApiServiceImpl(
                    plugin,
                    getService<GUIMenuService>(),
                    PlaceHolderServiceImpl(plugin)
                )
            )
            plugin.logger.log(Level.INFO, "Loaded ${settings.embedded} dependency ${placeHolderPluginName}.")
        } else {
            addService<PlaceHolderService>(PlaceHolderServiceImpl(plugin))
        }

        try {
            // Try Load Nashorn Implementation
            val nashornScriptEngine = ScriptNashornEngineServiceImpl(plugin, configurationService)
            addService<ScriptService>(nashornScriptEngine)
            plugin.logger.log(Level.INFO, "Loaded ${settings.embedded} embedded NashornScriptEngine.")
        } catch (e: Error) {
            try {
                // Try Load JDK Implementation
                val jdkScriptEngine = ScriptJdkEngineServiceImpl(plugin, configurationService)
                addService<ScriptService>(jdkScriptEngine)
                plugin.logger.log(Level.INFO, "Loaded ${settings.embedded} JDK NashornScriptEngine.")
            } catch (ex: Exception) {
                throw RuntimeException("Cannot find NashornScriptEngine implementation.", ex)
            }
        }
    }
}
