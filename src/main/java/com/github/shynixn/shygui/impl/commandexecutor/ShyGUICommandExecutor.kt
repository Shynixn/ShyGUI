package com.github.shynixn.shygui.impl.commandexecutor

import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.CoroutinePlugin
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.command.CommandBuilder
import com.github.shynixn.mcutils.common.command.Validator
import com.github.shynixn.mcutils.common.language.LanguageItem
import com.github.shynixn.mcutils.common.language.reloadTranslation
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.repository.CacheRepository
import com.github.shynixn.mcutils.common.translateChatColors
import com.github.shynixn.shygui.contract.GUIMenuService
import com.github.shynixn.shygui.contract.ShyGUILanguage
import com.github.shynixn.shygui.entity.GUIMeta
import com.github.shynixn.shygui.entity.ShyGUISettings
import com.github.shynixn.shygui.enumeration.Permission
import com.google.common.io.ByteStreams
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*
import java.util.logging.Level


class ShyGUICommandExecutor(
    private val settings: ShyGUISettings,
    private val plugin: CoroutinePlugin,
    private val guiMenuService: GUIMenuService,
    private val chatMessageService: ChatMessageService,
    private val repository: CacheRepository<GUIMeta>,
    private val configurationService: ConfigurationService,
    private val language: ShyGUILanguage,
    private val placeHolderService: PlaceHolderService
) {
    private val onlinePlayerTabs: ((CommandSender) -> List<String>) = {
        Bukkit.getOnlinePlayers().map { e -> e.name }
    }
    private val paramOrOnlinePlayerTabs: ((CommandSender) -> List<String>) = {
        val list = mutableListOf("<param>")
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
            return placeHolderService.resolvePlaceHolder(
                language.shyGuiPlayerNotFoundMessage.text,
                null,
                mapOf("0" to openArgs[0])
            )
        }
    }

    private val senderHasToBePlayer: () -> String = {
        language.shyGuiCommandSenderHasToBePlayer.text
    }

    private val menuTabs: (CommandSender) -> List<String> = {
        val cache = repository.getCache()
        cache?.map { e -> e.name } ?: emptyList()
    }

    private val guiMenuMustExist = object : Validator<GUIMeta> {
        override suspend fun transform(
            sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>
        ): GUIMeta? {
            return repository.getAll().firstOrNull { e -> e.name.equals(openArgs[0], true) }
        }

        override suspend fun message(sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>): String {
            return placeHolderService.resolvePlaceHolder(
                language.shyGuiGuiMenuNotFoundMessage.text,
                null,
                mapOf("0" to openArgs[0])
            )
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
            return sender.hasPermission(settings.guiPermission + argument.name)
        }

        override suspend fun message(sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>): String {
            return placeHolderService.resolvePlaceHolder(
                language.shyGuiGuiMenuNoPermissionMessage.text,
                null,
                mapOf("0" to openArgs[0])
            )
        }
    }

    fun registerShyGuiCommand() {
        CommandBuilder(plugin, settings.baseCommand, chatMessageService) {
            usage(language.shyGuiCommandUsage.text)
            description(language.shyGuiCommandDescription.text)
            aliases(plugin.config.getStringList(settings.aliasesPath))
            permission(settings.commandPermission)
            permissionMessage(language.shyGuiNoPermissionCommand.text)
            subCommand("open") {
                toolTip { language.shyGuiOpenCommandHint.text }
                builder().argument("menu").validator(guiMenuMustExist).validator(guiMenuMustHavePermission)
                    .tabs(menuTabs).executePlayer(senderHasToBePlayer) { player, guiMeta ->
                        plugin.launch {
                            guiMenuService.clearCache(player)
                            openGUI(player, guiMeta, emptyArray())
                        }
                    }.argument("args.../player").validator(remainingArguments).tabs(paramOrOnlinePlayerTabs)
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
                toolTip { language.shyGuiCloseCommandHint.text }
                builder().executePlayer(senderHasToBePlayer) { player ->
                    plugin.launch {
                        guiMenuService.getGUI(player)?.closeAll()
                    }
                }.argument("player").validator(playerMustExist).tabs(onlinePlayerTabs)
                    .permission { settings.otherPlayerPermission }.execute { _, player ->
                        plugin.launch {
                            guiMenuService.getGUI(player)?.closeAll()
                        }
                    }
            }
            subCommand("next") {
                toolTip {
                    language.shyGuiNextCommandHint.text
                }
                builder().argument("menu").validator(guiMenuMustExist).validator(guiMenuMustHavePermission)
                    .tabs(menuTabs).executePlayer(senderHasToBePlayer) { player, guiMeta ->
                        plugin.launch {
                            openGUI(player, guiMeta, emptyArray())
                        }
                    }.argument("args.../player").validator(remainingArguments).tabs(paramOrOnlinePlayerTabs)
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
                    language.shyGuiBackCommandHint.text
                }
                builder().executePlayer(senderHasToBePlayer) { player ->
                    plugin.launch {
                        guiMenuService.getGUI(player)?.closeBack()
                    }
                }.argument("player").validator(playerMustExist).tabs(onlinePlayerTabs)
                    .permission { settings.otherPlayerPermission }.execute { _, player ->
                        plugin.launch {
                            guiMenuService.getGUI(player)?.closeBack()
                        }
                    }
            }
            subCommand("message") {
                toolTip {
                    language.shyGuiMessageCommandHint.text
                }
                builder().argument("args.../player").validator(remainingArguments).tabs(paramOrOnlinePlayerTabs)
                    .execute { commandSender, remainingArgs ->
                        val playerWithArg = locatePlayerAndArguments(commandSender, remainingArgs) ?: return@execute
                        val player = playerWithArg.second ?: return@execute
                        val arguments = playerWithArg.first
                        val finalString = arguments.joinToString(" ").translateChatColors()
                        player.sendMessage(finalString)
                    }
            }
            subCommand("refresh") {
                permission(settings.refreshPermission)
                toolTip {
                    language.shyGuiRefreshCommandHint.text
                }
                builder().executePlayer(senderHasToBePlayer) { player ->
                    plugin.launch {
                        guiMenuService.getGUI(player)?.refresh()
                    }
                }.argument("player").validator(playerMustExist).tabs(onlinePlayerTabs)
                    .permission { settings.otherPlayerPermission }.execute { _, player ->
                        plugin.launch {
                            guiMenuService.getGUI(player)?.refresh()
                        }
                    }
            }
            subCommand("server") {
                permission(settings.serverPermission)
                toolTip {
                    language.shyGuiServerCommandHint.text
                }
                builder().argument("server").executePlayer(senderHasToBePlayer) { player, server ->
                    sendPlayerToServer(player, player, server)
                }.argument("player").validator(playerMustExist).tabs(onlinePlayerTabs)
                    .permission { settings.otherPlayerPermission }.execute { sender,  server, player ->
                        sendPlayerToServer(sender, player, server)
                    }
            }
            subCommand("reload") {
                permission(settings.otherPlayerPermission)
                toolTip {
                    language.shyGuiReloadCommandHint.text
                }
                builder().execute { sender ->
                    guiMenuService.close()
                    plugin.saveDefaultConfig()
                    plugin.reloadConfig()
                    plugin.reloadTranslation(language)
                    configurationService.reload()
                    repository.clearCache()
                    sender.sendLanguageMessage(language.shyGuiReloadMessage)
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
            CommandBuilder(plugin, command.command, chatMessageService) {
                usage(command.usage)
                description(command.description)
                aliases(command.aliases)
                permission(command.permission)
                permissionMessage(language.shyGuiNoPermissionCommand.text)
                builder().executePlayer(senderHasToBePlayer) { player ->
                    plugin.launch(plugin.globalRegionDispatcher) {
                        Bukkit.getServer().dispatchCommand(
                            Bukkit.getConsoleSender(),
                            "${settings.baseCommand} open ${guiMenu.name} / ${player.name}"
                        )
                    }
                }
            }.build()

            plugin.logger.log(Level.INFO, "Registered command '/${command.command}' for GUI '${guiMenu.name}'.")
        }
    }

    private suspend fun openGUI(player: Player, guiMeta: GUIMeta, arguments: Array<String>) {
        guiMenuService.openGUI(player, guiMeta, arguments)
    }

    private fun sendPlayerToServer(sender: CommandSender, player: Player, server: String) {
        val out = ByteStreams.newDataOutput()
        out.writeUTF("Connect")
        out.writeUTF(server)
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray())
        sender.sendLanguageMessage(language.shyGuiServerMessage, server)
    }

    private fun locatePlayerAndArguments(
        sender: CommandSender, remainArgs: List<String>
    ): Pair<Array<String>, Player?>? {
        val arguments = ArrayList<String>()
        var i = 0

        while (i < remainArgs.size) {
            val arg = remainArgs[i]
            i++
            if (arg == "/") {
                break
            }
            arguments.add(arg)
        }

        val playerResult = if (i < remainArgs.size) {
            try {
                val playerId = remainArgs[i]
                Bukkit.getPlayer(playerId) ?: Bukkit.getPlayer(UUID.fromString(playerId))!!
            } catch (e: Exception) {
                if (sender is Player) {
                    sender
                } else {
                    null
                }
            }
        } else if (sender is Player) {
            sender
        } else {
            null
        }

        if (playerResult == null) {
            sender.sendLanguageMessage(language.shyGuiCommandSenderHasToBePlayer)
            return null
        }

        if (playerResult != sender && !sender.hasPermission(settings.otherPlayerPermission)) {
            sender.sendLanguageMessage(language.shyGuiManipulateOtherMessage)
            return null
        }

        return Pair(arguments.toTypedArray(), playerResult)
    }

    private fun CommandSender.sendLanguageMessage(languageItem: LanguageItem, vararg args: String) {
        val sender = this
        plugin.launch(plugin.globalRegionDispatcher) {
            chatMessageService.sendLanguageMessage(sender, languageItem, *args)
        }
    }
}
