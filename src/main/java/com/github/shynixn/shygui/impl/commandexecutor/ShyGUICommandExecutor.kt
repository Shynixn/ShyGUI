package com.github.shynixn.shygui.impl.commandexecutor

import com.github.shynixn.mccoroutine.folia.launch
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

class ShyGUICommandExecutor @Inject constructor(
    private val plugin: Plugin,
    private val guiMetaService: CacheRepository<GUIMeta>,
    private val guiMenuService: GUIMenuService,
    private val chatMessageService: ChatMessageService
) {
    private val menuTabs: (suspend (CommandSender) -> List<String>) = {
        guiMetaService.getAll().map { e -> e.name }
    }
    private val onlinePlayerTabs: (suspend (CommandSender) -> List<String>) = {
        Bukkit.getOnlinePlayers().map { e -> e.name }
    }

    private val guiMenuMustExist = object : Validator<GUIMeta> {
        override suspend fun transform(
            sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>
        ): GUIMeta? {
            return guiMetaService.getAll().firstOrNull { e -> e.name.equals(openArgs[0], true) }
        }

        override suspend fun message(sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>): String {
            return ShyGUILanguage.guiMenuNoPermissionMessage.format(openArgs[0])
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

    init {
        CommandBuilder(plugin, "shygui", chatMessageService) {
            usage(ShyGUILanguage.commandUsage)
            description(ShyGUILanguage.commandDescription)
            aliases(plugin.config.getStringList("commands.shygui.aliases"))
            permission(Permission.COMMAND)
            permissionMessage(ShyGUILanguage.commandNoPermission)
            subCommand("open") {
                toolTip { ShyGUILanguage.openCommandHint }
                builder()
                    .argument("menu").validator(guiMenuMustExist).validator(guiMenuMustHavePermission)
                    .tabs(menuTabs)
                    .executePlayer(senderHasToBePlayer) { player, guiMeta ->
                        openGui(guiMeta, player)
                    }.argument("player").validator(playerMustExist).tabs(onlinePlayerTabs)
                    .execute { commandSender, guiMeta, player ->
                        openGui(guiMeta, player)
                    }
            }.helpCommand()
        }.build()
    }

    private suspend fun openGui(guiMeta: GUIMeta, player: Player) {
        plugin.launch {
            guiMenuService.openGUI(player, guiMeta)
        }
    }

    private fun CommandBuilder.permission(permission: Permission) {
        this.permission(permission.text)
    }
}
