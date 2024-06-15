package com.github.shynixn.shygui

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mcutils.common.ChatColor
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.Version
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.reloadTranslation
import com.github.shynixn.mcutils.guice.DependencyInjectionModule
import com.github.shynixn.mcutils.packet.api.PacketInType
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.shygui.contract.GUIMenuService
import com.github.shynixn.shygui.entity.Settings
import com.github.shynixn.shygui.enumeration.Permission
import com.github.shynixn.shygui.impl.commandexecutor.ShyGUICommandExecutor
import com.github.shynixn.shygui.impl.listener.GUIMenuListener
import com.github.shynixn.shygui.impl.provider.ShyGUIPlaceHolderApiProvider
import com.github.shynixn.shygui.impl.provider.ShyGUIPlaceHolderProvider
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

class ShyGUIPlugin : JavaPlugin() {
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
            )
        } else {
            listOf(Version.VERSION_1_20_R4)
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

        // Guice
        val settings = object : Settings() {
            override fun reload() {
                this.embedded = "ShyGUI"
                this.guis = listOf(
                    "gui/petblocks_main_menu.yml" to "petblocks_main_menu.yml",
                    "gui/petblocks_skins_menu.yml" to "petblocks_skins_menu.yml",
                    "gui/petblocks_skins_blockskins_menu.yml" to "petblocks_skins_blockskins_menu.yml",
                    "gui/petblocks_skins_petskins_menu.yml" to "petblocks_skins_petskins_menu.yml",
                    "gui/petblocks_skins_plushieskins_menu.yml" to "petblocks_skins_plushieskins_menu.yml",
                    "gui/petblocks_skins_vehicleskins_menu.yml" to "petblocks_skins_vehicleskins_menu.yml",
                    "gui/simple_sample_menu.yml" to "simple_sample_menu.yml"
                )
                this.baseCommand = "shygui"
                this.commandPermission = Permission.COMMAND.text
                this.otherPlayerPermission = Permission.OTHER_PLAYER.text
                this.guiPermission = Permission.DYN_OPEN.text
                this.baseCommand = "shygui"
                this.aliasesPath = "commands.shygui.aliases"
                this.commandUsage = ShyGUILanguage.commandUsage
                this.commandDescription = ShyGUILanguage.commandDescription

                this.playerNotFoundMessage = ShyGUILanguage.playerNotFoundMessage
                this.commandSenderHasToBePlayerMessage = ShyGUILanguage.commandSenderHasToBePlayer
                this.manipulateOtherMessage = ShyGUILanguage.manipulateOtherPlayerMessage
                this.reloadMessage = ShyGUILanguage.reloadMessage
                this.noPermissionMessage = ShyGUILanguage.commandNoPermission
                this.guiNotFoundMessage = ShyGUILanguage.guiMenuNotFoundMessage
                this.guiMenuNoPermissionMessage = ShyGUILanguage.guiMenuNoPermissionMessage

                this.reloadCommandHint = ShyGUILanguage.reloadCommandHint
                this.openCommandHint = ShyGUILanguage.openCommandHint
                this.nextCommandHint = ShyGUILanguage.nextCommandHint
                this.closeCommandHint = ShyGUILanguage.closeCommandHint
                this.backCommandHint = ShyGUILanguage.backCommandHint
                this.messageCommandHint = ShyGUILanguage.messageCommandHint
            }
        };
        settings.reload()
        this.shyGuiModule = ShyGUIDependencyInjectionModule(settings, this).build()

        // Register Language
        this.reloadConfig()
        val configurationService = shyGuiModule.getService<ConfigurationService>()
        val language = configurationService.findValue<String>("language")
        reloadTranslation(language, ShyGUILanguage::class.java, "en_us")
        settings.reload()
        logger.log(Level.INFO, "Loaded language file $language.properties.")

        // Register Packets
        val packetService = shyGuiModule.getService<PacketService>()
        packetService.registerPacketListening(PacketInType.CLICKINVENTORY)
        packetService.registerPacketListening(PacketInType.CLOSEINVENTORY)

        // Register Listeners
        Bukkit.getPluginManager().registerEvents(shyGuiModule.getService<GUIMenuListener>(), this)

        // Register CommandExecutor
        val commandExecutor = shyGuiModule.getService<ShyGUICommandExecutor>()
        commandExecutor.registerShyGuiCommand()

        // Register PlaceHolders
        val placeholderService = shyGuiModule.getService<PlaceHolderService>()
        placeholderService.registerProvider(ShyGUIPlaceHolderProvider(shyGuiModule.getService<Plugin>()))
        placeholderService.registerPlaceHolderApiProvider {
            ShyGUIPlaceHolderApiProvider(
                shyGuiModule.getService<Plugin>(),
                shyGuiModule.getService<PlaceHolderService>(), shyGuiModule.getService<GUIMenuService>()
            )
        }

        // Register Dependencies
        Bukkit.getServicesManager()
            .register(
                GUIMenuService::class.java,
                shyGuiModule.getService<GUIMenuService>(),
                this,
                ServicePriority.Normal
            )

        val plugin = this
        runBlocking {
            plugin.logger.log(Level.INFO, "Registering GUI commands...")
            commandExecutor.registerGuiCommands()
            plugin.logger.log(Level.INFO, "Registered GUI commands.")
            Bukkit.getServer().consoleSender.sendMessage(prefix + ChatColor.GREEN + "Enabled ShyGUI " + plugin.description.version + " by Shynixn")
        }
    }

    /**
     * Called when this plugin is disabled.
     */
    override fun onDisable() {
        if (immediateDisable) {
            return
        }

        val menuService = shyGuiModule.getService<GUIMenuService>()
        menuService.close()

        val packetService = shyGuiModule.getService<PacketService>()
        packetService.close()
    }
}
