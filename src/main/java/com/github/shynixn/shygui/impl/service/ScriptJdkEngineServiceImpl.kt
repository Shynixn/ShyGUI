package com.github.shynixn.shygui.impl.service

import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.shygui.contract.ScriptService
import com.google.inject.Inject
import org.bukkit.plugin.Plugin
import java.util.logging.Level
import javax.script.Compilable
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

class ScriptJdkEngineServiceImpl @Inject constructor(
    private val plugin: Plugin,
    configurationService: ConfigurationService
) : ScriptService {

    private val scriptEngine: ScriptEngine
    private val compileAbleScriptEngine: Compilable

    init {
        val options = configurationService.findValue<List<String>>("scriptEngine.options")
        this.scriptEngine = ScriptEngineManager().getEngineByName("nashorn");
        this.scriptEngine.put(ScriptEngine.ARGV, options.toTypedArray())
        this.compileAbleScriptEngine = this.scriptEngine as Compilable
    }

    /**
     * Evaluates a Javascript expression.
     */
    override fun evaluate(expression: String): Any? {
        return try {
            // Script Engine is thread safe.
            scriptEngine.eval(expression)
        } catch (e: Exception) {
            plugin.logger.log(Level.SEVERE, "Cannot evaluate expression '$expression'.", e)
            false
        }
    }
}
