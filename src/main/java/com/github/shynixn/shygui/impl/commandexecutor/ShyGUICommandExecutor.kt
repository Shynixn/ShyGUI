package com.github.shynixn.shygui.impl.commandexecutor

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.CoroutineExecutor
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.command.CommandBuilder
import com.github.shynixn.mcutils.common.command.Validator
import com.github.shynixn.mcutils.common.repository.CacheRepository
import com.github.shynixn.shygui.ShyGUILanguage
import com.github.shynixn.shygui.contract.GUIMenuService
import com.github.shynixn.shygui.entity.GUIMeta
import com.github.shynixn.shygui.enumeration.Permission
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*
import java.util.logging.Level

class ShyGUICommandExecutor @Inject constructor(
    private val baseCommand : String,
    private val aliasesPath : String,
    private val plugin: Plugin,
    private val guiMenuService: GUIMenuService,
    private val chatMessageService: ChatMessageService,
    private val repository: CacheRepository<GUIMeta>,
    private val configurationService: ConfigurationService
) {
    private val coroutineExecutor = object : CoroutineExecutor {
        override fun execute(f: suspend () -> Unit) {
            plugin.launch(plugin.globalRegionDispatcher) {
                f.invoke()
            }
        }
    }

    private val onlinePlayerTabs: (suspend (CommandSender) -> List<String>) = {
        Bukkit.getOnlinePlayers().map { e -> e.name }
    }
    private val paramOrOnlinePlayerTabs: (suspend (CommandSender) -> List<String>) = {
        val list = mutableListOf("<param>")
        list.addAll(Bukkit.getOnlinePlayers().map { e -> e.name })
        list
    }
    private val playerMustExist = object : Validator<Player> {
        override suspend fun transform(
            sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>
        ): Player? {
            try {
                val playerId = openArgs[0]
                val player = Bukkit.getPlayer(playerId)

                if (player != null) {
                    return player
                }
                return Bukkit.getPlayer(UUID.fromString(playerId))
            } catch (e: Exception) {
                return null
            }
        }

        override suspend fun message(sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>): String {
            return ShyGUILanguage.playerNotFoundMessage.format(openArgs[0])
        }
    }

    private val senderHasToBePlayer: () -> String = {
        ShyGUILanguage.commandSenderHasToBePlayer
    }

    private val menuTabs: (suspend (CommandSender) -> List<String>) = {
        repository.getAll().map { e -> e.name }
    }

    private val guiMenuMustExist = object : Validator<GUIMeta> {
        override suspend fun transform(
            sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>
        ): GUIMeta? {
            return repository.getAll().firstOrNull { e -> e.name.equals(openArgs[0], true) }
        }

        override suspend fun message(sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>): String {
            return ShyGUILanguage.guiMenuNotFoundMessage.format(openArgs[0])
        }
    }

    private val remainingArguments = object : Validator<List<String>> {
        override suspend fun transform(
            sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>
        ): List<String> {
            return openArgs
        }
    }

    private val guiMenuMustHavePermission = object : Validator<GUIMeta> {
        override suspend fun validate(
            sender: CommandSender, prevArgs: List<Any>, argument: GUIMeta, openArgs: List<String>
        ): Boolean {
            return sender.hasPermission(Permission.DYN_OPEN.text + argument.name)
        }

        override suspend fun message(sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>): String {
            return ShyGUILanguage.guiMenuNoPermissionMessage.format(openArgs[0])
        }
    }

    fun registerShyGuiCommand() {
        CommandBuilder(plugin, coroutineExecutor, baseCommand, chatMessageService) {
            usage(ShyGUILanguage.commandUsage)
            description(ShyGUILanguage.commandDescription)
            aliases(plugin.config.getStringList(aliasesPath))
            permission(Permission.COMMAND)
            permissionMessage(ShyGUILanguage.commandNoPermission)
            subCommand("open") {
                toolTip { ShyGUILanguage.openCommandHint }
                builder().argument("menu").validator(guiMenuMustExist).validator(guiMenuMustHavePermission)
                    .tabs(menuTabs).executePlayer(senderHasToBePlayer) { player, guiMeta ->
                        plugin.launch {
                            guiMenuService.clearCache(player)
                            openGUI(player, guiMeta, emptyArray())
                        }
                    }.argument("arg/player..").validator(remainingArguments).tabs(paramOrOnlinePlayerTabs)
                    .execute { commandSender, guiMeta, remainingArgs ->
                        val playerWithArg = locatePlayerAndArguments(commandSender, remainingArgs) ?: return@execute
                        val player = playerWithArg.second ?: return@execute
                        val arguments = playerWithArg.first
                        plugin.launch {
                            guiMenuService.clearCache(player)
                            openGUI(player, guiMeta, arguments)
                        }
                    }
            }
            subCommand("close") {
                toolTip { ShyGUILanguage.closeCommandHint }
                builder().executePlayer(senderHasToBePlayer) { player ->
                    plugin.launch {
                        guiMenuService.getGUI(player)?.closeAll()
                    }
                }.argument("player").validator(playerMustExist).tabs(onlinePlayerTabs)
                    .permission { Permission.OTHER_PLAYER.text }.execute { _, player ->
                        plugin.launch {
                            guiMenuService.getGUI(player)?.closeAll()
                        }
                    }
            }
            subCommand("next") {
                toolTip {
                    ShyGUILanguage.nextCommandHint
                }
                builder().argument("menu").validator(guiMenuMustExist).validator(guiMenuMustHavePermission)
                    .tabs(menuTabs).executePlayer(senderHasToBePlayer) { player, guiMeta ->
                        plugin.launch {
                            openGUI(player, guiMeta, emptyArray())
                        }
                    }.argument("arg/player..").validator(remainingArguments).tabs(paramOrOnlinePlayerTabs)
                    .execute { commandSender, guiMeta, remainingArgs ->
                        val playerWithArg = locatePlayerAndArguments(commandSender, remainingArgs) ?: return@execute
                        val player = playerWithArg.second ?: return@execute
                        val arguments = playerWithArg.first
                        plugin.launch {
                            openGUI(player, guiMeta, arguments)
                        }
                    }
            }
            subCommand("back") {
                toolTip {
                    ShyGUILanguage.backCommandHint
                }
                builder().executePlayer(senderHasToBePlayer) { player ->
                    plugin.launch {
                        guiMenuService.getGUI(player)?.closeBack()
                    }
                }.argument("player").validator(playerMustExist).tabs(onlinePlayerTabs)
                    .permission { Permission.OTHER_PLAYER.text }.execute { _, player ->
                        plugin.launch {
                            guiMenuService.getGUI(player)?.closeBack()
                        }
                    }
            }
            subCommand("reload") {
                toolTip {
                    ShyGUILanguage.reloadCommandHint
                }
                builder().execute { sender ->
                    guiMenuService.close()
                    plugin.saveDefaultConfig()
                    plugin.reloadConfig()
                    configurationService.reload()
                    repository.clearCache()
                    sender.sendMessage(ShyGUILanguage.reloadMessage)
                }
            }.helpCommand()
        }.build()
    }

    suspend fun registerGuiCommands() {
        val guiMenus = repository.getAll()
        for (guiMenu in guiMenus) {
            if (guiMenu.command.command.isBlank()) {
                continue
            }

            val command = guiMenu.command
            plugin.logger.log(Level.INFO, "Registered command '/${command.command}' for GUI '${guiMenu.name}'.")
        }
    }

    private fun openGUI(player: Player, guiMeta: GUIMeta, arguments: Array<String>) {
        guiMenuService.openGUI(player, guiMeta, arguments)
    }

    private fun locatePlayerAndArguments(
        sender: CommandSender,
        remainArgs: List<String>
    ): Pair<Array<String>, Player?>? {
        val playerResult = try {
            val playerId = remainArgs[remainArgs.size - 1]
            Bukkit.getPlayer(playerId) ?: Bukkit.getPlayer(UUID.fromString(playerId))!!
        } catch (e: Exception) {
            if (sender is Player) {
                sender
            } else {
                null
            }
        }

        if (playerResult == null && sender !is Player) {
            sender.sendMessage(ShyGUILanguage.commandSenderHasToBePlayer)
            return null
        }

        if (playerResult != sender && !sender.hasPermission(Permission.OTHER_PLAYER.text)) {
            sender.sendMessage(ShyGUILanguage.manipulateOtherPlayerMessage)
            return null
        }

        if (playerResult != null) {
            return Pair(remainArgs.dropLast(1).toTypedArray(), playerResult)
        }

        return Pair(remainArgs.toTypedArray(), null)
    }

    private fun CommandBuilder.permission(permission: Permission) {
        this.permission(permission.text)
    }
}