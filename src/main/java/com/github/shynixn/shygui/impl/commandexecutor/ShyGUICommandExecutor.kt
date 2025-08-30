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
                language.playerNotFoundMessage.text,
                null,
                mapOf("0" to openArgs[0])
            )
        }
    }

    private val senderHasToBePlayer: () -> String = {
        language.commandSenderHasToBePlayer.text
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
                language.guiMenuNotFoundMessage.text,
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
                language.guiMenuNoPermissionMessage.text,
                null,
                mapOf("0" to openArgs[0])
            )
        }
    }

    fun registerShyGuiCommand() {
        CommandBuilder(plugin, settings.baseCommand, chatMessageService) {
            usage(language.commandUsage.text)
            description(language.commandDescription.text)
            aliases(plugin.config.getStringList(settings.aliasesPath))
            permission(settings.commandPermission)
            permissionMessage(language.noPermissionCommand.text)
            subCommand("open") {
                toolTip { language.openCommandHint.text }
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
                toolTip { language.closeCommandHint.text }
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
                    language.nextCommandHint.text
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
                    language.backCommandHint.text
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
                    language.messageCommandHint.text
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
            subCommand("reload") {
                permission(settings.otherPlayerPermission)
                toolTip {
                    language.reloadCommandHint.text
                }
                builder().execute { sender ->
                    guiMenuService.close()
                    plugin.saveDefaultConfig()
                    plugin.reloadConfig()
                    plugin.reloadTranslation(language)
                    configurationService.reload()
                    repository.clearCache()
                    sender.sendLanguageMessage(language.reloadMessage)
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
                permissionMessage(language.noPermissionCommand.text)
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
            sender.sendLanguageMessage(language.commandSenderHasToBePlayer)
            return null
        }

        if (playerResult != sender && !sender.hasPermission(settings.otherPlayerPermission)) {
            sender.sendLanguageMessage(language.manipulateOtherMessage)
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
