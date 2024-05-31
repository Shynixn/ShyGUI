package com.github.shynixn.shygui

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mcutils.common.*
import com.github.shynixn.mcutils.common.item.Item
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.repository.Repository
import com.github.shynixn.mcutils.guice.DependencyInjectionModule
import com.github.shynixn.mcutils.packet.api.PacketInType
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.api.meta.enumeration.WindowType
import com.github.shynixn.mcutils.packet.api.packet.PacketOutInventoryContent
import com.github.shynixn.mcutils.packet.api.packet.PacketOutInventoryOpen
import com.github.shynixn.shygui.contract.GUIMenuService
import com.github.shynixn.shygui.entity.GUIMeta
import com.github.shynixn.shygui.impl.commandexecutor.ShyGUICommandExecutor
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

class ShyGUIPlugin : JavaPlugin() {
    private val prefix: String = ChatColor.BLUE.toString() + "[ShyGUI] " + ChatColor.WHITE
    private lateinit var module: DependencyInjectionModule
    private var immediateDisable = false

    /**
     * Called when this plugin is enabled.
     */
    override fun onEnable() {
        Bukkit.getServer().consoleSender.sendMessage(prefix + ChatColor.GREEN + "Loading ShyGUI ...")
        this.saveDefaultConfig()
        val versions = if (ShyGUIDependencyInjectionModule.areLegacyVersionsIncluded) {
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
        this.module = ShyGUIDependencyInjectionModule(this).build()
        this.reloadConfig()

        // Register Language
        val configurationService = module.getService<ConfigurationService>()
        val language = configurationService.findValue<String>("language")
        reloadTranslation(language, ShyGUILanguage::class.java, "en_us")
        logger.log(Level.INFO, "Loaded language file $language.properties.")

        // Register Packets
        val packetService = module.getService<PacketService>()
        packetService.registerPacketListening(PacketInType.CLICKINVENTORY)

        // Register Listeners

        // Register CommandExecutor
        module.getService<ShyGUICommandExecutor>()

        // Register Dependencies
        Bukkit.getServicesManager()
            .register(GUIMenuService::class.java, module.getService<GUIMenuService>(), this, ServicePriority.Normal)

        val plugin = this
        plugin.launch {
            val metaRepository = module.getService<Repository<GUIMeta>>()
            metaRepository.getAll()


            val player = Bukkit.getPlayer("Shynixn")!!

            val item = Item(typeName = "minecraft:iron_shovel")
            val itemService = module.getService<ItemService>()
            val containerId = packetService.getNextContainerId(player)

            packetService.sendPacketOutInventoryOpen(player, PacketOutInventoryOpen().also {
                it.title = "Test Inventory"
                it.windowType = WindowType.SIX_ROW
                it.containerId = containerId
            })

            delay(5000)

            packetService.sendPacketOutInventoryContent(player, PacketOutInventoryContent().also {
                it.containerId = containerId
                it.stateId = 1
                it.items = listOf(itemService.toItemStack(item))
            })











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

        val menuService = module.getService<GUIMenuService>()
        menuService.close()

        val packetService = module.getService<PacketService>()
        packetService.close()
    }
}
