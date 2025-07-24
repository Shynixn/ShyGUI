package com.github.shynixn.shygui

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.regionDispatcher
import com.github.shynixn.mcutils.common.ChatColor
import com.github.shynixn.mcutils.common.CoroutinePlugin
import com.github.shynixn.mcutils.common.Version
import com.github.shynixn.mcutils.common.di.DependencyInjectionModule
import com.github.shynixn.mcutils.common.language.reloadTranslation
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderServiceImpl
import com.github.shynixn.mcutils.packet.api.PacketInType
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.shygui.contract.GUIMenuService
import com.github.shynixn.shygui.entity.ShyGUISettings
import com.github.shynixn.shygui.enumeration.PlaceHolder
import com.github.shynixn.shygui.impl.commandexecutor.ShyGUICommandExecutor
import com.github.shynixn.shygui.impl.listener.GUIMenuListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level
import kotlin.coroutines.CoroutineContext

class ShyGUIPlugin : JavaPlugin(), CoroutinePlugin {
    private val prefix: String = ChatColor.BLUE.toString() + "[ShyGUI] " + ChatColor.WHITE
    private lateinit var shyGuiModule: DependencyInjectionModule
    private var immediateDisable = false

    companion object {
        private val areLegacyVersionsIncluded: Boolean by lazy {
            try {
                Class.forName("com.github.shynixn.shygui.lib.com.github.shynixn.mcutils.packet.nms.v1_8_R3.PacketSendServiceImpl")
                true
            } catch (e: ClassNotFoundException) {
                false
            }
        }
    }

    /**
     * Called when this plugin is enabled.
     */
    override fun onEnable() {
        Bukkit.getServer().consoleSender.sendMessage(prefix + ChatColor.GREEN + "Loading ShyGUI ...")
        this.saveDefaultConfig()
        this.reloadConfig()
        val versions = if (areLegacyVersionsIncluded) {
            listOf(
                Version.VERSION_1_8_R3,
                Version.VERSION_1_9_R2,
                Version.VERSION_1_10_R1,
                Version.VERSION_1_11_R1,
                Version.VERSION_1_12_R1,
                Version.VERSION_1_13_R1,
                Version.VERSION_1_13_R2,
                Version.VERSION_1_14_R1,
                Version.VERSION_1_15_R1,
                Version.VERSION_1_16_R1,
                Version.VERSION_1_16_R2,
                Version.VERSION_1_16_R3,
                Version.VERSION_1_17_R1,
                Version.VERSION_1_18_R1,
                Version.VERSION_1_18_R2,
                Version.VERSION_1_19_R1,
                Version.VERSION_1_19_R2,
                Version.VERSION_1_19_R3,
                Version.VERSION_1_20_R1,
                Version.VERSION_1_20_R2,
                Version.VERSION_1_20_R3,
                Version.VERSION_1_20_R4,
                Version.VERSION_1_21_R1,
                Version.VERSION_1_21_R2,
                Version.VERSION_1_21_R3,
                Version.VERSION_1_21_R4,
                Version.VERSION_1_21_R5,
            )
        } else {
            listOf(Version.VERSION_1_21_R5)
        }

        if (!Version.serverVersion.isCompatible(*versions.toTypedArray())) {
            immediateDisable = true
            logger.log(Level.SEVERE, "================================================")
            logger.log(Level.SEVERE, "ShyGUI does not support your server version")
            logger.log(Level.SEVERE, "Install v" + versions[0].from + " - v" + versions[versions.size - 1].to)
            logger.log(Level.SEVERE, "Need support for a particular version? Go to https://www.patreon.com/Shynixn")
            logger.log(Level.SEVERE, "Plugin gets now disabled!")
            logger.log(Level.SEVERE, "================================================")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        logger.log(Level.INFO, "Loaded NMS version ${Version.serverVersion}.")

        // Register Language
        val language = ShyGUILanguageImpl()
        reloadTranslation(language)
        logger.log(Level.INFO, "Loaded language file.")

        // Module
        val placeHolderService = PlaceHolderServiceImpl(this)
        this.shyGuiModule =
            ShyGUIDependencyInjectionModule(this, ShyGUISettings(), language, placeHolderService).build()

        // Register PlaceHolders
        PlaceHolder.registerAll(
            this, shyGuiModule.getService<PlaceHolderService>(), shyGuiModule.getService<GUIMenuService>()
        )

        // Register Packets
        val packetService = shyGuiModule.getService<PacketService>()
        packetService.registerPacketListening(PacketInType.CLICKINVENTORY)
        packetService.registerPacketListening(PacketInType.CLOSEINVENTORY)

        // Register Listeners
        Bukkit.getPluginManager().registerEvents(shyGuiModule.getService<GUIMenuListener>(), this)

        // Register CommandExecutor
        val commandExecutor = shyGuiModule.getService<ShyGUICommandExecutor>()
        commandExecutor.registerShyGuiCommand()

        // Load GUI Commands
        val plugin = this
        runBlocking {
            plugin.logger.log(Level.INFO, "Registering GUI commands...")
            commandExecutor.registerGuiCommands()
            plugin.logger.log(Level.INFO, "Registered GUI commands.")
            Bukkit.getServer().consoleSender.sendMessage(prefix + ChatColor.GREEN + "Enabled ShyGUI " + plugin.description.version + " by Shynixn")
        }
    }

    override fun execute(f: suspend () -> Unit): Job {
        return launch {
            f.invoke()
        }
    }

    override fun fetchEntityDispatcher(entity: Entity): CoroutineContext {
        return entityDispatcher(entity)
    }

    override fun fetchGlobalRegionDispatcher(): CoroutineContext {
        return globalRegionDispatcher
    }

    override fun fetchLocationDispatcher(location: Location): CoroutineContext {
        return regionDispatcher(location)
    }

    /**
     * Called when this plugin is disabled.
     */
    override fun onDisable() {
        if (immediateDisable) {
            return
        }

        shyGuiModule.close()
    }
}
